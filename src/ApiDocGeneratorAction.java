import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.freeone.apidoc.entity.*;
import org.freeone.setting.JBean2TsBeanComponent;
import org.freeone.util.LogPanelUtil;
import org.freeone.util.NotificationUtil;
import org.freeone.util.PlatformUtil;
import org.freeone.util.TemplateUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ApiDocGeneratorAction extends AnAction {
    static  Project project = null;

    static String serverPathValue = null;

    static String jwtValue = null;

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        this.project = e.getProject();
        VirtualFile data = e.getData(PlatformDataKeys.PROJECT_FILE_DIRECTORY);
        String projectPath = data.getPath();

        JBean2TsBeanComponent instance = JBean2TsBeanComponent.getInstance();
        Map<String, String> apidocMap = instance.getApidocMap();
        if (apidocMap == null){
            Messages.showErrorDialog("无法获取相关配置", "异常操作");
            return;
        }

        String serverPath1 = apidocMap.get("serverPath");
        if(serverPath1 == null || "".equals(serverPath1)){
            Messages.showWarningDialog("请先设置服务器的接口路径", "提示");
            return;
        }
        ApiDocGeneratorAction.serverPathValue = serverPath1;
        String jwt = apidocMap.get("jwt");
        if (jwt == null || "".equals(jwt)){
            Messages.showWarningDialog("请先设置jwt认证参数", "提示");
            return;
        }
        ApiDocGeneratorAction.jwtValue = jwt;


        VirtualFile[] virtualFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (virtualFiles == null || virtualFiles.length == 0) {
            NotificationUtil.createNotification("无法获取文件或文件夹", NotificationUtil.ERROR);
            return;
        }

        boolean hasRequestFile = false;
        LogPanelUtil.clearTextArea(project);
        for (VirtualFile virtualFile : virtualFiles) {
            if (virtualFile.isDirectory()) {
                // 如果是一个文件夹
                String path = virtualFile.getPath();
                List<String> allJavaFilePath = TemplateUtil.getAllJavaFile(path, false);
                for (String javaFilePath : allJavaFilePath) {
                    if (javaFilePath.endsWith("Request.java")) {
                        hasRequestFile = true;
                        TbRequestClassEntity requestClassEntity = parseJavaRequestFileToServer(javaFilePath);
                        String responseFilePath = javaFilePath.replace("Request.java", "Response.java");
                        TbResponseClassEntity tbResponseClassEntity = parseJavaResponseFileToServer(responseFilePath);
                        if(null == requestClassEntity){
                            LogPanelUtil.writeInfo(project,virtualFile.getName()+"没有请求类，将跳过解析");
                            continue;
                        }
                        if(tbResponseClassEntity == null){
                            LogPanelUtil.writeInfo(project,requestClassEntity.getClassName()+"没有响应类，将跳过解析");
                            continue;
                        }
                        if(tbResponseClassEntity.getResponseFieldList() == null || tbResponseClassEntity.getResponseFieldList().isEmpty() ){
                            NotificationUtil.createNotification(requestClassEntity.getClassName()+"没有响应参数，请及时补充",NotificationUtil.WARNING);
                        }

                        DataBody dataBody = new DataBody();
                        dataBody.setRequestClass(requestClassEntity);
                        dataBody.setResponseClass(tbResponseClassEntity);
                        try {
                            commitToServer(dataBody);
                        } catch (JsonProcessingException jsonProcessingException) {
                            jsonProcessingException.printStackTrace();

                        }
                    }
                }
            }else{
                // TODO
                // 文件的路径

                String path = virtualFile.getPath();
                String name = virtualFile.getName();
                path = path.replace("\\","/");
                if (path.endsWith("Request.java") || path.endsWith("Response.java") ) {
                    if(path.endsWith("Response.java")){
                        path = path.replace("Response.java","Request.java");
                    }
                    hasRequestFile =true;
                    TbRequestClassEntity requestClassEntity = parseJavaRequestFileToServer(path);
                    if(null == requestClassEntity){
                        LogPanelUtil.writeInfo(project,name+"没有请求类，将跳过解析");
                        return;
                    }
                    path = path.replace("Request.java","Response.java");
                    TbResponseClassEntity tbResponseClassEntity = parseJavaResponseFileToServer(path);
                    if(tbResponseClassEntity == null){
                        LogPanelUtil.writeInfo(project,requestClassEntity.getClassName()+"没有响应类,请查看文件是否正确");
                        return;
                    }
                    if(tbResponseClassEntity.getResponseFieldList() == null || tbResponseClassEntity.getResponseFieldList().isEmpty() ){
                        NotificationUtil.createNotification(tbResponseClassEntity.getClassName()+"没有响应参数，请及时补充",NotificationUtil.WARNING);
                    }

                    DataBody dataBody = new DataBody();
                    dataBody.setRequestClass(requestClassEntity);
                    dataBody.setResponseClass(tbResponseClassEntity);
                    try {
                        commitToServer(dataBody);
                    } catch (JsonProcessingException jsonProcessingException) {
                        jsonProcessingException.printStackTrace();

                    }

                }else{
                    Messages.showInfoMessage("请选择Request或Response结尾的java文件","提示");
                    return;
                }
            }
        }

        if (!hasRequestFile){

            LogPanelUtil.writeInfo(project,"没有找到相关类，将跳过解析");
        }


    }

    public static TbRequestClassEntity parseJavaRequestFileToServer(String path) {
        path = path.replace("\\","/");
        try {
            TbRequestClassEntity requestClassEntity = new TbRequestClassEntity();
            File javaFile = new File(path);
            CompilationUnit parse = null;
            parse = JavaParser.parse(javaFile, "utf-8");
            List<TypeDeclaration> types = parse.getTypes();
            PackageDeclaration aPackage = parse.getPackage();
            String platform = PlatformUtil.getPlatform(aPackage);
            if (platform == null) {
                LogPanelUtil.writeInfo(project,path.substring(path.lastIndexOf("/") + 1, path.length()) + "无法识别平台");
                NotificationUtil.createStickyNotification(path.substring(path.lastIndexOf("/") + 1, path.length()) + "无法识别平台", NotificationUtil.ERROR);
                return null;
            }
            if (types != null && !types.isEmpty()) {
                ClassOrInterfaceDeclaration javaType = (ClassOrInterfaceDeclaration) types.get(0);
                // 获取java类的注释
                JavadocComment javaDoc = javaType.getJavaDoc();
                // java类的名字
                String name = javaType.getName();
                requestClassEntity.setPackagePath(aPackage.getName().toString()).setClassName(name).setDescription(javaDoc == null ? null : javaDoc.toString()).setPlatform(platform);
                List<BodyDeclaration> members = javaType.getMembers();
                List<TbRequestFieldEntity> fieldList = TemplateUtil.getRequestFieldListFromMembers(members);
                requestClassEntity.setRequestFieldList(fieldList);
                return requestClassEntity;
            } else {
                LogPanelUtil.writeInfo(project,path.substring(path.lastIndexOf("/") + 1, path.length()) + "无法识别类");
//                NotificationUtil.createStickyNotification(path.substring(path.lastIndexOf("/") + 1, path.length()) + "无法识别类", NotificationUtil.ERROR);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解析ResponseFile
     * @param path ResponseFile的路径
     * @return TbResponseClassEntity
     */
    public static TbResponseClassEntity parseJavaResponseFileToServer(String path){
        path = path.replace("\\","/");
        TbResponseClassEntity tbResponseClassEntity = new TbResponseClassEntity();
        File javaFile = new File(path);
        CompilationUnit parse = null;
        try {
            parse = JavaParser.parse(javaFile, "utf-8");
            List<TypeDeclaration> types = parse.getTypes();
            PackageDeclaration aPackage = parse.getPackage();
            String platform = PlatformUtil.getPlatform(aPackage);
            if (platform == null) {
                NotificationUtil.createStickyNotification(path.substring(path.lastIndexOf("/") + 1, path.length()) + "无法识别平台", NotificationUtil.ERROR);
                return null;
            }
            if (types != null && !types.isEmpty()) {
                ClassOrInterfaceDeclaration javaType = (ClassOrInterfaceDeclaration) types.get(0);
                // 获取java类的注释
                JavadocComment javaDoc = javaType.getJavaDoc();
                // java类的名字
                String name = javaType.getName();
                tbResponseClassEntity.setPackagePath(aPackage.getName().toString()).setClassName(name) .setDescription(javaDoc == null ? null : javaDoc.toString()).setPlatform(platform);
                List<BodyDeclaration> members = javaType.getMembers();
                if (members != null && !members.isEmpty()){
                    String fileName = javaFile.getName();
                    String replace = path.replace("/" + fileName, "");
                    List<TbResponseFieldEntity> responseFieldListFromMembers = TemplateUtil.getResponseFieldListFromMembers(members, replace);
                    tbResponseClassEntity.setResponseFieldList(responseFieldListFromMembers);
                }

                return tbResponseClassEntity;
            } else {
                NotificationUtil.createStickyNotification(path.substring(path.lastIndexOf("/") + 1, path.length()) + "无法识别类", NotificationUtil.ERROR);
            }


        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void commitToServer(DataBody dataBody ) throws JsonProcessingException {

        HttpPost httpPost = new HttpPost(serverPathValue);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.addHeader("Authorization",jwtValue);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(dataBody);
        StringEntity stringEntity = new StringEntity(s, "UTF-8");
        httpPost.setEntity(stringEntity);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            String toString = EntityUtils.toString(responseEntity);
            System.out.println(toString);
            LogPanelUtil.writeInfo(project,"-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            LogPanelUtil.writeInfo(project,"提交: " +dataBody.getRequestClass().getClassName() + " & " +dataBody.getResponseClass().getClassName() );
//            LogPanelUtil.writeInfo(project,"响应状态为: " +response.getStatusLine());
            LogPanelUtil.writeInfo(project,"响应内容为: " + toString);
            LogPanelUtil.writeInfo(project,"-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            /*if(response.getStatusLine().getStatusCode() == 500){
                LogPanelUtil.writeInfo(project,"服务器异常");
            }else if(response.getStatusLine().getStatusCode() == 200){
                if (responseEntity != null) {
//                    System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                    System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                }
            }*/

        } catch (Exception e) {
            LogPanelUtil.writeInfo(project,e.getClass().getName() +": " + e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*public static void main(String[] args) throws JsonProcessingException {
        String reqpath = "E:\\diandaxia\\common\\src\\main\\java\\com\\diandaxia\\common\\sdk\\pinduoduo\\PddOrderListGetRequest.java";
        String respath = "E:\\diandaxia\\common\\src\\main\\java\\com\\diandaxia\\common\\sdk\\pinduoduo\\PddOrderListGetResponse.java";
        reqpath = reqpath.replace("\\", "/");
        TbResponseClassEntity tbResponseClassEntity = ApiDocGeneratorAction.parseJavaResponseFileToServer(respath);
        ObjectMapper objectMapper = new ObjectMapper();

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8080/accept");
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        TbRequestClassEntity requestClassEntity = parseJavaRequestFileToServer(reqpath);
        DataBody dataBody = new DataBody();
        dataBody.setRequestClass(requestClassEntity);
        dataBody.setResponseClass(tbResponseClassEntity);
        String s = objectMapper.writeValueAsString(dataBody);
        StringEntity stringEntity = new StringEntity(s, "UTF-8");
        httpPost.setEntity(stringEntity);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if(response.getStatusLine().getStatusCode()==500){

            }
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}

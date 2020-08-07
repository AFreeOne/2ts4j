import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
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
import org.freeone.util.NotificationUtil;
import org.freeone.util.PlatformUtil;
import org.freeone.util.TemplateUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ApiDocGeneratorAction extends AnAction {
    Project project = null;

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        this.project = e.getProject();
        VirtualFile[] virtualFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (virtualFiles == null || virtualFiles.length == 0) {
            NotificationUtil.createNotification("无法获取文件或文件夹", NotificationUtil.ERROR);
            return;
        }
        for (VirtualFile virtualFile : virtualFiles) {
            if (virtualFile.isDirectory()) {
                // 如果是一个文件夹
                String path = virtualFile.getPath();
                List<String> allJavaFilePath = TemplateUtil.getAllJavaFile(path, false);
                for (String javaFilePath : allJavaFilePath) {
                    if (javaFilePath.endsWith("Request.java")) {
                        System.err.println(javaFilePath);
                        TbRequestClassEntity requestClassEntity = parseJavaRequestFileToServer(javaFilePath);

                    }else if(javaFilePath.endsWith("Response.java")){

                    }
                }
            }
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
                System.err.println(requestClassEntity);
                return requestClassEntity;
            } else {
                NotificationUtil.createStickyNotification(path.substring(path.lastIndexOf("/") + 1, path.length()) + "无法识别类", NotificationUtil.ERROR);
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

    public static void main(String[] args) throws JsonProcessingException {
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
    }
}

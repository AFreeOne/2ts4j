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
import org.freeone.apidoc.entity.TbRequestClassEntity;
import org.freeone.apidoc.entity.TbRequestFieldEntity;
import org.freeone.apidoc.entity.TbResponseClassEntity;
import org.freeone.apidoc.entity.TbResponseFieldEntity;
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
        VirtualFile[] datas = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (datas == null || datas.length == 0) {
            NotificationUtil.createNotification("无法获取文件或文件夹", NotificationUtil.ERROR);
            return;
        }
        for (VirtualFile virtualFile : datas) {
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
                    TemplateUtil.getResponseFieldListFromMembers(members);
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

    public static void main(String[] args) {
        String path = "E:\\diandaxia\\common\\src\\main\\java\\com\\diandaxia\\common\\sdk\\pinduoduo\\PddOrderListGetResponse.java";
        path = path.replace("\\", "/");


        ApiDocGeneratorAction.parseJavaResponseFileToServer(path);
    }
}

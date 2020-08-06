import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.TypeDeclaration;
import org.freeone.util.LogPanelUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ApiDocGeneratorAction extends AnAction {
    Project project = null;
    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        this.project = e.getProject();



    }

    public static void parseJavaFile(String path)   {
        try {
            File javaFile = new File(path);
            CompilationUnit parse = null;
            parse = JavaParser.parse(javaFile,"utf-8");
            List<TypeDeclaration> types = parse.getTypes();
            PackageDeclaration aPackage = parse.getPackage();

            if (types != null && !types.isEmpty()){
                ClassOrInterfaceDeclaration javaType = (ClassOrInterfaceDeclaration)types.get(0);
                // 获取java类的注释
                JavadocComment javaDoc = javaType.getJavaDoc();
                // java类的名字
                String name = javaType.getName();


            }else{
                // TODO 输出获取不到方法
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
            String path = "E:\\diandaxia\\common\\src\\main\\java\\com\\diandaxia\\common\\sdk\\pinduoduo\\PddOrderListGetRequest.java";
            path = path.replace("\\","/");
        ApiDocGeneratorAction.parseJavaFile(path);
    }
}

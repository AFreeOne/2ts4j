
import com.intellij.notification.EventLog;
import com.intellij.openapi.actionSystem.*;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectImpl;
import com.intellij.openapi.ui.Messages;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.openapi.wm.ToolWindow;

import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentFactory;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.*;

import org.apache.commons.collections.CollectionUtils;

import org.freeone.window.Console;
import org.freeone.window.MyToolWindow;
import org.jetbrains.annotations.NotNull;


import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FirstAction extends AnAction {
    Console console ;
    String beSeletedpath = null;

    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public FirstAction(){
        console = new Console();
        console.setVisible(true);
    }



    /**
     * 点击菜单就会执行这个
     * @param event
     */

    @Override
    public void actionPerformed(AnActionEvent event) {


        // TODO: insert action logic here

        System.err.println("-----------------------------------");
        Project project = event.getData(PlatformDataKeys.PROJECT);
        VirtualFile data = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        ToolWindow toolWindow = event.getData(PlatformDataKeys.TOOL_WINDOW);
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);



        if (data.isDirectory()){
            //  是文件夹
            FileChooserDescriptor singleFolderDescriptor = null;
         /*   if (path == null){
                singleFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();

            }else{

            }*/

            singleFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            singleFolderDescriptor.setTitle("请选择转换之后的存放路径");
            singleFolderDescriptor.setDescription("ts文件将保存在指定的目录中");

            System.err.println(project.getClass());
//            VirtualFile toSelect = LocalFileSystem.getInstance().findFileByPath(File.separator + "Users" + File.separator + "wengyongcheng" + File.separator + "swagger-html" + File.separator);

            VirtualFile toSelect = null;
            if (beSeletedpath != null){
                toSelect = LocalFileSystem.getInstance().findFileByPath(beSeletedpath);
            }

            VirtualFile newFileChooser =   FileChooser.chooseFile(singleFolderDescriptor, null,toSelect);

            String savePath = "";
            if (null == newFileChooser){
                Messages.showErrorDialog("请选择转换之后的文件夹", "错误");
                return;
            }else{
                savePath = newFileChooser.getPath();
                beSeletedpath = savePath;
            }



            String path = data.getPath();
            List<String> filePaths = Util.getAllJavaFile(path, true);
            Util.fileRelativeLevel.clear();
            for (String str:filePaths) {

                str = str.replace("\\","/");
                String fileRelativePath = (str.replace(path, ""));
                int i = Util.countMatches(fileRelativePath, "/");
                // 文件的层级
                if( fileRelativePath.endsWith(".java")){
                     Util.fileRelativeLevel.put(fileRelativePath,i);
                }
            }

            Messages.showMessageDialog(project, "转换成功", "操作",Messages.getInformationIcon());

            scheduledExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    if (console == null){
                        console = new Console();
                    }

                }
            });



            for (String filePath : filePaths) {
                filePath = filePath.replace("\\", "/");
                String temSavePath = filePath.replace(path, savePath);
                if (!temSavePath.endsWith(".java")){
                    // 文件夹
                    File file = new File(temSavePath);
                    file.mkdirs();
                }else{

                    // 截取多出来的那一段路径
                    String replace = filePath.replace(path, "");
                    //savePath 保存路径加上相对路径
                    try {
                        javaFileToTypescriptFile(filePath,savePath+replace.substring(0,replace.lastIndexOf("/")),path);
                        console.appendString(">" +filePath +"\n");
                    }catch (Exception e){
                        console.appendString(">" +filePath +"\n");
                    }

                }
            }


        }else if("java".equals(data.getExtension())){
            // 是个java文件

            console.setVisible(true);
            FileChooserDescriptor singleFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            singleFolderDescriptor.setTitle("请选择转换之后的存放路径");
            VirtualFile newFileChooser =   FileChooser.chooseFile(singleFolderDescriptor, null,null);


            String savePath = "";
            if (null == newFileChooser){
                Messages.showErrorDialog("请选择转换之后的文件夹", "错误");
                return;
            }else{
                savePath = newFileChooser.getPath();
            }

            String path = data.getPath();
            try {
                javaFileToTypescriptFile(path,savePath,null);
                console.appendString(">" +path +"\n");

            }catch (Exception e){
                console.appendString(">" +path +"  error\n");

            }



        }else {
            Messages.showErrorDialog("选java文件或文件夹", "异常操作");
        }


        console.closeInSecond(3);

    }


    @Override
    public void update(@NotNull AnActionEvent e) {

    }

    /**
     * java文件转typescrite文件
     * @param javaFilePath java文件的路径
     * @param savePath  保存路径
     * @param parentPath 父路径，传入此参数说明是将文件夹内的java文件全部转换成ts文件
     */
    private static void javaFileToTypescriptFile(String javaFilePath,String savePath,String parentPath ){
        try {

            // 用于储存字段的名字和类型
            Map<String,String> fieldMap = new LinkedHashMap<>();

            javaFilePath.replace("file://", "");
            File javaFile = new File(javaFilePath);

            String parent = javaFile.getParent();
            File file = new File(parent);
            File[] files = file.listFiles();
            // 当前文件夹下的所有java类
            Set<String> javaClassesInCurrentFolder = new HashSet<>();
            if (files != null){
                for (int i = 0; i < files.length; i++) {
                    if(files[i].isFile() && files[i].getName().endsWith(".java")){
                        javaClassesInCurrentFolder.add(files[i].getName().replace(".java", ""));
                    }
                }
            }

            // 解析java文件获取解析之后的对象
            CompilationUnit parse = JavaParser.parse(javaFile,"utf-8");
            StringBuilder typeScriptFileContent = new StringBuilder();
            // 获取包路径
            PackageDeclaration aPackage = parse.getPackage();
            String packageString = "";
            if(aPackage != null){
                NameExpr name = aPackage.getName();
                if(name != null ){
                    packageString = name.toString();
                }
            }

            // 已经被引入的java class
            List<String> javaClassHasImported = new ArrayList<>();
            // 一般import处理，处理的是该包下子包的类的import
            List<ImportDeclaration> imports = parse.getImports();



            List<TypeDeclaration> types = parse.getTypes();
            // 获取第一个class
            ClassOrInterfaceDeclaration javaClass = (ClassOrInterfaceDeclaration)types.get(0);
            // 临时的成员，用于查找属性，用户辨认是否在当前文件
            List<BodyDeclaration> temMembers = javaClass.getMembers();
            // 获取members中所有的java类型,判断并在import时导入相关类
            Set<String> javaTypeInMembers = Util.getAllFieldJavaTypeInMembers(temMembers);
            //  获取继承中的java类型
            Set<String> javaTypeInExtends = Util.getJavaTypeInExtends(javaClass.getExtends());
            // 添加其中一起判断和导入
            javaTypeInMembers.addAll(javaTypeInExtends);
            // 获取泛型中的java类型
            Set<String> javaTypeInTypeParameters = Util.getJavaTypeInTypeParameters(javaClass.getTypeParameters());
            javaTypeInMembers.addAll(javaTypeInTypeParameters);


            final String tempPackageString = packageString;
            // 不同路径引入
            javaTypeInMembers.forEach(javaTypeName ->{
                if (javaClassHasImported.indexOf(javaTypeName) == -1){
                    String importInDifferentFolder = Util.getImportInDifferentFolder(javaFilePath, javaTypeName,  tempPackageString ,imports);
                    if(importInDifferentFolder != null){
                        javaClassHasImported.add(javaTypeName);
                        typeScriptFileContent.append(importInDifferentFolder);
                    }
                }
            });



            // 先获取所有的字段
            Set<String> allField = Util.getAllField(temMembers);
            typeScriptFileContent.append("\n");
            // 注释
            JavadocComment classJavaDoc = javaClass.getJavaDoc();
            if (classJavaDoc != null){
                typeScriptFileContent.append(classJavaDoc.toString());
            }

            String javaClassName = javaClass.getName();
            int classModifiers = javaClass.getModifiers();
            // public :1  默认 0 ，private: 2, protected: 4 public abstract :1025 ,abstract = 1024

            if(classModifiers == 1){
                // public 类什么都不放
                typeScriptFileContent.append("");
            }else if(classModifiers == 1024 || classModifiers == 1025){
                typeScriptFileContent.append("abstract ");
            }
            Class<InternalError> Intergt = null;

            if (javaClass.isInterface()){
                typeScriptFileContent.append("interface " + javaClassName+ " {\n");
            }else{
                typeScriptFileContent.append("class " + javaClassName+ " ");
                // 泛型
                List<TypeParameter> typeParameters = javaClass.getTypeParameters();
                if(typeParameters != null){
                    String typeParameterString = typeParameters.get(0).toString();
                    typeScriptFileContent.append("<"+typeParameterString+">");

                }
                // 继承
                List<ClassOrInterfaceType> anExtends = javaClass.getExtends();
                if (anExtends != null){
                    String extendsString = anExtends.get(0).toString();
                    typeScriptFileContent.append(" extends "+ extendsString);
                }


                typeScriptFileContent.append("{\n");
            }

            List<BodyDeclaration> members = javaClass.getMembers();



            members.forEach(member -> {
                // member是字段
                if(member instanceof FieldDeclaration){
                    FieldDeclaration field = (FieldDeclaration)member;
                    String templateFromField = getTemplateFromField(field, fieldMap);
                    typeScriptFileContent.append(templateFromField);

                }else if( member instanceof MethodDeclaration){

                    // member是方法
                    MethodDeclaration method = (MethodDeclaration)member;
                    String templateFromMethod = getTemplateFromMethod(method,allField);
                    typeScriptFileContent.append(templateFromMethod);
                }
            });

            String constructorTemplate = Util.getConstructorTemplate(fieldMap,javaClass.getExtends() != null);
            typeScriptFileContent.append(constructorTemplate);

            typeScriptFileContent.append("}\n");
            typeScriptFileContent.append("export = "+javaClassName+";");

           String typeScriptFileSavePath  = savePath  + "/"+javaClassName+".ts";
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(typeScriptFileSavePath,true),"utf-8"));
            bufferedWriter.write(typeScriptFileContent.toString());
            bufferedWriter.close();

        } catch ( Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 从字段中获取ts内容
     * @param field
     * @return
     */
    public static String getTemplateFromField(FieldDeclaration field, Map<String,String> fieldMap){
        StringBuilder fieldTemplate = new StringBuilder();
        // 注释
        JavadocComment javaDoc = field.getJavaDoc();
        if (javaDoc != null){
            fieldTemplate.append("    "+javaDoc.toString());
        }
        // 字段权限
        int modifiers = field.getModifiers();
        if (modifiers == 1){
            fieldTemplate.append("    public ");
        }else if(modifiers == 2){
            fieldTemplate.append("    private ");
        }else if(modifiers == 4){
            fieldTemplate.append("    protected ");
        }else{
            fieldTemplate.append("    ");
        }
        // 字段名称
        List<VariableDeclarator> variables = field.getVariables();
        VariableDeclarator variable = variables.get(0);
        String name = variable.getId().getName();
        fieldTemplate.append(name + ": ");
        // 字段类型
        String typeName = "";
        int arrayCount = 0;


        ClassOrInterfaceType classOrInterfaceType = null;

        if (field.getType() instanceof ReferenceType){
            ReferenceType type = (ReferenceType)field.getType();
            classOrInterfaceType = (ClassOrInterfaceType)type.getType();
            // 获得类型的名称 String还是Inter之类的
            typeName = classOrInterfaceType.getName();
            arrayCount = type.getArrayCount();
        }else{
            PrimitiveType primitiveType = (PrimitiveType) field.getType();
            typeName = primitiveType.toString();
//            arrayCount = primitiveType.ge
        }


        // arrayCount == 1 是数组[]
        if (arrayCount == 1){
            fieldTemplate.append("Array<"+Util.getTypeScriptDataType(typeName)+">;\n");
            fieldMap.put(name,"Array<"+Util.getTypeScriptDataType(typeName)+">");
        }else if(arrayCount == 0){
            // 可能是List，也是能是基础数据类型
            if("List".equals(typeName)){
                if (field.getType() instanceof ReferenceType){
                    String string = classOrInterfaceType.toString();
                    String replace = string.replace("List<", "Array<");
                    fieldTemplate.append(replace+";\n");
                    fieldMap.put(name,replace);
                }else{
                    String string = classOrInterfaceType.toString();
                    String replace = string.replace("List<", "Array<");
                    fieldTemplate.append(replace+";\n");
                    fieldMap.put(name,replace);
                }

            }else if("Map".equals(typeName)){
                List<Type> typeArgs = classOrInterfaceType.getTypeArgs();
                if(classOrInterfaceType != null){
                    String keyType = Util.getReturnType(typeArgs.get(0));
                    String valueType = Util.getReturnType(typeArgs.get(1));
                    fieldTemplate.append("Map<"+keyType+","+valueType+">");
                    fieldTemplate.append(";\n");
                    fieldMap.put(name,"Map<"+keyType+","+valueType+">");
                }
            }else if("Set".equals(typeName)) {
                List<Type> typeArgs = classOrInterfaceType.getTypeArgs();
                if(classOrInterfaceType != null){
                    String keyType = Util.getReturnType(typeArgs.get(0));
                    fieldTemplate.append("Set<"+keyType+">");
                    fieldTemplate.append(";\n");
                    fieldMap.put(name,"Set<"+keyType+">");
                }
            } else {
                // 基础数据类型
                String typeScriptDataType = Util.getTypeScriptDataType(typeName);
                fieldTemplate.append(typeScriptDataType+";\n");
                fieldMap.put(name,typeScriptDataType);
            }
        }
        return fieldTemplate.toString();
    }


    /**
     * 从方法中获取ts内容
     * @param method
     * @return
     */
    public static String getTemplateFromMethod(MethodDeclaration method, Set<String> allFields){
        StringBuilder methodTemplate = new StringBuilder();
        // 注释
        JavadocComment javaDoc = method.getJavaDoc();
        if (javaDoc != null){
            methodTemplate.append("    "+javaDoc.toString());
        }
        // 字段权限
        int modifiers = method.getModifiers();
        if(modifiers == 0){
            // 默认的
            methodTemplate.append("    ");
        } else  if (modifiers == 1){
            methodTemplate.append("public ");
        }else if(modifiers == 2){
            methodTemplate.append("private ");
        }else if(modifiers == 1024 || modifiers == 1025){
            methodTemplate.append("abstract ");
        }
        // 方法名
        String name = method.getName();
        methodTemplate.append(name+" ");
        // 获取参数
        List<Parameter> parameters = method.getParameters();
        List<String> allParameterName = new LinkedList<>();
        if (parameters == null ){
            methodTemplate.append("(): ");

        }else{
            List<String> parametersList = new LinkedList<>();
            parameters.forEach(parameter -> {
                String parameterType = Util.getReturnType(parameter.getType());
                String parameterName = parameter.getId().getName();
                allParameterName.add(parameterName);
                parametersList.add(parameterName + ": "+parameterType);
            });
            String parametersJoin = String.join(", ", parametersList);
            methodTemplate.append("("+parametersJoin+"): ");
        }

        // 方法的返回类型
        Type type = method.getType();
        String returnType = "";
        if(type instanceof VoidType){
            methodTemplate.append("void");
        }else{
            returnType = Util.getReturnType(type);
            methodTemplate.append(returnType);
        }
        // abstract抽象方法没方法体
        if(modifiers == 1024 || modifiers == 1025){
            // 给抽象方法添加结束符
            methodTemplate.append(";\n ");
        }else{


            // 方法体
            BlockStmt body = method.getBody();
            if(body != null){
                String methodBNodyString = body.toString();
                List<Statement> stmts = body.getStmts();
                if(stmts != null){
                    for (Statement stmt : stmts ) {
                        if(stmt instanceof ReturnStmt){
                            ReturnStmt returnStmt = (ReturnStmt)stmt;
                            Expression expr = returnStmt.getExpr();
                            if(expr instanceof  NameExpr){
                                NameExpr nameExpr = (NameExpr)expr;
                                String nameExprName = nameExpr.getName();
                                methodBNodyString = methodBNodyString.replace("return "+nameExprName,"return this."+nameExprName);
                            }else if(expr instanceof ClassExpr){
                                methodBNodyString = methodBNodyString.replace(".class", ".prototype");
                            }
                        }else {
                            if (stmts.size() == 1 && stmt instanceof ExpressionStmt){
                                for (String fieldString : allFields) {
                                    // 那么就是set方法
                                    if((  "set" + fieldString.substring(0,1).toUpperCase() + fieldString.substring(1,fieldString.length()) ).equals(name)){

                                        ExpressionStmt expressionStmt = (ExpressionStmt)stmt;
                                        if(expressionStmt != null){
                                            Expression expression = expressionStmt.getExpression();
                                            // AssignExpr 指派| 赋值
                                            if(expression  instanceof AssignExpr){
                                                AssignExpr assignExpr = (AssignExpr) expression;
                                                Expression target = assignExpr.getTarget();
                                                if(target instanceof FieldAccessExpr){
                                                    // 含有this.

                                                } else if(target instanceof NameExpr){
                                                    // 没有this 这种 情况一般是字段名和参数不一样，判断是不是真不一样
                                                    String targetString = target.toString();
                                                    if(allParameterName.indexOf(targetString) == -1 && allFields.contains(targetString)){  // 不在参数之中,却在字段之中
                                                        methodBNodyString = methodBNodyString.replace(targetString,"this."+targetString);
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                methodTemplate.append(methodBNodyString + "\n");
            }
            return methodTemplate.toString();
        }
        return methodTemplate.toString();


    }

    public static void main(String[] args) throws IOException, ParseException {
/*
//        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/taobao/TaobaoTradesSoldGetResponse.java";
//            String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/taobao/TaobaoTradesSoldGetRequest.java";
        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/demo/Order.java";
//        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/DdxBaseRequest.java";
//        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/jingdong/bean/ApiResult.java";
//        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/jingdong/bean/OrderSearchInfo.java";

        String savePath = "D:/lqq/test";
        javaFileToTypescriptFile(javaFilePath, savePath,null);*/


    }


}

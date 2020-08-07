
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
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
import org.freeone.setting.JBean2TsBeanComponent;
import org.freeone.util.FolderUtil;
import org.freeone.util.NotificationUtil;
import org.freeone.util.TemplateUtil;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.List;



public class FirstAction extends AnAction {


    Project currentProject = null;




    /**
     * 点击菜单就会执行这个
     * @param event
     */

    @Override
    public void actionPerformed(AnActionEvent event) {


        // TODO: insert action logic here

        System.err.println("-----------------------------------");
        Project project = event.getProject();
        currentProject = project;
        ToolWindow toolWindow = event.getData(PlatformDataKeys.TOOL_WINDOW);
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

        JBean2TsBeanComponent instance = JBean2TsBeanComponent.getInstance();
        List<String> folderMappingList = instance.getFolderMappingList();
        if (folderMappingList == null || folderMappingList.isEmpty()){
            Messages.showInfoMessage("请先在配置项中设置相关文件映射", "错误");
            return ;
        }

        clearTextArea(project);

        VirtualFile[] virtualFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if(virtualFiles == null){
            NotificationUtil.createNotification("无法获取文件或文件夹",NotificationUtil.ERROR);
            return;
        }
        for (VirtualFile data : virtualFiles) {
            String path = data.getPath();
            // 记录有没有转换过
            boolean beChanged = false;
            if (data.isDirectory()){
                Map<String, String> folderMap = TemplateUtil.convertToFolderMap(folderMappingList);
                Set<String> originFolderSet = folderMap.keySet();
                for (String originFolder: originFolderSet) {
                    // 如果选择的文件是java源文件夹开头
                    if (path.startsWith(originFolder)){
                        // 计算文件的层级，重要不可删除
                        List<String> filePaths = TemplateUtil.getAllJavaFile(originFolder, true);
                        TemplateUtil.fileRelativeLevel.clear();
                        for (String str:filePaths) {

                            str = str.replace("\\","/");
                            String fileRelativePath = (str.replace(originFolder, ""));
                            int i = TemplateUtil.countMatches(fileRelativePath, "/");
                            // 文件的层级
                            if( fileRelativePath.endsWith(".java")){
                                TemplateUtil.fileRelativeLevel.put(fileRelativePath,i);
                            }
                        }



                        beChanged = true;
                        // ts目标文件文件夹
                        String targetFolder = folderMap.get(originFolder);
                        // 相对路径
                        String relativePath = path.replace(originFolder, "");
                        // 目标路径
                        String tsAbsolutePath = targetFolder + relativePath;

                        FolderUtil.clearFolderContent(tsAbsolutePath);
                        writeInfo(project, "初始化路径："+ tsAbsolutePath);
                        List<String> folderListInPath = FolderUtil.getFolderListInPath(path);
                        List<String> pathListInTargetFolder = FolderUtil.convertToPathInTargetFolder(folderListInPath, path, tsAbsolutePath);
                        FolderUtil.createFolders(pathListInTargetFolder);
                        List<String> allJavaFile = TemplateUtil.getAllJavaFile(path, false);
                        for (String javaFilePath : allJavaFile) {
                            // 目标文件的路径，包含文件的名字了
                            String saveFileAbsolutePath = javaFilePath.replace(path, tsAbsolutePath);
                            String saveFolderPath = saveFileAbsolutePath.substring(0, saveFileAbsolutePath.lastIndexOf("/"));
                            javaFileToTypescriptFile(javaFilePath, saveFolderPath, originFolder);
                            writeInfo(project, "源文件："+ javaFilePath);
                            writeInfo(project, "目标文件："+ saveFileAbsolutePath.replace(".java", ".ts"));
                        }

                    }
                }

            }else if("java".equals(data.getExtension())){
                // 是个java文件

                beChanged = true;
                Map<String, String> folderMap = TemplateUtil.convertToFolderMap(folderMappingList);
                Set<String> originFolderSet = folderMap.keySet();
                for (String originFolder: originFolderSet) {
                    if (path.startsWith(originFolder)){

                        // 计算文件的层级，重要不可删除
                        List<String> filePaths = TemplateUtil.getAllJavaFile(originFolder, true);
                         TemplateUtil.fileRelativeLevel.clear();
                        for (String str:filePaths) {

                            str = str.replace("\\","/");
                            String fileRelativePath = (str.replace(originFolder, ""));
                            int i = TemplateUtil.countMatches(fileRelativePath, "/");

                            // 文件的层级
                            if( fileRelativePath.endsWith(".java")){
                                TemplateUtil.fileRelativeLevel.put(fileRelativePath,i);
                            }
                        }

                        String originJavaFileFolderPath = path.substring(0, path.lastIndexOf("/"));
                        String targetFolder = folderMap.get(originFolder);
                        // 相对路径
                        String relativePath = originJavaFileFolderPath.replace(originFolder, "");
                        String savePath = targetFolder + relativePath;
                        javaFileToTypescriptFile(path,savePath,originFolder);
                        writeInfo(project, "源文件："+ path);
                        writeInfo(project, "目标文件："+ path.replace(originFolder,targetFolder).replace(".java", ".ts"));

                    }
                }

            }else {
                Messages.showErrorDialog("选java文件或文件夹", "异常操作");
            }

            if (!beChanged){
                Messages.showWarningDialog(path + "没有找到匹配的映射，请在配置中添加相关映射", "警告");
            }else{
                writeActionComplete(project);
            }
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {

    }

    /**
     * java文件转typescrite文件
     * @param javaFilePath java文件的路径
     * @param savePath  保存路径，不需要包含文件的名字
     * @param originFolder 映射路径
     */
    public  void javaFileToTypescriptFile(String javaFilePath,String savePath,String originFolder ){
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
            Set<String> javaTypeInMembers = TemplateUtil.getAllFieldJavaTypeInMembers(temMembers);
            //  获取继承中的java类型
            Set<String> javaTypeInExtends = TemplateUtil.getJavaTypeInExtends(javaClass.getExtends());
            // 添加其中一起判断和导入
            javaTypeInMembers.addAll(javaTypeInExtends);
            // 获取泛型中的java类型
            Set<String> javaTypeInTypeParameters = TemplateUtil.getJavaTypeInTypeParameters(javaClass.getTypeParameters());
            javaTypeInMembers.addAll(javaTypeInTypeParameters);


            final String tempPackageString = packageString;
            // 不同路径引入
            javaTypeInMembers.forEach(javaTypeName ->{
                if (javaClassHasImported.indexOf(javaTypeName) == -1){
                    String importInDifferentFolder = TemplateUtil.getImportInDifferentFolder(javaFilePath, javaTypeName,  tempPackageString ,imports,originFolder);
                    if(importInDifferentFolder != null){
                        javaClassHasImported.add(javaTypeName);
                        typeScriptFileContent.append(importInDifferentFolder);
                    }
                }
            });

            // 先获取所有的字段
            Set<String> allField = TemplateUtil.getAllField(temMembers);
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
                    typeScriptFileContent.append(" extends ").append(extendsString);
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

//            String constructorTemplate = TemplateUtil.getConstructorTemplate(fieldMap,javaClass.getExtends() != null);
//            typeScriptFileContent.append(constructorTemplate);

            typeScriptFileContent.append("}\n");
            typeScriptFileContent.append("export = "+javaClassName+";");
            File savePathFolder = new File(savePath);
            if(!savePathFolder.exists() ){
                savePathFolder.mkdirs();
            }
            String typeScriptFileSavePath  = savePath  + "/"+javaClassName+".ts";
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(typeScriptFileSavePath,false),"utf-8"));
            bufferedWriter.write(typeScriptFileContent.toString());
            bufferedWriter.close();

        } catch ( Exception e) {
            e.printStackTrace();
            writeInfo(currentProject, "异常");
            writeInfo(currentProject, e.toString());
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
        fieldTemplate.append(name).append(": ");
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
        }


        // arrayCount == 1 是数组[]
        if (arrayCount == 1){
            fieldTemplate.append("Array<").append(TemplateUtil.getTypeScriptDataType(typeName)).append(">;\n");
            fieldMap.put(name,"Array<"+ TemplateUtil.getTypeScriptDataType(typeName)+">");
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
                if(typeArgs != null){
                    String keyType = TemplateUtil.getReturnType(typeArgs.get(0));
                    String valueType = TemplateUtil.getReturnType(typeArgs.get(1));
                    fieldTemplate.append("Map<").append(keyType).append(",").append(valueType).append(">");
                    fieldTemplate.append(";\n");
                    fieldMap.put(name,"Map<"+keyType+","+valueType+">");
                }
            }else if("Set".equals(typeName)) {
                List<Type> typeArgs = classOrInterfaceType.getTypeArgs();
                if(typeArgs != null){
                    String keyType = TemplateUtil.getReturnType(typeArgs.get(0));
                    fieldTemplate.append("Set<").append(keyType).append(">");
                    fieldTemplate.append(";\n");
                    fieldMap.put(name,"Set<"+keyType+">");
                }
            } else {
                // 基础数据类型
                String typeScriptDataType = TemplateUtil.getTypeScriptDataType(typeName);
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
            methodTemplate.append("    ").append(javaDoc.toString());
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
                String parameterType = TemplateUtil.getReturnType(parameter.getType());
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
            returnType = TemplateUtil.getReturnType(type);
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
                                                    // 不在参数之中,却在字段之中
                                                    if(allParameterName.indexOf(targetString) == -1 && allFields.contains(targetString)){
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

    /**
     * 获取日志窗口的textarea
     * @param project
     * @return
     */
    public JTextArea getTextArea(Project project){
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow java_bean_to_ts_bean_result = (ToolWindow)toolWindowManager.getToolWindow("java bean to ts bean result");
        boolean visible = java_bean_to_ts_bean_result.isVisible();
        if (!visible){
            java_bean_to_ts_bean_result.show(null);
        }
        JComponent component = java_bean_to_ts_bean_result.getComponent();
        ContentManager contentManager = java_bean_to_ts_bean_result.getContentManager();
        Content[] contents = contentManager.getContents();
        JScrollPane jScrollPane = (JScrollPane)contents[0].getComponent();
        return (JTextArea)jScrollPane.getViewport().getComponent(0);
    }

    /**
     * 清理日志信息
     * @param project
     */
    public void clearTextArea(Project project){
        try {
            JTextArea textArea = getTextArea(project);
            textArea.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 日志窗口输出转换完成
     * @param project
     */
    public void writeActionComplete(Project project){
        JTextArea textArea = getTextArea(project);
        textArea.append("\n");
        textArea.append("========== 转 换 完 成 ==========");
        textArea.append("\n");
    }

    /**
     * 将info添加到面板上
     * @param project
     * @param info
     */
    public void writeInfo(Project project,String info){
        try {
            JTextArea textArea = getTextArea(project);
            textArea.append("\n");
            textArea.append("> " + info);
            textArea.append("\n");
            textArea.setCaretPosition(textArea.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static void main(String[] args) throws IOException, ParseException {

//        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/taobao/TaobaoTradesSoldGetResponse.java";
//            String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/taobao/TaobaoTradesSoldGetRequest.java";
        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/demo/Order.java";
//        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/DdxBaseRequest.java";
//        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/jingdong/bean/ApiResult.java";
//        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/jingdong/bean/OrderSearchInfo.java";

        String savePath = "D:/lqq/test";
        javaFileToTypescriptFile(javaFilePath, savePath,null);


    }*/


}

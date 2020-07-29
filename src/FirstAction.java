import com.fasterxml.jackson.databind.ObjectMapper;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.*;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import javassist.bytecode.ClassFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstAction extends AnAction {

    static List<String> typesToNumber = Arrays.asList("int", "Integer", "byte", "Byte", "short", "Short", "long", "Long", "float", "Float", "double", "Double");
    static List<String> typesToString = Arrays.asList("String");
    static List<String> typesToBoolean = Arrays.asList("boolean", "Boolean");
    static List<String> typesToAny = Arrays.asList("Object");


    /**
     * 点击菜单就会执行这个
     * @param event
     */

    @Override
    public void actionPerformed(AnActionEvent event) {

        ObjectMapper objectMapper = new ObjectMapper();

        // TODO: insert action logic here
        Project project = event.getData(PlatformDataKeys.PROJECT);
        DataContext dataContext = event.getDataContext();
        String fileExtension = getFileExtension(dataContext);
        System.err.println("-----------------------------------");

        VirtualFile data = event.getData(PlatformDataKeys.VIRTUAL_FILE);




        if (data.isDirectory()){
            //  是文件夹
            String savePath = chooseFolder();
            if (null == savePath){
                Messages.showErrorDialog("请选择转换之后的文件夹", "错误");
            }

        }else if("java".equals(data.getExtension())){
            // 是个java文件
            String savePath = chooseFolder();
            if (null == savePath){
                Messages.showErrorDialog("请选择转换之后的文件夹", "错误");
            }
            String nameWithoutExtension = data.getNameWithoutExtension();
            String name = data.getName();

            readJavaFile(nameWithoutExtension,data.getPath(), savePath);
//            javaFileToTypescriptFile(data.getPath(), savePath);

        }else if("class".equals(data.getExtension())){

            try {


                Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(data.getPath());
                System.err.println(aClass);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }else{
            Messages.showErrorDialog("选java文件或文件夹", "异常操作");
        }

    }



    @Override
    public void update(@NotNull AnActionEvent event) {


    }




    public static String getFileExtension(DataContext dataContext) {
        VirtualFile data = dataContext.getData(PlatformDataKeys.VIRTUAL_FILE);
        return data == null ? null : data.getExtension();
    }

    /**
     * 设置编译之后的路径
     * @return
     */
    private String getSavePath(){

        JFileChooser fileChooser = new JFileChooser();
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        //桌面路径
        File homeDirectory = fileSystemView.getHomeDirectory();
        fileChooser.setCurrentDirectory(homeDirectory);
        fileChooser.setDialogTitle("请选择编译之后的文件夹");
        fileChooser.setApproveButtonText("确定");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(fileChooser);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            //这个就是你选择的文件夹的路径
            String savePath  = fileChooser.getSelectedFile().getAbsolutePath();
            System.out.println("absolutePath");
            System.out.println(savePath);
            return savePath;
        }else{
            return null;
        }
    }

    private String chooseFolder(){

        FileChooserDescriptor singleFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        singleFolderDescriptor.setTitle("请选择转换之后的存放路径");
        VirtualFile newFileChooser =   FileChooser.chooseFile(singleFolderDescriptor, null,null);

        if (null == newFileChooser){
            return null;
        }else{

            return newFileChooser.getPath();
        }
    }

    /**
     * java文件转typescrite文件
     * @param javaFilepath
     * @param savePath
     */
    private void javaFileToTypescriptFile(String javaFilepath,String savePath){
        try {
            javaFilepath.replace("file://", "");
            FileReader fileReader = new FileReader(javaFilepath);

            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(javaFilepath), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s = "";
            String packagePath = "";
            List<String> importPathList = new ArrayList<>();



            //定义一个变量s，让s等于br去读一行。
            while((s = bufferedReader.readLine()) != null){
                if (s.startsWith("package")){
                    s = s.replace("package", "");
                    s = s.replace(";", "");
                    s = s.trim();
                    packagePath = s;
                }else if(s.startsWith("import")){
                    s = s.replace("import", "");
                    s = s.replace(";", "");
                    s = s.trim();
                    importPathList.add(s);
                }
            }

            System.err.println(packagePath);
            System.err.println(importPathList);


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取java文件
     * @param javaClassName
     * @param javaFilePath
     * @param savePath
     */
    private void readJavaFile(String javaClassName, String javaFilePath,String savePath){
        try {




        }  catch (Exception e) {
            e.printStackTrace();
        }

        Class<?> clazz = null;
    }

    private void compilerJavaFile(String javaClassName, String javaFilePath,String savePath){
        try {

            JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
//            ClassLoader systemToolClassLoader = ToolProvider.getSystemToolClassLoader();
            String[] order = {"-encoding", "UTF-8",javaFilePath};
            int compilationResult = javac.run(null, null, null, order);
            if  (compilationResult != 0){
                throw new IllegalArgumentException("编译失败");
            }else{
                System.err.println("编译成功");
            }


            String classFilePath = javaFilePath.subSequence(0, javaFilePath.length() - 4).toString()+"class";

            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(classFilePath));
            ClassFile classFile = new ClassFile(dataInputStream);
            List fields = classFile.getFields();
            fields.forEach(field -> {
                System.err.println(field);
            });


        }  catch (IOException e) {
            e.printStackTrace();
        }

        Class<?> clazz = null;
    }




    public static void main(String[] args) throws IOException, ParseException {
        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/demo/Trade.java";
        CompilationUnit parse = JavaParser.parse(new File(javaFilePath),"utf-8");
        StringBuilder typeScriptFileContent = new StringBuilder();
        List<TypeDeclaration> types = parse.getTypes();
        // 获取第一个class
        TypeDeclaration javaClass = types.get(0);
        String javaClassName = javaClass.getName();
        typeScriptFileContent.append("class " + javaClassName+ " {\n");
        List<BodyDeclaration> members = javaClass.getMembers();

        members.forEach(member -> {
             if(member instanceof FieldDeclaration){
                 FieldDeclaration field = (FieldDeclaration)member;
                 // 先放注解
                 JavadocComment javaDoc = field.getJavaDoc();
                 if (javaDoc != null){
                     String content = javaDoc.getContent();
                     if (content != null){
                         typeScriptFileContent.append(content);
                     }
                 }
                 // 字段权限
                 int modifiers = field.getModifiers();
                 if (modifiers == 1){
                     typeScriptFileContent.append("    public ");
                 }else if(modifiers == 2){
                     typeScriptFileContent.append("    private ");
                 }
                 // 字段名称
                 List<VariableDeclarator> variables = field.getVariables();
                 VariableDeclarator variable = variables.get(0);
                 String name = variable.getId().getName();
                 typeScriptFileContent.append("name: ");
                 // 字段类型
                 ReferenceType type = (ReferenceType)field.getType();
                 ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType)type.getType();
                 // 获得类型的名称 String还是Inter之类的
                 String typeName = classOrInterfaceType.getName();
                 int arrayCount = type.getArrayCount();
                 // arrayCount == 1 是数组[]
                 if (arrayCount == 1){

                     typeScriptFileContent.append("Array<"+typeName+">;\n");
                 }else if(arrayCount == 0){
                     // 可能是List，也是能是基础数据类型
                     if("List".equals(typeName)){
                         String string = classOrInterfaceType.toString();
                         String replace = string.replace("List<", "Array<");
                         typeScriptFileContent.append(replace+";\n");
                     }else{
                         // 基础数据类型
                         String typeScriptDataType = getTypeScriptDataType(typeName);
                         typeScriptFileContent.append(typeScriptDataType+";\n");
                     }
                 }


             }else if( member instanceof MethodDeclaration){
                 // 方法
                 member = (MethodDeclaration)member;


             }
        });



        typeScriptFileContent.append("}\n");


    }

    /**
     * 获取文件的类型
     * @param type
     * @return
     */
    public static String getTypeScriptDataType(String type){
        if (typesToString.indexOf(type) != -1){
            return "string";
        }else if (typesToNumber.indexOf(type) != -1){
            return "number";
        }else if(typesToBoolean.indexOf(type) != -1){
            return "boolean";
        }else {
            return "any";
        }
    }






}

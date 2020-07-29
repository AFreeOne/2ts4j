import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.io.java.impl.ClassFileBuilderImpl;
import com.sun.jna.Structure;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import javassist.bytecode.ClassFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.SystemIndependent;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstAction extends AnAction {
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


    public void JavaBaseTypes(){

        List<String> typesToNumber = Arrays.asList("int", "Integer", "byte", "Byte", "short", "Short", "long", "Long", "float", "Float", "double", "Double");
        List<String> typesToString = Arrays.asList("String");
        List<String> typesToBoolean = Arrays.asList("boolean", "Boolean");
        List<String> typesToAny = Arrays.asList("Object");

    }

    public static void main(String[] args) throws IOException, ParseException {
        String javaFilePath = "E:/diandaxia/common/src/main/java/com/diandaxia/common/sdk/demo/Trade.java";
        CompilationUnit parse = JavaParser.parse(new File(javaFilePath),"utf-8");
        PackageDeclaration aPackage = parse.getPackage();
        List<Comment> comments = parse.getComments();
        System.err.println(1);


    }

    private static void parseFile(byte[] data){

        //输出魔数

        System.out.print("魔数(magic):0x");
        System.out.print(Integer.toHexString(data[0]).substring(6).toUpperCase());
        System.out.print(Integer.toHexString(data[1]).substring(6).toUpperCase());
        System.out.print(Integer.toHexString(data[2]).substring(6).toUpperCase());
        System.out.println(Integer.toHexString(data[3]).substring(6).toUpperCase());
        //主版本号和次版本号码
        int minor_version = (((int)data[4]) << 8) + data[5];
        int major_version = (((int)data[6]) << 8) + data[7];
        System.out.println("版本号(version):" + major_version + "." + minor_version);
    }






}

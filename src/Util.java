import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

import java.io.File;
import java.util.*;

public class Util {

    static List<String> typesToNumber = Arrays.asList("int", "Integer", "byte", "Byte", "short", "Short", "long", "Long", "float", "Float", "double", "Double");
    static List<String> typesToString = Arrays.asList("String");
    static List<String> typesToBoolean = Arrays.asList("boolean", "Boolean");
    static List<String> typesToAny = Arrays.asList("Object");


    /**
     * 获取java 数据类型，转ts数据类型
     * @param type
     * @return
     */
    public static String getTypeScriptDataType(String type){
        if (Util.typesToString.indexOf(type) != -1){
            return "string";
        }else if (Util.typesToNumber.indexOf(type) != -1){
            return "number";
        }else if(Util.typesToBoolean.indexOf(type) != -1){
            return "boolean";
        }else if (Util.typesToAny.indexOf(type) != -1){
            return "any";
        }else{
            return type;
        }
    }

    /**
     * 获取文件夹中所有java文件的路劲
     * @param directoryPath 目标路劲
     * @param isAddDirectory 是否将子路径也添加到返回值中
     * @return
     */
    public static List<String> getAllJavaFile(String directoryPath,boolean isAddDirectory) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if(isAddDirectory){
                    list.add(file.getAbsolutePath());
                }
                list.addAll(getAllJavaFile(file.getAbsolutePath(),isAddDirectory));
            } else {
                String absolutePath = file.getAbsolutePath();
                if(absolutePath.endsWith(".java")){
                    list.add(file.getAbsolutePath());
                }

            }
        }
        return list;
    }

    /**
     * 获取members中所有的java类型
     * @param temMembers
     */
    public static Set<String> getAllFieldJavaTypeInMembers(List<BodyDeclaration> temMembers){
        Set<String> javaTypeNameSet = new HashSet<>();
        if (temMembers != null){
            temMembers.forEach(temMember ->{
                if (temMember instanceof FieldDeclaration){
                    // 是字段
                    // 字段的类型
                    Type type = ((FieldDeclaration) temMember).getType();
                    Set<String> javaTypeNameSet1 = getJavaTypeNameSet(type);
                    javaTypeNameSet.addAll(javaTypeNameSet1);
                }
            });
        }
        return javaTypeNameSet;
    }

    /**
     * 获取type中所有引用的java类型
     * @param type
     * @return
     */
    public static Set<String> getJavaTypeNameSet(Type type){
        Set<String> javaTypeNameSet = new HashSet<>();
        ReferenceType referenceType = (ReferenceType)type;
        ClassOrInterfaceType classOrInterfaceType =  (ClassOrInterfaceType)referenceType.getType();
        List<Type> typeArgs = classOrInterfaceType.getTypeArgs();

        String javaTypeName = classOrInterfaceType.getName();
        javaTypeNameSet.add(javaTypeName);
        if (typeArgs != null){
            typeArgs.forEach(type1 -> {

                Set<String> javaTypeNameList1 = getJavaTypeNameSet(type1);
                javaTypeNameSet.addAll(javaTypeNameList1);
            });
        }
        return javaTypeNameSet;
    }

    /**
     * 获取返回的类型
     * @param type
     */
    public static String getReturnType(Type type){
        String returnType = "";

        ReferenceType referenceType =  (ReferenceType) type;


        ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType)referenceType.getType();
        int arrayCount = referenceType.getArrayCount();
        if(arrayCount == 1){
            // 数组
            String name = classOrInterfaceType.getName();
            String typeScriptDataType = Util.getTypeScriptDataType(name);
            returnType = "Array<"+typeScriptDataType+">";
        }else if(arrayCount == 0){
            List<Type> typeArgs = classOrInterfaceType.getTypeArgs();
            if (typeArgs == null){
                // 基础数据类型
                String typeScriptDataType = Util.getTypeScriptDataType(classOrInterfaceType.getName());
                returnType = typeScriptDataType;
            }else{
                // list 之类



                String childReturnType = getReturnType(typeArgs.get(0));


                if(  "Class".equals(classOrInterfaceType.getName())){
                    returnType = getTypeScriptDataType(childReturnType+"|Function ");
                }else{
                    returnType = "Array<"+childReturnType+">";
                }

            }

        }else{
            returnType = "any";
        }

        return returnType;
    }

    /**
     * 获取所有的字段
     * @param temMembers
     * @return
     */
    public static Set<String> getAllField(List<BodyDeclaration> temMembers){
        // TODO getAllField
        Set<String> fieldSet = new HashSet<>();
        if (temMembers != null){
            temMembers.forEach(temMember ->{
                if (temMember instanceof FieldDeclaration){
                    // 是字段
                    // 字段的类型
                    Type type = ((FieldDeclaration) temMember).getType();
                    List<VariableDeclarator> variables = ((FieldDeclaration) temMember).getVariables();
                    if(variables != null){
                        String fieldName = variables.get(0).getId().getName();
                        fieldSet.add(fieldName);
                    }


                }
            });
        }
        return fieldSet;


    }

}

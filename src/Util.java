import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import org.apache.commons.lang3.CharSequenceUtils;

import java.io.File;
import java.util.*;

public class Util {

    static Map<String,Integer> fileRelativeLevel = new LinkedHashMap<>();
    static List<String> typesToNumber = Arrays.asList("int", "Integer", "byte", "Byte", "short", "Short", "long", "Long", "float", "Float", "double", "Double");
    static List<String> typesToString = Arrays.asList("String");
    static List<String> typesToBoolean = Arrays.asList("boolean", "Boolean");
    static List<String> typesToAny = Arrays.asList("Object");


    /**
     * 获取java 数据类型，转ts数据类型
     *
     * @param type
     * @return
     */
    public static String getTypeScriptDataType(String type) {
        if (Util.typesToString.indexOf(type) != -1) {
            return "string";
        } else if (Util.typesToNumber.indexOf(type) != -1) {
            return "number";
        } else if (Util.typesToBoolean.indexOf(type) != -1) {
            return "boolean";
        } else if (Util.typesToAny.indexOf(type) != -1) {
            return "any";
        } else {
            return type;
        }
    }

    /**
     * 获取文件夹中所有java文件的路劲
     *
     * @param directoryPath  目标路劲
     * @param isAddDirectory 是否将子路径也添加到返回值中
     * @return
     */
    public static List<String> getAllJavaFile(String directoryPath, boolean isAddDirectory) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (isAddDirectory) {
                    list.add(file.getAbsolutePath());
                }
                list.addAll(getAllJavaFile(file.getAbsolutePath(), isAddDirectory));
            } else {
                String absolutePath = file.getAbsolutePath();
                if (absolutePath.endsWith(".java")) {
                    list.add(file.getAbsolutePath());
                }

            }
        }
        return list;
    }

    /**
     * 获取members中所有的java类型
     *
     * @param temMembers
     */
    public static Set<String> getAllFieldJavaTypeInMembers(List<BodyDeclaration> temMembers) {
        Set<String> javaTypeNameSet = new HashSet<>();
        if (temMembers != null) {
            temMembers.forEach(temMember -> {
                if (temMember instanceof FieldDeclaration) {
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
     *
     * @param type
     * @return
     */
    public static Set<String> getJavaTypeNameSet(Type type) {
        Set<String> javaTypeNameSet = new HashSet<>();
        if (type instanceof PrimitiveType) {
            PrimitiveType primitiveType = (PrimitiveType) type;
            String javaTypeName = primitiveType.toString();
            javaTypeNameSet.add(javaTypeName);
            return javaTypeNameSet;
        } else if(type instanceof ClassOrInterfaceType){
            ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) type;
            List<Type> typeArgs = classOrInterfaceType.getTypeArgs();

            String javaTypeName = classOrInterfaceType.getName();
            javaTypeNameSet.add(javaTypeName);
            if (typeArgs != null) {
                typeArgs.forEach(type1 -> {
                    Set<String> javaTypeNameList1 = getJavaTypeNameSet(type1);
                    javaTypeNameSet.addAll(javaTypeNameList1);
                });
            }
            return javaTypeNameSet;
        }else {
            ReferenceType referenceType = (ReferenceType) type;
            ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) referenceType.getType();
            List<Type> typeArgs = classOrInterfaceType.getTypeArgs();

            String javaTypeName = classOrInterfaceType.getName();
            javaTypeNameSet.add(javaTypeName);
            if (typeArgs != null) {
                typeArgs.forEach(type1 -> {
                    Set<String> javaTypeNameList1 = getJavaTypeNameSet(type1);
                    javaTypeNameSet.addAll(javaTypeNameList1);
                });
            }
            return javaTypeNameSet;
        }
    }

    /**
     * 获取类的继承中的所使用到的java类
     * @param anExtends
     * @return
     */
    public static Set<String> getJavaTypeInExtends(List<ClassOrInterfaceType> anExtends){
        Set<String> javaTypeSet = new HashSet<>();
        if (anExtends == null || anExtends.isEmpty()){
            return javaTypeSet;
        }
        for (ClassOrInterfaceType classOrInterfaceType : anExtends) {
            String name = classOrInterfaceType.getName();
            javaTypeSet.add(name);
            // 如果继承中存在泛型
            List<Type> typeArgs = classOrInterfaceType.getTypeArgs();
            if(typeArgs != null){
                typeArgs.forEach(typeArg->{
                    Set<String> javaTypeNameSet = getJavaTypeNameSet(typeArg);
                    javaTypeSet.addAll(javaTypeNameSet);
                });
            }

        }
        return javaTypeSet;
    }

    /**
     * 获取泛型中的使用到java类
     * @param typeParameters
     */
    public static Set<String> getJavaTypeInTypeParameters(List<TypeParameter> typeParameters){
        Set<String> javaTypeSet = new HashSet<>();
        if (typeParameters == null || typeParameters.isEmpty()){
            return javaTypeSet;
        }

        for (TypeParameter typeParameter : typeParameters) {
            String name = typeParameter.getName();
            javaTypeSet.add(name);
            List<ClassOrInterfaceType> typeBound = typeParameter.getTypeBound();
            if (typeBound != null){
                ClassOrInterfaceType classOrInterfaceType = typeBound.get(0);
                Set<String> javaTypeNameSet = getJavaTypeNameSet(classOrInterfaceType);
                javaTypeSet.addAll(javaTypeNameSet);
            }
        }
        return javaTypeSet;
    }

    /**
     * 获取返回的类型
     *
     * @param type
     */
    public static String getReturnType(Type type) {
        String returnType = "";
        if (type instanceof PrimitiveType) {
            return getTypeScriptDataType(type.toString());
        }

        ReferenceType referenceType = (ReferenceType) type;


        ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) referenceType.getType();
        int arrayCount = referenceType.getArrayCount();
        if (arrayCount == 1) {
            // 数组
            String name = classOrInterfaceType.getName();
            String typeScriptDataType = Util.getTypeScriptDataType(name);
            returnType = "Array<" + typeScriptDataType + ">";
        } else if (arrayCount == 0) {
            List<Type> typeArgs = classOrInterfaceType.getTypeArgs();
            if (typeArgs == null) {
                // 基础数据类型
                String typeScriptDataType = Util.getTypeScriptDataType(classOrInterfaceType.getName());
                returnType = typeScriptDataType;
            } else {
                // list 之类
                String childReturnType = getReturnType(typeArgs.get(0));
                if ("Class".equals(classOrInterfaceType.getName())) {
                    returnType = getTypeScriptDataType(childReturnType + "|Function ");
                } else if("List".equals(classOrInterfaceType.getName())) {
                    returnType = "Array<" + childReturnType + ">";
                }else if("Map".equals(classOrInterfaceType.getName())){
                    String keyType = Util.getReturnType(typeArgs.get(0));
                    String valueType = Util.getReturnType(typeArgs.get(1));
                    returnType =  "Map<"+keyType+","+valueType+">";
                }else if("Set".equals(classOrInterfaceType.getName())){
                    String keyType = Util.getReturnType(typeArgs.get(0));
                    returnType =  "Set<"+keyType+">";
                }else {
                    returnType = "Array<" + childReturnType + ">";
                }
            }

        } else {
            returnType = "any";
        }

        return returnType;
    }

    /**
     * 获取所有的字段
     *
     * @param temMembers
     * @return
     */
    public static Set<String> getAllField(List<BodyDeclaration> temMembers) {

        Set<String> fieldSet = new HashSet<>();
        if (temMembers != null) {
            temMembers.forEach(temMember -> {
                if (temMember instanceof FieldDeclaration) {
                    // 是字段
                    // 字段的类型
                    Type type = ((FieldDeclaration) temMember).getType();
                    List<VariableDeclarator> variables = ((FieldDeclaration) temMember).getVariables();
                    if (variables != null) {
                        String fieldName = variables.get(0).getId().getName();
                        fieldSet.add(fieldName);
                    }
                }
            });
        }
        return fieldSet;

    }

    public static String  getImportInDifferentFolder(String currentJavaFielPath,String targetClassName){
        Set<String> keySet = fileRelativeLevel.keySet();
        for (String key: keySet) {
            boolean contains = currentJavaFielPath.contains(key);
            if(currentJavaFielPath.contains(key)){
                // 当前java文件的层级
                Integer currentJavaFieLevel = fileRelativeLevel.get(key);
                for (String classFileKey: keySet) {
                    if(classFileKey.contains("/"+targetClassName+".java")){
                        // 需要引入的文件的层级
                        Integer targetClassLevel = fileRelativeLevel.get(classFileKey);
                        if(currentJavaFieLevel.intValue() == targetClassLevel.intValue()){
                            // 相同层级已经经过处理
                            continue;
                        }else if(currentJavaFieLevel.intValue() > targetClassLevel.intValue()){
                            StringBuilder returnString = new StringBuilder();
                            returnString.append("import "+ targetClassName+" = require(\"");
                            // 层级差距
                            int value =  currentJavaFieLevel.intValue() - targetClassLevel.intValue();
                            for (int i = 0; i < value; i++) {
                                returnString.append("../");
                            }
                            returnString.append(targetClassName + "\");\n");
                            return returnString.toString();
                        }
                    }

                }
            }

        }
        return  null;
    }


    //--------------------------------------------------

    /**
     * 字符串工具
     * @param str
     * @param sub
     * @return
     */
    public static int countMatches(CharSequence str, CharSequence sub) {
        if (!isEmpty(str) && !isEmpty(sub)) {
            int count = 0;

            for(int idx = 0; (idx = indexOf(str, sub, idx)) != -1; idx += sub.length()) {
                ++count;
            }

            return count;
        } else {
            return 0;
        }
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    static int indexOf(CharSequence cs, CharSequence searchChar, int start) {
        return cs.toString().indexOf(searchChar.toString(), start);
    }
}

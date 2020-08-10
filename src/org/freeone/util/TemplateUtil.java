package org.freeone.util;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import org.freeone.apidoc.entity.TbRequestFieldEntity;
import org.freeone.apidoc.entity.TbResponseFieldEntity;


import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Lq<sup>2</sup>
 */
public class TemplateUtil {

    public static final String FOLDER_SPLIT = "\\|";
    public static final String Origin_Folder = "originFolder";
    public static final String Target_Folder = "targetFolder";

    public static Map<String, Integer> fileRelativeLevel = new LinkedHashMap<>();

    public static Map<String, Integer> fileAbsoluteLevel = new LinkedHashMap<>();

    static List<String> typesToNumber = Arrays.asList("int", "Integer", "byte", "Byte", "short", "Short", "long", "Long", "float", "Float", "double", "Double", "BigDecimal");
    static List<String> typesToString = Arrays.asList("String", "StringBuilder", "StringBuffer");
    static List<String> typesToBoolean = Arrays.asList("boolean", "Boolean");
    static List<String> typesToAny = Collections.singletonList("Object");
    /**
     * 原样输出
     */
    static List<String> sourceType = Arrays.asList("Date","String","Integer","int","Boolean","boolean", "byte", "Byte", "short", "Short", "long", "Long", "float", "Float", "double", "Double", "BigDecimal");




    /**
     * 获取java 数据类型，转ts数据类型
     *
     * @param type java类型
     * @return type script 类型
     */
    public static String getTypeScriptDataType(String type) {
        if (TemplateUtil.typesToString.indexOf(type) != -1) {
            return "string";
        } else if (TemplateUtil.typesToNumber.indexOf(type) != -1) {
            return "number";
        } else if (TemplateUtil.typesToBoolean.indexOf(type) != -1) {
            return "boolean";
        } else if (TemplateUtil.typesToAny.indexOf(type) != -1) {
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
     * @return java文件的路径list
     */
    public static List<String> getAllJavaFile(String directoryPath, boolean isAddDirectory) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        if (files == null) {
            return list;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                if (isAddDirectory) {
                    String absolutePath = file.getAbsolutePath();
                    list.add(absolutePath.replace("\\", "/"));
                }
                list.addAll(getAllJavaFile(file.getAbsolutePath(), isAddDirectory));
            } else {
                String absolutePath = file.getAbsolutePath();
                if (absolutePath.endsWith(".java")) {
                    list.add(absolutePath.replace("\\", "/"));
                }
            }
        }
        return list;
    }

    /**
     * 获取members中所有的java类型
     *
     * @param temMembers java类中的member
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
     * @param type javatype
     * @return type中所有引用的java类型
     */
    public static Set<String> getJavaTypeNameSet(Type type) {
        Set<String> javaTypeNameSet = new HashSet<>();
        if (type instanceof PrimitiveType) {
            PrimitiveType primitiveType = (PrimitiveType) type;
            String javaTypeName = primitiveType.toString();
            javaTypeNameSet.add(javaTypeName);
            return javaTypeNameSet;
        } else if (type instanceof ClassOrInterfaceType) {
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
        } else {
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
     *
     * @param anExtends 继承部分
     * @return 继承部分java类的set
     */
    public static Set<String> getJavaTypeInExtends(List<ClassOrInterfaceType> anExtends) {
        Set<String> javaTypeSet = new HashSet<>();
        if (anExtends == null || anExtends.isEmpty()) {
            return javaTypeSet;
        }
        for (ClassOrInterfaceType classOrInterfaceType : anExtends) {
            String name = classOrInterfaceType.getName();
            javaTypeSet.add(name);
            // 如果继承中存在泛型
            List<Type> typeArgs = classOrInterfaceType.getTypeArgs();
            if (typeArgs != null) {
                typeArgs.forEach(typeArg -> {
                    Set<String> javaTypeNameSet = getJavaTypeNameSet(typeArg);
                    javaTypeSet.addAll(javaTypeNameSet);
                });
            }

        }
        return javaTypeSet;
    }

    /**
     * 获取泛型中的使用到java类
     *
     * @param typeParameters 泛型部分
     */
    public static Set<String> getJavaTypeInTypeParameters(List<TypeParameter> typeParameters) {
        Set<String> javaTypeSet = new HashSet<>();
        if (typeParameters == null || typeParameters.isEmpty()) {
            return javaTypeSet;
        }

        for (TypeParameter typeParameter : typeParameters) {
            String name = typeParameter.getName();
            javaTypeSet.add(name);
            List<ClassOrInterfaceType> typeBound = typeParameter.getTypeBound();
            if (typeBound != null) {
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
     * @param type java type
     */
    public static String getReturnType(Type type) {
        String returnType;
        if (type instanceof PrimitiveType) {
            return getTypeScriptDataType(type.toString());
        }

        ReferenceType referenceType = (ReferenceType) type;


        ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) referenceType.getType();
        int arrayCount = referenceType.getArrayCount();
        if (arrayCount == 1) {
            // 数组
            String name = classOrInterfaceType.getName();
            String typeScriptDataType = TemplateUtil.getTypeScriptDataType(name);
            returnType = "Array<" + typeScriptDataType + ">";
        } else if (arrayCount == 0) {
            List<Type> typeArgs = classOrInterfaceType.getTypeArgs();
            if (typeArgs == null) {
                // 基础数据类型
                returnType = TemplateUtil.getTypeScriptDataType(classOrInterfaceType.getName());
            } else {
                // list 之类
                String childReturnType = getReturnType(typeArgs.get(0));
                if ("Class".equals(classOrInterfaceType.getName())) {
//                    returnType = getTypeScriptDataType(childReturnType + "|Function ");
                    returnType = getTypeScriptDataType(childReturnType + "");
                } else if ("List".equals(classOrInterfaceType.getName())) {
                    returnType = "Array<" + childReturnType + ">";
                } else if ("Map".equals(classOrInterfaceType.getName())) {
                    String keyType = TemplateUtil.getReturnType(typeArgs.get(0));
                    String valueType = TemplateUtil.getReturnType(typeArgs.get(1));
                    returnType = "Map<" + keyType + "," + valueType + ">";
                } else if ("Set".equals(classOrInterfaceType.getName())) {
                    String keyType = TemplateUtil.getReturnType(typeArgs.get(0));
                    returnType = "Set<" + keyType + ">";
                } else {
                    returnType = "Array<" + childReturnType + ">";
                }
            }

        } else {
            returnType = "any";
        }

        return returnType;
    }

    /**
     * 获取所有的字段的名字
     *
     * @param temMembers 当前类的member
     * @return 当前java类所有字段的set
     */
    public static Set<String> getAllField(List<BodyDeclaration> temMembers) {

        Set<String> fieldSet = new LinkedHashSet<>();
        if (temMembers != null) {
            temMembers.forEach(temMember -> {
                if (temMember instanceof FieldDeclaration) {
                    // 是字段
                    // 字段的类型
//                    Type type = ((FieldDeclaration) temMember).getType();
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

    /**
     * 获取import模板
     * @param currentJavaFilePath 当前java文件的路径
     * @param targetClassName 目标类
     * @param packageString 当前累的package声明
     * @param imports 当前类的所有import
     * @param originFolder 映射路径
     * @return String
     */
    public static String getImportInDifferentFolder(String currentJavaFilePath, String targetClassName, String packageString, List<ImportDeclaration> imports,String originFolder) {
        // TODO 重置 有限考虑在import中
//        if(imports.indexOf (packageString + "."+ targetClassName))
        boolean inImport = false;
        if (imports != null) {
            for (ImportDeclaration otherImport : imports) {
                String otherImportString = otherImport.toString();
                if (otherImportString.contains("." + targetClassName)) {
                    inImport = true;
                    break;
                }
            }
        }


        Set<String> keySet = fileRelativeLevel.keySet();


        for (String key : keySet) {

            if (currentJavaFilePath.contains(key)) {
                // 当前java文件的层级
                Integer currentJavaFieLevel = fileRelativeLevel.get(key);

                for (String classFileKey : keySet) {
                    // 存在多个文件名相同的情况下使用，由于一个文件夹不会存在同名文件，不用担心Key重复

                    if (classFileKey.contains("/" + targetClassName + ".java")) {
                        // 不在import之中，却在文件夹中,层级相同，那么应该是在相同文件中
                        if (!inImport) {
                            return "import " + targetClassName + " = require(\"./" + targetClassName + "\");\n";
                        }
                        // 剩下的都是在导入之中
                        // 需要引入的文件的层级
                        // 当前文件夹应该是以subffixString的值结尾
                        String suffixString = classFileKey.replace(".java", "").replace("/", ".");
                        boolean rightClassFileKey = false;
                        ImportDeclaration targetClassImport = null;
                        for (ImportDeclaration anImport : imports) {
                            String tempImportString = anImport.toString();
                            tempImportString = tempImportString.replace("\n","").replace(";","");
                            // com.diandaxia.common.sdk.taobao
                            // com.diandaxia.common.sdk.douyin.bean.Order
                            if(tempImportString.endsWith(targetClassName)  ){
                                if( tempImportString.endsWith(suffixString)){
                                    targetClassImport = anImport;
                                    rightClassFileKey = true;
                                    break;
                                }

                            }
                        }

                        if(!rightClassFileKey){
                            continue;
                        }

                        Integer targetClassLevel = fileRelativeLevel.get(classFileKey);
                        if (currentJavaFieLevel.intValue() == targetClassLevel.intValue()) {
                            // 不在import之中，却在文件夹中,层级相同，那么应该是在相同文件中

                            // 在import中，却需要导入那么就是其他文件夹，只是层级一样
                            StringBuilder returnString = new StringBuilder();
                            returnString.append("import ").append(targetClassName).append(" = require(\"");
                            // 进入根目录
                            for (int i = 0; i < currentJavaFieLevel - 1; i++) {
                                returnString.append("../");
                            }
                            returnString.append(classFileKey.replace(".java", ""));
                            returnString.append("\");\n");
                            return returnString.toString().replace("//", "/");


                        } else if (currentJavaFieLevel > targetClassLevel) {
                            // 当前文件在树的更里面
                            // TODO 树的更里面需要怎么处理
                            StringBuilder returnString = new StringBuilder();
                            returnString.append("import ").append(targetClassName).append(" = require(\"");
                            // 层级差距
                            int value = currentJavaFieLevel - targetClassLevel;
                            for (int i = 0; i < value; i++) {
                                returnString.append("../");
                            }
                            returnString.append(targetClassName).append("\");\n");
                            return returnString.toString();
                        } else {
                            StringBuilder returnString = new StringBuilder();
                            returnString.append("import ").append(targetClassName).append(" = require(\"");
                            // 当 当前文件的等级是1级时，第一个import不能是../
                            String targetClassImportString = targetClassImport.getName().toString();
                            if(targetClassImportString.startsWith(packageString)){
                                // targetClassImportString是当前文件的所在目录的子目录中
                                returnString.append(".");
                                returnString.append( targetClassImportString.replace(packageString+".","/").replace(".","/"));
                                returnString.append("\");\n");
                                return returnString.toString().replace("//", "/");
                            }

                                // 层级差距
                                int value = targetClassLevel - currentJavaFieLevel;
                            if (currentJavaFieLevel == 1){
                                for (int i = 0; i < value; i++) {
                                    if (i == 0) {
                                        returnString.append("./");
                                    } else {
                                        returnString.append("../");
                                    }
                                }
                            }else{
                                for (int i = 0; i < value; i++) {
                                        returnString.append("../");
                                }
                            }




                                StringBuilder javaFilePath = new StringBuilder(classFileKey);
                                javaFilePath.replace(javaFilePath.length() - 5, javaFilePath.length(), "");
                                returnString.append(javaFilePath.toString());
                                returnString.append("\");\n");
                                return returnString.toString().replace("//", "/");



                        }
                    }


                }
            }

        }
        return null;
    }

    /**
     * 获取构造方法字符串
     *
     * @param fieldMap  字段键值对，key是字段的名称，value是字段的类型
     * @param isExtends 是否是继承
     * @return 构造方法字符串
     */
    public static String getConstructorTemplate(Map<String, String> fieldMap, boolean isExtends) {
        StringBuilder constructorTemplate = new StringBuilder();
        constructorTemplate.append("    constructor(");
        List<String> parameterStrList = new LinkedList<>();
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            parameterStrList.add(entry.getKey() + "?: " + entry.getValue());
        }
        String join = String.join(", ", parameterStrList);
        constructorTemplate.append(join);
        constructorTemplate.append(") {\n");
        if (isExtends) {
            constructorTemplate.append("super();\n");
        }

        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            constructorTemplate.append("        this.").append(entry.getKey()).append(" = ").append(entry.getKey()).append(";\n");
        }
        constructorTemplate.append("    }\n");
        return constructorTemplate.toString();
    }

    /**
     * 获取一个文件选择描述器
     *
     * @param title       标题
     * @param description 描述
     * @return FileChooserDescriptor
     */
    public static FileChooserDescriptor createFileChooserDescriptor(String title, String description) {
        FileChooserDescriptor singleFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        if (title != null) {
            singleFolderDescriptor.setTitle(title);
        }
        if (description != null) {
            singleFolderDescriptor.setDescription(description);
        }
        return singleFolderDescriptor;
    }

    /**
     * 转换成键值对，键是源文件夹 ，值是目标文件夹
     *
     * @param folderMappingList 配置文件中的映射list，包含数显竖线的
     * @return 转换之后的键值对
     */
    public static Map<String, String> convertToFolderMap(List<String> folderMappingList) {
        Map<String, String> folderMap = new LinkedHashMap<>();
        if (folderMappingList == null || folderMappingList.isEmpty()) {
            return folderMap;
        }
        for (String folderMapping : folderMappingList) {
            String[] folders = folderMapping.split(TemplateUtil.FOLDER_SPLIT);
            folderMap.put(folders[0], folders[1]);
        }
        return folderMap;
    }

    /**
     * 通过分析members获取类中的请求字段
     * @param members 类中的member
     * @return List<TbRequestFieldEntity>
     */
    public static List<TbRequestFieldEntity> getRequestFieldListFromMembers(List<BodyDeclaration> members){
        List<TbRequestFieldEntity> fieldList = new ArrayList<>();
        members.forEach(member ->{

            if(member instanceof FieldDeclaration){
                TbRequestFieldEntity requestField = new TbRequestFieldEntity();
                FieldDeclaration field = (FieldDeclaration) member;
                String fieldName = field.getVariables().get(0).getId().getName();
                requestField.setFieldName(fieldName);
                requestField.setFieldType(field.getType().toString());

                List<AnnotationExpr> annotations = field.getAnnotations();
                if (annotations != null && !annotations.isEmpty()){
                    annotations.forEach(annotation ->{
                        String name = annotation.getName().getName();
                        requestField.setRequired("NotNull".equals(name) || "NotBlank".equals(name));
                    });
                }
                JavadocComment javaDoc = field.getJavaDoc();
                if (javaDoc != null){
                    String content = javaDoc.getContent();
                    requestField.setDescription(content);
                }
                fieldList.add(requestField);
            }
        });

        return fieldList;
    }

    /**
     * 从members中获取字段
     * @param members 类的成员
     * @param currentJavaFileFolderPath 当前java文件所在文件夹的路径
     * @return
     */
    public static List<TbResponseFieldEntity> getResponseFieldListFromMembers(List<BodyDeclaration> members, String currentJavaFileFolderPath){
        List<TbResponseFieldEntity> responseFieldList = new ArrayList<>();
        members.forEach(member ->{
            if(member instanceof FieldDeclaration){
                TbResponseFieldEntity responseFieldEntity = new TbResponseFieldEntity();
                FieldDeclaration field = (FieldDeclaration) member;

                String fieldName = field.getVariables().get(0).getId().getName();
                responseFieldEntity.setFieldName(fieldName);
                String fieldTypeString = field.getType().toString();
                if(field.getType() instanceof PrimitiveType){
                    // 基本数据类型 直接放进去
                    responseFieldEntity.setFieldType(fieldTypeString);
                }else{
                    // 引用数据类型
                    if (!sourceType.contains(fieldTypeString)){
                        // TODO 不知名的类，判断import，查找类
                        ReferenceType fieldType = (ReferenceType) field.getType();
                        int arrayCount = fieldType.getArrayCount();
                        if(arrayCount == 1){
                            //
                            if(fieldType.getType() instanceof PrimitiveType){
                                responseFieldEntity.setFieldType(fieldTypeString);
                            }else{
                                {
                                    ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) fieldType.getType();
                                    String name = classOrInterfaceType.getName();
                                    fieldTypeString = name;
                                    if(fieldTypeString.equals("List")){
                                        fieldTypeString = getReturnType(classOrInterfaceType.getTypeArgs().get(0));
                                        responseFieldEntity.setFieldType("Object[]");
                                    }else{
                                        responseFieldEntity.setFieldType("Object");
                                    }
                                    List<String> allJavaFileInCurrentFolder = getAllJavaFile(currentJavaFileFolderPath, false);
                                    String javaFilePathOfFieldType = null;
                                    for (String s : allJavaFileInCurrentFolder) {
                                        if (s.endsWith(fieldTypeString+".java")){
                                            javaFilePathOfFieldType = s;
                                            break;
                                        }
                                    }
                                    if (javaFilePathOfFieldType == null){
                                        NotificationUtil.createStickyNotification(fieldTypeString+".java无法找到",NotificationUtil.ERROR);
                                    }else{
                                        try {
                                            List<TbResponseFieldEntity> nodeFieldList = getNodeFieldList(javaFilePathOfFieldType);
                                            responseFieldEntity.setNodeFieldList(nodeFieldList);
                                        } catch (IOException | ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }

                        }else{
                            ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) fieldType.getType();
                            String name = classOrInterfaceType.getName();
                            fieldTypeString = name;
                            if(fieldTypeString.equals("List")){
                                fieldTypeString = getReturnType(classOrInterfaceType.getTypeArgs().get(0));
                                responseFieldEntity.setFieldType("Object[]");
                            }else{
                                responseFieldEntity.setFieldType("Object");
                            }
                            List<String> allJavaFileInCurrentFolder = getAllJavaFile(currentJavaFileFolderPath, false);
                            String javaFilePathOfFieldType = null;
                            for (String s : allJavaFileInCurrentFolder) {
                                if (s.endsWith(fieldTypeString+".java")){
                                    javaFilePathOfFieldType = s;
                                    break;
                                }
                            }
                            if (javaFilePathOfFieldType == null){
                                NotificationUtil.createStickyNotification(fieldTypeString+".java无法找到",NotificationUtil.ERROR);
                            }else{
                                try {
                                    List<TbResponseFieldEntity> nodeFieldList = getNodeFieldList(javaFilePathOfFieldType);
                                    responseFieldEntity.setNodeFieldList(nodeFieldList);
                                } catch (IOException | ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }




                    }else{
                        responseFieldEntity.setFieldType(fieldTypeString);
                    }
                }




                responseFieldList.add(responseFieldEntity);
            }
        });

        return responseFieldList;
    }

    /**
     * 获取子节点的字段
     * @param javaFilePathOfFieldType 字段的类型的所属的java文件的路径
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static List<TbResponseFieldEntity> getNodeFieldList(String javaFilePathOfFieldType) throws IOException, ParseException {
        File fieldFile = new File(javaFilePathOfFieldType);
        CompilationUnit parse = JavaParser.parse(fieldFile, "utf-8");
        String name = parse.getTypes().get(0).getName();
        List<BodyDeclaration> members = parse.getTypes().get(0).getMembers();
        String replace = javaFilePathOfFieldType.replace("/" + name + ".java", "");
        return TemplateUtil.getResponseFieldListFromMembers(members, replace);
    }

    //--------------------------------------------------

    /**
     * 字符串工具
     *
     * @param str 目标字符串
     * @param sub 需要匹配的字符串
     * @return 匹配次数
     */
    public static int countMatches(CharSequence str, CharSequence sub) {
        if (!isEmpty(str) && !isEmpty(sub)) {
            int count = 0;

            for (int idx = 0; (idx = indexOf(str, sub, idx)) != -1; idx += sub.length()) {
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

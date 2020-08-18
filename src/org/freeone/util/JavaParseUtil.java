package org.freeone.util;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.*;

/**
 * 使用github的javapartsr
 */
public class JavaParseUtil {
    public static CompilationUnit parseJavaFile(String javaFilePath) throws IOException, ParseException {
        return JavaParser.parse(new BufferedReader( new InputStreamReader(  new FileInputStream(javaFilePath),"UTF-8")) ,true);
    }

    public static CompilationUnit parseJavaFile(File file) throws IOException, ParseException {
        return JavaParser.parse(new BufferedReader( new InputStreamReader(  new FileInputStream(file),"UTF-8")) ,true);
    }

}

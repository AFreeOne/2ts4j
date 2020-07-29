public class MyClassLoader extends ClassLoader{

    private String classDir;  // 文件目录，例如:"file:/today/javadir/src/main/java/"
    @Override
    public Class<?> findClass(String name) {

        return null;
    }

    public MyClassLoader(String classDir) {
        this.classDir = "file:/".concat(classDir).concat("/src/main/java/");  //拼接 “file:/”前缀
    }


}

package com.wncud.runtime;

import net.openhft.compiler.CachedCompiler;

/**
 * Created by yajunz on 2014/11/5.
 */
public class CompilerTest{
    public static void main(String[] args) {
        CachedCompiler compiler = new CachedCompiler(null, null);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package com.wncud.runtime;\n");
        stringBuilder.append("public class MyTest implements MyApi{\n");
        stringBuilder.append("public void test(){System.out.println(\"Hello\");}\n");
        stringBuilder.append("}");
        try {
            Class aClass = compiler.loadFromJava("com.wncud.runtime.MyTest", stringBuilder.toString());
            MyApi api = (MyApi)aClass.newInstance();

            api.test();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

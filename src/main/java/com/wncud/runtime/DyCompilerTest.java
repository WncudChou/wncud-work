package com.wncud.runtime;

import net.openhft.compiler.CachedCompiler;

import java.io.*;

/**
 * Created by yajunz on 2014/11/28.
 */
public class DyCompilerTest {

    public void parse(String code){
        CachedCompiler compiler = new CachedCompiler(null, null);

        //String code = "System.out.println(\"Hello\");";

        DyCompilerTest dyCompilerTest = new DyCompilerTest();

        //String path = dyCompilerTest.getClass().getClassLoader().getResource("/").getPath();
        //path += File.separator + "log-parser.properties";
        String path = "E:\\workspace\\wncud-work\\target\\classes";
        path += File.separator + "log-parser.properties";
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            StringBuffer buffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            String parseCode = buffer.toString();
            parseCode = parseCode.replace("${parseCode}", code);

            Class aClass = compiler.loadFromJava("com.wncud.runtime.MyTest", parseCode);
            MyApi api = (MyApi)aClass.newInstance();

            api.test();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CachedCompiler compiler = new CachedCompiler(null, null);

        String code = "System.out.println(\"Hello\");";

        DyCompilerTest dyCompilerTest = new DyCompilerTest();

        //String path = dyCompilerTest.getClass().getClassLoader().getResource("/").getPath();
        //path += File.separator + "log-parser.properties";
        String path = "E:\\workspace\\wncud-work\\target\\classes";
        path += File.separator + "log-parser.properties";
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            StringBuffer buffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            String parseCode = buffer.toString();
            parseCode = parseCode.replace("${parseCode}", code);

            Class aClass = compiler.loadFromJava("com.wncud.runtime.MyTest", parseCode);
            MyApi api = (MyApi)aClass.newInstance();

            api.test();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

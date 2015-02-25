package com.wncud.runtime_test;

import com.wncud.runtime.DyCompilerTest;

/**
 * Created by yajunz on 2014/11/28.
 */
public class MainTest implements Runnable {
    public static void main(String[] args) {
        /*DyCompilerTest dyCompilerTest = new DyCompilerTest();
        String code = "System.out.println(\"Hello\");";
        dyCompilerTest.parse(code);*/

        MainTest mainTest = new MainTest();
        Thread thread = new Thread(mainTest);
        thread.start();
    }

    @Override
    public void run() {
        DyCompilerTest dyCompilerTest = new DyCompilerTest();
        String code = "System.out.println(\"Hello\");";
        dyCompilerTest.parse(code);
    }
}

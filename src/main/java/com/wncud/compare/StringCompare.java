package com.wncud.compare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouyajun on 2015/12/2.
 */
public class StringCompare {
    public static void main(String[] args) {
        //String regex = "^\\[";
        String regex = "^\\[\\s*([A-Z]+)\\s*\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher("[INFO 2012]");
        System.out.println(m.matches());
        System.out.println(m.lookingAt());
        System.out.println(m.find());

        m = pattern.matcher("[ ERROR ] dada;j [zhouyu]");
        /*System.out.println(m.matches());
        System.out.println(m.lookingAt());*/
        if(m.find()){
            System.out.printf(m.group(1));
        }

        m = pattern.matcher("[ERROR] 2015-12-30 15:16:25.60 [com.wncud.log4j.LogInvoker.main(LogInvoker.java:30):2051152] 2050");
        /*System.out.println(m.matches());
        System.out.println(m.lookingAt());*/
        if(m.find()){
            System.out.printf(m.group(1));
        }
        //System.out.println(m.find());
    }
}

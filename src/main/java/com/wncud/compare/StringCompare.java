package com.wncud.compare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouyajun on 2015/12/2.
 */
public class StringCompare {
    public static void main(String[] args) {
        String regex = "^\\[\\s*[IEWDT]";
        //String regex = "^\\[\\s*([A-Z]+?)\\s*\\]";
        //String regex = "^\\[.+?\\](.+?)\\[";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher("[ ERROR 2012]");
        System.out.println(m.matches());
        System.out.println(m.lookingAt());
        System.out.println(m.find());

        m = pattern.matcher("[ ERROR ] dada;j [zhouyu]");
        /*System.out.println(m.matches());
        System.out.println(m.lookingAt());*/
        if(m.find()){
            System.out.println(m.group(1));
        }

        //m = pattern.matcher("[ERROR] 2015-12-30 15:16:25.60 [com.wncud.log4j.LogInvoker.main(LogInvoker.java:30):2051152] 2050");
        //m = pattern.matcher("[DEBUG] 2016-01-12 11:23:07.365 [org.mybatis.spring.SqlSessionFactoryBean] - Parsed mapper file: 'URL [jar:file:/home/dmadmin/liuh/producer/spotmix-producer/lib/spotmix-dao-1.0-SNAPSHOT.jar!/mybatis/Department.xml]' ");
        m = pattern.matcher("[INFO] 01-08 19:17:00.000 [ 取消时间纬度取消订单调度器[CancelExpireOrderNewWorker]_Worker-4][LOG_CANCEL_ORDER_WORKER]com.wm.order.man.service.jobs.CancelExpireOrderNewWorker:50 - [CancelExpireOrderNewWorker]取消订单调度任务开始");
        /*System.out.println(m.matches());
        System.out.println(m.lookingAt());*/
        if(m.find()){
            System.out.println(m.group(1));
        }
        //System.out.println(m.find());
    }
}

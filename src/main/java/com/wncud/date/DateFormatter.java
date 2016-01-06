package com.wncud.date;

import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.DateTimeZone;

import java.util.Date;

/**
 * Created by zhouyajun on 2015/9/22.
 */
public class DateFormatter {
    public static void main(String[] args) {
        System.out.println(DateTime.now(DateTimeZone.UTC).toString("yyyy/MM/dd HH:mm:ss.SSS"));;
        DateTime dateTime = new DateTime("2015-09-21T07:13:08Z");
        System.out.printf(dateTime.toString("yyyy/MM/dd HH:mm:ss.SSS"));


        System.out.println(DateTime.now().millisOfDay().withMinimumValue().toString("yyyy/MM/dd HH:mm:ss.SSS"));
    }
}

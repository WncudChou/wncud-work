package com.wncud;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yajunz on 2015/1/23.
 */
public class TimeZone {
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CHINA);
        sdf.setTimeZone(java.util.TimeZone.getDefault());
        System.out.println(sdf.format(new Date()));
    }
}

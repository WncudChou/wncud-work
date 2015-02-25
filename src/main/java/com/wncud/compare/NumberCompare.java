package com.wncud.compare;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.util.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yajunz on 2015/1/14.
 */
public class NumberCompare {
    private static Boolean isInScope(String scope, String value) {
        scope = scope.replace(" ", "");
        Pattern pattern = Pattern.compile("^(\\(|\\[)((-?\\d+)(\\.\\d+)?|\\*),\\s*((-?\\d+)(\\.\\d+)?|\\*)(\\)|\\])$");
        Matcher matcher = pattern.matcher(scope);
        if (!matcher.find() && StringUtils.isNumeric(value)) {
        return false;
        }

        String tmp;

        String[] scopes = scope.split(",");
        Double valueF = Double.valueOf(value);
        tmp = scopes[0].substring(1);
        if(!"*".equals(tmp)){
            Double min = Double.valueOf(tmp);
            if ("(".equals(String.valueOf(scopes[0].charAt(0))))
            {
                if (valueF <= min)
                    return false;
            }
            else if ("[".equals(String.valueOf(scopes[0].charAt(0))))
            {
                if (valueF < min)
                    return false;
            }
        }

        tmp = scopes[1].substring(0, scopes[1].length() - 1);
        if(!"*".equals(tmp)){
            Double max = Double.valueOf(tmp);
            if (")".equals(String.valueOf(scopes[1].charAt(scopes[1].length() - 1))))
            {
                if (valueF >= max)
                    return false;
            }
            else if ("]".equals(String.valueOf(scopes[1].charAt(scopes[1].length() - 1))))
            {
                if (valueF > max)
                    return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        System.out.println(NumberCompare.isInScope("[8 ,12]", "11.5"));
    }
}

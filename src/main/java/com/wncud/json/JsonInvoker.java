package com.wncud.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zhouyajun on 2015/10/15.
 */
public class JsonInvoker {
    @Test
    public void testGetJson(){
        String path = "E:\\me.json";
        File file = new File(path);
        try {
            FileReader fileReader = new FileReader(file);
            int fileLen = (int) file.length();
            char[] chars = new char[fileLen];
            fileReader.read(chars);
            String txt = String.valueOf(chars);
            JSONObject jsonObject = JSON.parseObject(txt);
            jsonObject.size();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

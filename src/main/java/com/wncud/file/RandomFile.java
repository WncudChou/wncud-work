package com.wncud.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by yajunz on 2014/12/11.
 */
public class RandomFile {
    public static void main(String[] args) {
        try {
            RandomAccessFile accessFile = new RandomAccessFile("G:\\log\\filelog-test.log", "r");
            accessFile.seek(0);
            String content;
            content = new String(accessFile.readLine().getBytes("8859_1"), "utf-8");
            System.out.println(content);
            accessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

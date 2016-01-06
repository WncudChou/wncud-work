package com.wncud.file;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;

/**
 * Created by zhouyajun on 2015/12/1.
 */
public class PathInvoker {
    public static void main(String[] args) {
        Path path = Paths.get("E:\\test\\log\\a_log");
        if(Files.isDirectory(path)){
            try {
                DirectoryStream<Path> stream =  Files.newDirectoryStream(path);
                for(Path item : stream){
                    FileTime time =  Files.getLastModifiedTime(item);
                    System.out.println(item + "##" + time);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

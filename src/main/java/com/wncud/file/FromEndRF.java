package com.wncud.file;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by zhouyajun on 2016/1/27.
 */
public class FromEndRF {

    /**
     *
     * @param filename Ŀ���ļ�
     * @param charset Ŀ���ļ��ı����ʽ
     */
    public static void read(String filename, String charset) {

        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(filename, "r");
            long len = rf.length();
            long start = rf.getFilePointer();
            long nextend = start + len - 1;
            String line;
            rf.seek(nextend);
            int c = -1;
            while (nextend > start) {
                c = rf.read();
                if (c == '\n' || c == '\r') {
                    line = rf.readLine();
                    if (line != null) {
                        System.out.println(new String(line
                                .getBytes("ISO-8859-1"), charset));
                    } else {
                        System.out.println(line);
                    }
                    nextend--;
                }
                nextend--;
                rf.seek(nextend);
                if (nextend == 0) {// ���ļ�ָ�������ļ���ʼ���������һ��
                    // System.out.println(rf.readLine());
                    System.out.println(new String(rf.readLine().getBytes(
                            "ISO-8859-1"), charset));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rf != null)
                    rf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        read("E:\\test\\xx.txt", "UTF-8");
    }
}
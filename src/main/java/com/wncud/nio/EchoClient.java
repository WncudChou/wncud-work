package com.wncud.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * Created by zhouyajun on 2016/1/27.
 */
public class EchoClient {
    public static void main(String[] args) throws Exception{
        //1. 准备一个非阻塞 SocketChannel
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        //2. 建立 SocketChannel 接收数据的缓冲区 、 缓冲区写入的 FileChannel
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.connect(new InetSocketAddress("127.0.0.1",9091));
        while(!channel.finishConnect());    //判断 SocketChannel 是否连接成功，如果没连接成功就一直循环
        System.out.println("Client Connected");
        Scanner inScanner = new Scanner(System.in);
        boolean isRead = false;
        while(true){
            String str = inScanner.next();
            buffer.clear();
            buffer.put(str.getBytes("UTF-8"));
            buffer.flip();  //切换成读模式
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            buffer.clear();
            while(true){
                while(channel.read(buffer) > 0){
                    isRead = true;
                }
                if(isRead && channel.read(buffer) <= 0){
                    break;
                }
            }
            isRead = false;
            buffer.flip();
            str = new String(buffer.array(),0,buffer.limit(),"UTF-8");
            buffer.clear();
            System.out.println(str);
        }
    }
}

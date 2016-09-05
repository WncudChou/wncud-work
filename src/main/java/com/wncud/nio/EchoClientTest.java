package com.wncud.nio;

import com.wncud.protobuf.PersonMsg;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * Created by zhouyajun on 2016/1/27.
 */
public class EchoClientTest {
    public static void main(String[] args) throws Exception{

        byte[] xx = new byte[10];
        ByteBuffer test = ByteBuffer.allocate(10);
        test.clear();
        test.put(xx, 8, 2);

        //1. 准备一个非阻塞 SocketChannel

        int capacity = 1024*1024;
        SocketChannel channel = null;
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            //2. 建立 SocketChannel 接收数据的缓冲区 、 缓冲区写入的 FileChannel
            ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
            channel.connect(new InetSocketAddress("127.0.0.1", 9091));
            while(!channel.finishConnect());    //判断 SocketChannel 是否连接成功，如果没连接成功就一直循环
            System.out.println("Client Connected");
            buffer.clear();
            PersonMsg.Person.Builder builder = PersonMsg.Person.newBuilder();
            for(int i = 0; i < 10000; i++){
                builder.setId(i);
                builder.setName("wncud" + i);
                builder.setEmail("dlajfdlassj");
                //builder.clearFriends();
                builder.addFriends("Friend A");
                builder.addFriends("Friend B");
                PersonMsg.Person person = builder.build();
                //System.out.println(person.toByteArray().length);
                byte[] data = person.toByteArray();
                //buffer.clear();
                System.out.println(buffer.position() + ":" + data.length);
                buffer.putInt(data.length);
                int bodyLength = data.length;
                int position = 0;
                int limit = bodyLength > capacity-4 ?  capacity-4 : bodyLength;

                while(position < bodyLength){
                    buffer.put(data, position, limit);
                    buffer.flip();
                    while (buffer.remaining() > 0){
                        channel.write(buffer);
                    }
                    buffer.clear();
                    position = position + limit;
                    limit = bodyLength - position > capacity ? capacity : bodyLength - position;
                }

                Thread.sleep(200);
            }

            Thread.sleep(100000);
        } finally {
            if(channel != null){
                channel.close();
            }
        }
    }
}

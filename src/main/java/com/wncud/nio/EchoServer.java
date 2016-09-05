package com.wncud.nio;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wncud.protobuf.PersonMsg;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouyajun on 2016/1/27.
 */
public class EchoServer {
    Map<String, ByteBuffer> dataBufferMap = new HashMap<>();
    Map<String, Integer> dataLengthMap = new HashMap<>();
    public static void main(String[] args)throws Exception {
        EchoServer echoServer = new EchoServer();
        echoServer.init();
    }

    public void init() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(9091);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(address);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while(true){
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){
                    ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel)key.channel();
                    SocketChannel channel = serverSocketChannel1.accept();
                    if(channel != null){
                        channel.configureBlocking(false);
                        channel.register(selector,SelectionKey.OP_READ);
                    }
                }
                else if(key.isReadable()){  //当客户端发送数据给服务器
                    SocketChannel channel = (SocketChannel)key.channel();
                    receiveData(channel);
                }
                iterator.remove();//Selector的触发事件集合不会自己删除，需要手动删除
            }
        }
    }

    private void receiveData(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
        buffer.clear();
        while(true){
            int count = channel.read(buffer);
            if(count <= 0){
                buffer.flip();
                encode(buffer);
                break;
            }
        }
    }

    private void encode(ByteBuffer buffer){
        while (buffer.remaining() > 0){
            try {
                buffer.mark();
                int length = buffer.getInt();
                if(buffer.remaining() < length){
                    buffer.reset();
                    break;
                }
                else {
                    byte[] data = new byte[length];
                    buffer.get(data);
                    PersonMsg.Person person = PersonMsg.Person.parseFrom(data);

                    //TODO 消息处理
                    System.out.println(person.getName());
                    Thread.sleep(200);
                }
            } catch (Exception e) {
                //TODO 错误处理
            }
        }
        buffer.compact();
    }
}

package com.wncud.thrift.service;

import com.wncud.thrift.Hello;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by yajunz on 2014/10/13.
 */
public class NIOHelloService {
    public static void main(String[] args) {
        try {
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(10005);
            Hello.Processor processor = new Hello.Processor(new HelloServiceImpl());
            TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));
            server.serve();
            System.out.println("server start...");
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}

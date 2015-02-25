package com.wncud.thrift.service;

import com.wncud.thrift.Hello;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by yajunz on 2014/10/13.
 */
public class HelloService {
    public static void main(String[] args) {
        try {
            TServerSocket serverSocket = new TServerSocket(7911);
            TProcessor processor = new Hello.Processor<HelloServiceImpl>(new HelloServiceImpl());
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverSocket).processor(processor));
            ThreadLocal<TServer> local = new ThreadLocal<TServer>();
            local.set(server);
            server.serve();

            System.out.println("server start ....");
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}

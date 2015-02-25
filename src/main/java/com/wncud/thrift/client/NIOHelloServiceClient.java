package com.wncud.thrift.client;

import com.wncud.thrift.Hello;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by yajunz on 2014/10/13.
 */
public class NIOHelloServiceClient {
    public static void main(String[] args) {
        try {
            TTransport transport = new TFramedTransport(new TSocket("localhost", 10005));
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            Hello.Client client = new Hello.Client(protocol);
            String msg = client.helloString("wncud xx");
            System.out.println(msg);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}

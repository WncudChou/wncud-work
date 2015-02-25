package com.wncud.thrift.client;

import com.wncud.thrift.Hello;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by yajunz on 2014/10/13.
 */
public class HelloServiceClient {
    public static void main(String[] args) {
        try {
            TTransport transport = new TSocket("localhost", 7911);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            Hello.Client client = new Hello.Client(protocol);
            String msg = client.helloString("wncud");
            int num = client.helloInt(8);
            System.out.println(msg + ">" + num);
            transport.close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }

    }
}

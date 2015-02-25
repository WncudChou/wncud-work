package com.wncud.thrift.service;

import com.wncud.thrift.Hello;
import org.apache.thrift.TException;

/**
 * Created by yajunz on 2014/10/13.
 */
public class HelloServiceImpl implements Hello.Iface {
    @Override
    public String helloString(String para) throws TException {
        return "hello : " + para;
    }

    @Override
    public int helloInt(int para) throws TException {
        return para;
    }

    @Override
    public boolean helloBoolean(boolean para) throws TException {
        return para;
    }
}

package com.wncud.dubbo.impl;

import com.wncud.dubbo.DemoService;

/**
 * Created by zhouyajun on 2015/11/27.
 */
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "hello " + name;
    }
}

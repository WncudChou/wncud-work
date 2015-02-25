package com.wncud.hessian.service;

/**
 * Created by yajunz on 2014/10/11.
 */
public class HessianServiceImpl implements HessianService {
    @Override
    public String sayHello(String name) {
        return "hello " +name;
    }
}

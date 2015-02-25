package com.wncud.hessian;

import com.caucho.hessian.client.HessianProxyFactory;
import com.wncud.hessian.service.HessianService;
import org.junit.Test;

import java.net.MalformedURLException;

/**
 * Created by yajunz on 2014/10/11.
 */
public class HessianServiceTest {
    private final static String url = "http://localhost:8080/service";

    @Test
    public void testSayHello(){
        HessianProxyFactory proxyFactory = new HessianProxyFactory();
        try {
            HessianService hessianService = (HessianService) proxyFactory.create(HessianService.class, url);
            System.out.println(hessianService.sayHello("wncud"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

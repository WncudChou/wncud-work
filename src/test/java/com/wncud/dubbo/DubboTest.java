package com.wncud.dubbo;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhouyajun on 2015/11/27.
 */
public class DubboTest {
    @Test
    public void testDubbo(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/spring-dubbo.xml");
        DemoService demoService = context.getBean("testDemoService", DemoService.class);
        String text = demoService.sayHello("zhouyu");
        System.out.println(text);
    }
}

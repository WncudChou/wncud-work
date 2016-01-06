package com.wncud.mail;

import com.wncud.email.ApplicationMailer;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhouyajun on 2015/9/29.
 */
public class MailerTest {
    @Test
    public void testSendMail(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/spring-dao.xml");
        ApplicationMailer applicationMailer = context.getBean("applicationMailer", ApplicationMailer.class);
        String content = "<html><body><h1 style='color:red'>hello 中文</h1></body></html>";
        applicationMailer.sendMailByTemplate("yajun.zhou@dmall.com", "test send email 中文", content);
    }
}

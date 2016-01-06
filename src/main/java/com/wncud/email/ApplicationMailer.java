package com.wncud.email;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouyajun on 2015/9/29.
 */
@Service(value = "applicationMailer")
public class ApplicationMailer {
    @Resource(name = "mailSender")
    private JavaMailSender mailSender;
    @Resource(name = "velocityEngine")
    private VelocityEngine velocityEngine;

    public void sendMail(String to, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("mid-ware@dmall.com");
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendMail2(String to, String subject, String body){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            messageHelper.setFrom("mid-ware@dmall.com");
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(mimeMessage);
    }
    public void sendMailByTemplate(String to, String subject, String contont){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            messageHelper.setFrom("mid-ware@dmall.com");
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            Map<String, Object> model = new HashMap<>();
            model.put("name", "zhouyu");
            model.put("age", "27");
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                    "/velocity/email_template.vm", "utf-8", model);
            messageHelper.setText(text, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(mimeMessage);
    }

}

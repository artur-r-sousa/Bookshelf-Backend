package com.bookshelf.bookshelf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.thymeleaf.TemplateEngine;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("artursousa505@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
    }

    public void sendConfirmationEmail(String to, String name, String confirmationLink, String template) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("link", confirmationLink);
        
        String body = templateEngine.process(template, context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("artursousa505@gmail.com");
        helper.setTo(to);
        helper.setSubject("Confirmação de Cadastro");
        helper.setText(body, true);

        mailSender.send(message);
    }
}

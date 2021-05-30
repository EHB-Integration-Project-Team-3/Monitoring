package com.brielage.monitor.Mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public enum MailSender {
    ;

    private static final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    static {
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("email");
        mailSender.setPassword("pass");

        Properties props = mailSender.getJavaMailProperties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
    }

    public static void sendMail(String to,
                                String subject,
                                String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@monitoring.nogiet");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
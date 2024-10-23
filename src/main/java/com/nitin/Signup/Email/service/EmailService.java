package com.nitin.Signup.Email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;


    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException{
        /* MimeMessage vs MimeMailMessage*/
        //-> MIME allows more formats sent on email: images, file, videos, exe files, HTML etc. Extension of Simple Mails.

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message); //-> Helper class to Populate MimeMessage

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true); //-> IMU, here MIME is useful (in our case) as mail will be having HTML.

        javaMailSender.send(message);
    }
}

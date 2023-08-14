package com.example.demo.Service;


import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

@Service
public class EmailService {

    public boolean sendEmailWithAttachment(String to, String from, String subject, String text, File file) {
        boolean flag = false;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.host", "smtp.gmail.com");

        String username = "customerportal45"; //username before @gmail.com
        String password = "rlpoqmtdvsyvegot"; //App password

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);

            MimeBodyPart part1 = new MimeBodyPart();
            part1.setText(text);

            MimeBodyPart part2 = new MimeBodyPart();
            part2.attachFile(file);

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(part1);
            multipart.addBodyPart(part2);

            message.setContent(multipart);

            Transport.send(message);
            flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }
    
    public boolean sendPasswordResetVerificationEmail(String to, String from, String subject, String verificationLink) {
        boolean flag = false;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.host", "smtp.gmail.com");

        String username = "customerportal45"; // Username before @gmail.com
        String appPassword = "rlpoqmtdvsyvegot"; // App password

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);

            MimeBodyPart part1 = new MimeBodyPart();
            part1.setText("To verify your password reset request, please click the link below:\n" + verificationLink);

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(part1);

            message.setContent(multipart);

            Transport.send(message);
            flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }


}

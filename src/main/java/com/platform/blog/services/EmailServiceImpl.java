package com.platform.blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    /*
    * @Inject an instance of MailContentBuilder
    * */
    @Autowired
    private MailContentBuilder mailContentBuilder;

    /*
    * @Inject an instance of JavaMailSender which we have created as a bean
    */
    @Autowired
    private JavaMailSender mailSender;

    /*
    * @Send simple email message implementation
    */
    @Override
    public boolean sendSimpleMessage(String to, String subject, String text) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            this.mailSender.send(message);
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }

    /*
     * @Send professional templated email message implementation
     */
    @Override
    public boolean sendProfessionalMessage(String sender, String to, String subject, String text, Map<String, Object> data) {
        try{
            MimeMessage message = this.mailSender.createMimeMessage();
            String templatedText = this.mailContentBuilder.build(sender, to, subject, text, data);
            MimeMessageHelper helper= new MimeMessageHelper(message);
            helper.setTo(to);
            helper.setSubject(subject);
            // Set true for html
            helper.setText(templatedText, true);
            this.mailSender.send(helper.getMimeMessage());
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }

    /*
    * @Send email with attachment implementation
    */
    @Override
    public boolean sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        try {
            MimeMessage message = this.mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file
                    = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }



}

package com.platform.blog.services;

import java.util.Map;

public interface EmailService {

    public boolean sendSimpleMessage(String to, String subject, String text);
    public boolean sendProfessionalMessage(String sender, String to, String subject, String text, Map<String, Object> data);
    public boolean sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment);

}

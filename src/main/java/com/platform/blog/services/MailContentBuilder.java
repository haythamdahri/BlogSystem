package com.platform.blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

public interface MailContentBuilder {

    public String build(String sender, String to, String subject, String message, Map<String, Object> data);

}

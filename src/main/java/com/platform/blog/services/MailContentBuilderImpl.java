package com.platform.blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class MailContentBuilderImpl implements MailContentBuilder{

    private TemplateEngine templateEngine;

    /*
    * @Inject an instance of templateEngine
    */
    @Autowired
    public MailContentBuilderImpl(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(String sender, String to, String subject, String text, Map<String, Object> data) {
        Context context = new Context();
        context.setVariable("appTitle", subject);
        context.setVariable("sender", sender);
        context.setVariable("to", to);
        context.setVariable("text", text);
        context.setVariable("data", data);
        return templateEngine.process("mailing/postMailTemplate", context);
    }

}

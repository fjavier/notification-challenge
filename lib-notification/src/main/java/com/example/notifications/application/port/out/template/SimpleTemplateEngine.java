package com.example.notifications.application.port.out.template;

import java.util.Map;

public class SimpleTemplateEngine implements TemplateEngine {

    @Override
    public String render(String template, Map<String, Object> variables) {
        String result = template;
        for (var entry : variables.entrySet()) {
            result = result.replace(
                    "{{" + entry.getKey() + "}}",
                    String.valueOf(entry.getValue())
            );
        }
        return result;
    }
}

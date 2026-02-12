package com.example.notifications.application.port.out.template;

import java.util.Map;

public interface TemplateEngine {
    String render(String template, Map<String, Object> variables);
}

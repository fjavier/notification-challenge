package org.challenge.demo;

import com.example.notifications.application.port.out.push.PushGateway;
import com.example.notifications.application.port.out.template.SimpleTemplateEngine;
import com.example.notifications.application.registry.NotifierRegistry;
import com.example.notifications.application.service.EmailNotifier;
import com.example.notifications.application.service.NotificationService;
import com.example.notifications.application.service.PushNotifier;
import com.example.notifications.application.service.SmsNotifier;
import com.example.notifications.domain.model.EmailNotification;
import com.example.notifications.domain.model.PushNotification;
import com.example.notifications.domain.model.SmsNotification;
import com.example.notifications.infraestructure.email.sendgrid.SendGridConfig;
import com.example.notifications.infraestructure.email.sendgrid.SendGridEmailProvider;
import com.example.notifications.infraestructure.push.firebase.FirebaseConfig;
import com.example.notifications.infraestructure.push.firebase.FirebasePushProvider;
import com.example.notifications.infraestructure.sms.twilio.TwilioConfig;
import com.example.notifications.infraestructure.sms.twilio.TwilioSmsProvider;

import java.util.List;
import java.util.Map;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // ===== Template Engine =====
        var templateEngine = new SimpleTemplateEngine();

        // ===== Providers =====
        var emailNotifier = getEmailNotifier(templateEngine);
        var smsNotifier = getSmsNotifier(templateEngine);
        var pushNotifier = getPushNotifier(templateEngine);

        // ===== Registry (Factory) =====
        var registry = new NotifierRegistry(
                Map.of(
                        EmailNotification.class, emailNotifier,
                        SmsNotification.class, smsNotifier,
                        PushNotification.class, pushNotifier
                )
        );

        // ===== Service =====
        var notificationService = new NotificationService(registry);

        // ==========================================================
        // =============== EJEMPLOS DE USO ==========================
        // ==========================================================

        // ===== 1. Email async con templates =====
        var email = new EmailNotification(
                "test@mail.com",
                "Hola {{name}}",
                "Bienvenido {{name}} a {{app}}",
                Map.of(
                        "name", "Javier",
                        "app", "Notifications Lib"
                )
        );

        notificationService
                .sendAsync(email)
                .join();

        // ===== 2. SMS sync con template =====
        var sms = new SmsNotification(
                "+50588887777",
                "Hola {{name}}, tu código es {{code}}",
                Map.of(
                        "name", "Javier",
                        "code", "123456"
                )
        );

        notificationService.send(sms);

        // ===== 3. Push batch async =====
        var push1 = new PushNotification(
                "user-123",
                "device-token-abc",
                "Hello {{name}}",
                "Your code is {{code}}",
                Map.of("name", "Javier", "code", "9999"),
                Map.of("screen", "home")
        );

        var push2 = new PushNotification(
                "user-123",
                "device-token-bcd",
                "Hello {{name}}",
                "Your code is {{code}}",
                Map.of("name", "Javier", "code", "9999"),
                Map.of("screen", "home")
        );

        notificationService
                .sendBatchAsync(List.of(push1, push2))
                .join();

        notificationService.shutdown();
    }

    private static PushNotifier getPushNotifier(SimpleTemplateEngine templateEngine) {
        PushGateway gateway = new FirebasePushProvider(new FirebaseConfig("projectId","serviceAccountKey"));
        return new PushNotifier(gateway, templateEngine);
    }

    private static SmsNotifier getSmsNotifier(SimpleTemplateEngine templateEngine) {
        var config = new TwilioConfig("account-sid", "auth-token", "+5052222222");
        var smsGateway = new TwilioSmsProvider(config);
        return new SmsNotifier(smsGateway, templateEngine);
    }

    private static EmailNotifier getEmailNotifier(SimpleTemplateEngine templateEngine) {
        // ===== SendGrid Email Gateway =====
        var emailGateway = new SendGridEmailProvider(
                new SendGridConfig("api-key",
                        "https://api.sendgrid.com/v3/mail/send")
        );

        return new EmailNotifier(emailGateway, templateEngine);
    }
}
# 📬 Lib-Notification

Librería Java para enviar notificaciones multi-canal (Email, SMS, Push) con arquitectura hexagonal, framework-agnostic y fácil de extender.

## 🎯 Características

- **Multi-canal**: Email, SMS y Push Notifications
- **Arquitectura Hexagonal**: Desacoplamiento total entre capas
- **Múltiples Proveedores**: SendGrid, Mailgun, Twilio, Firebase
- **Asincronía**: Soporte con `CompletableFuture`
- **Templating**: Variables dinámicas en mensajes
- **Type-Safe**: Sealed Interfaces y Records (Java 21)

## 📦 Instalación

```bash
cd lib-notification
mvn clean install
```

**Maven** (`pom.xml`):

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>lib-notification</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle**:

```gradle
implementation 'com.example:lib-notification:1.0.0'
```

## 🚀 Quickstart:

Ver ejemplo completo en [`notification-demo/src/main/java/org/challenge/demo/Main.java`](notification-demo/src/main/java/org/challenge/demo/Main.java)

### 1. Configurar Proveedores

```java
// Template Engine
var templateEngine = new SimpleTemplateEngine();

// Email (SendGrid)
var emailNotifier = new EmailNotifier(
    new SendGridEmailProvider(new SendGridConfig("api-key", "url")),
    templateEngine
);

// SMS (Twilio)
var smsNotifier = new SmsNotifier(
    new TwilioSmsProvider(new TwilioConfig("sid", "token", "+1234567890")),
    templateEngine
);

// Push (Firebase)
var pushNotifier = new PushNotifier(
    new FirebasePushProvider(new FirebaseConfig("project-id", "key")),
    templateEngine
);
```

### 2. Registrar y Crear Servicio

```java
var registry = new NotifierRegistry(Map.of(
    EmailNotification.class, emailNotifier,
    SmsNotification.class, smsNotifier,
    PushNotification.class, pushNotifier
));

var notificationService = new NotificationService(registry);
```

### 3. Enviar Notificaciones

**Email (Async)**:

```java
var email = new EmailNotification(
    "sender@mail.com", "recipient@mail.com",
    "Hola {{name}}",
    "Bienvenido {{name}} a {{app}}",
    Map.of("name", "Javier", "app", "Notifications Lib")
);

notificationService.sendAsync(email).join();
```

**SMS (Sync)**:

```java
var sms = new SmsNotification(
        "+50512345678",
    "+50588887777",
    "Hola {{name}}, tu código es {{code}}",
    Map.of("name", "Javier", "code", "123456")
);

notificationService.send(sms);
```

**Push (Batch Async)**:

```java
var push1 = new PushNotification(
    "user-123", "device-token-abc",
    "Hello {{name}}", "Your code is {{code}}",
    Map.of("name", "Javier", "code", "9999"),
    Map.of("screen", "home")
);

notificationService.sendBatchAsync(List.of(push1, push2)).join();
```

### 4. Cleanup

```java
notificationService.shutdown();
```

## 🔌 Proveedores Soportados


| Canal | Proveedor | Clase                   |
| ----- | --------- | ----------------------- |
| Email | SendGrid  | `SendGridEmailProvider` |
| Email | Mailgun   | `MailgunEmailProvider`  |
| SMS   | Twilio    | `TwilioSmsProvider`     |
| Push  | Firebase  | `FirebasePushProvider`  |

## 📚 API Reference

### NotificationService

Servicio principal para enviar notificaciones.

**Métodos**:


| Método                                        | Descripción              | Retorno                                       |
| ---------------------------------------------- | ------------------------- | --------------------------------------------- |
| `send(T notification)`                         | Envío sincrónico        | `NotificationResult`                          |
| `sendAsync(T notification)`                    | Envío asincrónico       | `CompletableFuture<NotificationResult>`       |
| `sendBatchAsync(List<? extends Notification>)` | Envío batch asincrónico | `CompletableFuture<List<NotificationResult>>` |
| `shutdown()`                                   | Cierra el ExecutorService | `void`                                        |

**Ejemplo**:

```java
// Sincrónico
var result = notificationService.send(email);

// Asincrónico
notificationService.sendAsync(sms)
    .thenAccept(r -> System.out.println("Enviado: " + r.messageId()));

// Batch
notificationService.sendBatchAsync(List.of(email1, sms1, push1))
    .thenAccept(results -> results.forEach(r -> System.out.println(r.messageId())));
```

### Domain Models

**EmailNotification**:

```java
record EmailNotification(
    String sender,            // Email del que envia
    String recipient,        // Email destinatario
    String subjectTemplate,  // Plantilla asunto: "Hola {{name}}"
    String bodyTemplate,     // Plantilla cuerpo: "Bienvenido {{name}}"
    Map<String, Object> variables  // Variables: Map.of("name", "Juan")
)
```

**SmsNotification**:

```java
record SmsNotification(
    String sender,            //Número del que envia el mensaje
    String recipient,        // Número: "+50512345678"
    String messageTemplate,  // Plantilla: "Tu código es {{code}}"
    Map<String, Object> variables  // Variables: Map.of("code", "123456")
)
```

**PushNotification**:

```java
record PushNotification(
    String recipient,        // User ID
    String deviceToken,      // Token del dispositivo
    String titleTemplate,    // Plantilla título
    String bodyTemplate,     // Plantilla cuerpo
    Map<String, Object> variables,  // Variables para templates
    Map<String, String> data        // Datos adicionales
)
```

**NotificationResult**:

```java
record NotificationResult(
    String messageId,  // ID del mensaje enviado
    String status,     // Estado: "sent", "failed", etc.
    String error       // Mensaje de error (null si exitoso)
)
```

## ➕ Agregar un Nuevo Proveedor

### Paso 1: Identificar el Canal

Determina si tu proveedor es para **Email**, **SMS** o **Push**.

### Paso 2: Crear el Package

Crea un package en `infrastructure/`:

```
infrastructure/
└── email/              (o sms/ o push/)
    └── tu-proveedor/
        ├── TuProveedorEmailProvider.java
        ├── TuProveedorConfig.java
        ├── TuProveedorRequest.java
        └── TuProveedorResponse.java
```

### Paso 3: Crear la Clase Config

```java
package com.example.notifications.infraestructure.email.tuproveedor;

public record TuProveedorConfig(
    String apiKey,
    String apiUrl
    // Agrega los parámetros que necesites
) {}
```

### Paso 4: Implementar el Gateway

**Para Email**, implementa `EmailGateway`:

```java
package com.example.notifications.infraestructure.email.tuproveedor;

import com.example.notifications.application.port.out.email.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TuProveedorEmailProvider implements EmailGateway {
  
    private final TuProveedorConfig config;
  
    public TuProveedorEmailProvider(TuProveedorConfig config) {
        this.config = config;
    }
  
    @Override
    public EmailGatewayResponse send(EmailMessage message) {
        // 1. Mapear EmailMessage a tu formato de request
        TuProveedorRequest request = new TuProveedorRequest(
            message.from(),
            message.to(),
            message.subject(),
            message.body()
        );
    
        // 2. Hacer la llamada al API (o simularla)
        TuProveedorResponse response = callApi(request);
    
        // 3. Traducir la respuesta a EmailGatewayResponse
        if (response.isSuccess()) {
            log.info("Email enviado a: {}, messageId: {}", 
                message.to(), response.messageId());
            return new EmailGatewayResponse(
                response.messageId(),
                response.status(),
                null
            );
        }
    
        return new EmailGatewayResponse(
            null,
            response.status(),
            response.errorMessage()
        );
    }
  
    private TuProveedorResponse callApi(TuProveedorRequest request) {
        // Aquí va tu lógica de integración
        // Por ahora, simula la respuesta:
        log.info("Enviando email via TuProveedor a: {}", request.to());
        return new TuProveedorResponse(
            "msg-" + System.currentTimeMillis(),
            "sent",
            true,
            null
        );
    }
}
```

**Para SMS**, implementa `SmsGateway`:

```java
public class TuProveedorSmsProvider implements SmsGateway {
    @Override
    public SmsGatewayResponse send(SmsMessage message) {
        // Similar al ejemplo de Email
    }
}
```

**Para Push**, implementa `PushGateway`:

```java
public class TuProveedorPushProvider implements PushGateway {
    @Override
    public PushGatewayResponse send(PushMessage message) {
        // Similar al ejemplo de Email
    }
}
```

### Paso 5: Crear Request/Response Models

```java
// Request
public record TuProveedorRequest(
    String from,
    String to,
    String subject,
    String body
) {}

// Response
public record TuProveedorResponse(
    String messageId,
    String status,
    boolean isSuccess,
    String errorMessage
) {}
```

### Paso 6: Usar el Nuevo Proveedor

```java
// Configurar
var config = new TuProveedorConfig("api-key", "https://api.tuproveedor.com");
var provider = new TuProveedorEmailProvider(config);
var notifier = new EmailNotifier(provider, templateEngine);

// Registrar
var registry = new NotifierRegistry(Map.of(
    EmailNotification.class, notifier
));

// Usar
var service = new NotificationService(registry);
```

### Paso 7: Testing

```java
@Test
void shouldSendEmailViaTuProveedor() {
    var mockGateway = mock(EmailGateway.class);
    when(mockGateway.send(any())).thenReturn(
        new EmailGatewayResponse("msg-123", "sent", null)
    );
  
    var notifier = new EmailNotifier(mockGateway, new SimpleTemplateEngine());
    var email = new EmailNotification(
        "test@example.com",
        "Subject",
        "Body",
        Map.of()
    );
  
    var result = notifier.send(email);
  
    assertEquals("msg-123", result.messageId());
    assertEquals("sent", result.status());
}
```

### Resumen de Interfaces


| Canal | Interfaz       | Método              | Input                                         | Output                                                  |
| ----- | -------------- | -------------------- | --------------------------------------------- | ------------------------------------------------------- |
| Email | `EmailGateway` | `send(EmailMessage)` | `EmailMessage(from, to, subject, body)`       | `EmailGatewayResponse(messageId, status, errorMessage)` |
| SMS   | `SmsGateway`   | `send(SmsMessage)`   | `SmsMessage(from, to, message)`               | `SmsGatewayResponse(messageId, status, errorMessage)`   |
| Push  | `PushGateway`  | `send(PushMessage)`  | `PushMessage(deviceToken, title, body, data)` | `PushGatewayResponse(messageId, status, errorMessage)`  |

## 🔒 Seguridad

### ❌ NO hardcodear credenciales

```java
var config = new SendGridConfig("SG.hardcoded-key", "url"); // ❌
```

### ✅ Usar variables de entorno

```java
var apiKey = System.getenv("SENDGRID_API_KEY");
var config = new SendGridConfig(apiKey, "url"); // ✅
```

### ✅ Archivo de propiedades (.gitignore)

```java
Properties props = new Properties();
props.load(new FileInputStream("config.properties"));
var apiKey = props.getProperty("sendgrid.api.key");
```

### ✅ Gestores de secretos

```java
var apiKey = secretsManager.getSecret("sendgrid-api-key");
```

## 🏗️ Arquitectura

```
domain/          → Modelos de negocio (Notification, EmailNotification, etc.)
application/     → Lógica de aplicación (NotificationService, Notifiers)
  ├── port/in/   → Interfaces de entrada (Notifier)
  └── port/out/  → Interfaces de salida (EmailGateway, SmsGateway, PushGateway)
infrastructure/  → Implementaciones concretas (SendGrid, Twilio, Firebase)
```

**Principio**: La infraestructura depende de la aplicación, no al revés.

## 🐳 Docker

```bash
docker build -t notification-challenge .
docker run notification-challenge
```

## 🧪 Testing

```bash
cd lib-notification
mvn test
```

---

**Versión**: 1.0.0 | **Java**: 21+ | **Build**: Maven 3.9+

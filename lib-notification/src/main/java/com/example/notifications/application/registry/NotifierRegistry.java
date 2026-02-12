package com.example.notifications.application.registry;

import com.example.notifications.domain.model.Notification;
import com.example.notifications.application.port.in.Notifier;

import java.util.Map;

/**
 * Registro (registry) de {@link Notifier} asociados a cada tipo concreto de {@link Notification}.
 * <p>
 * Su responsabilidad es actuar como un mapa de resolución: dada una notificación ya instanciada,
 * permite obtener el {@code Notifier} que sabe enviarla.
 * </p>
 *
 * <h2>Cómo funciona</h2>
 * <ul>
 *   <li>Internamente mantiene un {@link Map} cuya clave es la clase concreta de la notificación
 *       (por ejemplo {@code PushNotification.class})</li>
 *   <li>El valor es el {@code Notifier} correspondiente a esa clase</li>
 *   <li>Al llamar a {@link #get(Notification)}, se busca el notifier por {@code notification.getClass()}</li>
 * </ul>
 *
 * <h2>Consideraciones</h2>
 * <ul>
 *   <li><b>Coincidencia exacta por clase</b>: la búsqueda se hace con {@code notification.getClass()},
 *       por lo que requiere que exista una entrada para la clase exacta (no resuelve por interfaz,
 *       superclase o jerarquía).</li>
 *   <li><b>Posible {@code null}</b>: si la clase de la notificación no está registrada,
 *       {@link #get(Notification)} devolverá {@code null}. Se recomienda validar el resultado
 *       (o cambiar la implementación para lanzar una excepción si no existe).</li>
 *   <li><b>Genéricos y cast</b>: el mapa almacena {@code Notifier<?>}. Para devolver un
 *       {@code Notifier<T>} tipado, se realiza un cast no verificable en tiempo de compilación,
 *       por eso existe {@code @SuppressWarnings("unchecked")}.</li>
 * </ul>
 *
 * <h2>Uso típico</h2>
 * <pre>{@code
 * Map<Class<? extends Notification>, Notifier<?>> map = Map.of(
 *     PushNotification.class, new PushNotifier(...),
 *     EmailNotification.class, new EmailNotifier(...)
 * );
 *
 * NotifierRegistry registry = new NotifierRegistry(map);
 *
 * Notification n = new PushNotification(...);
 * Notifier<Notification> notifier = registry.get(n); // devuelve el notifier adecuado
 * }</pre>
 */
public class NotifierRegistry {

    private final Map<Class<? extends Notification>, Notifier<?>> registry;

    /**
     * Crea un registro a partir de un mapa de notifiers por tipo de notificación.
     *
     * @param registry mapa donde la clave es la clase concreta de la notificación y el valor
     *                 es el {@link Notifier} capaz de enviarla
     */
    public NotifierRegistry(Map<Class<? extends Notification>, Notifier<?>> registry) {
        this.registry = registry;
    }

    /**
     * Obtiene el {@link Notifier} asociado a la clase concreta de la notificación proporcionada.
     *
     * @param notification instancia de notificación a resolver
     * @param <T>          tipo concreto de notificación
     * @return el notifier registrado para {@code notification.getClass()}, o {@code null} si no existe
     */
    @SuppressWarnings("unchecked")
    public <T extends Notification> Notifier<T> get(T notification) {
        return (Notifier<T>) registry.get(notification.getClass());
    }
}

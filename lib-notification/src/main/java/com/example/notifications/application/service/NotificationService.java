package com.example.notifications.application.service;

import com.example.notifications.domain.model.Notification;
import com.example.notifications.domain.result.NotificationResult;
import com.example.notifications.application.registry.NotifierRegistry;
import com.example.notifications.application.port.in.Notifier;

import java.util.List;
import java.util.concurrent.*;

public class NotificationService {

    private final NotifierRegistry registry;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public NotificationService(NotifierRegistry registry) {
        this.registry = registry;
    }

    /**
     * Envía una notificación de forma sincrónica usando el {@link Notifier} correspondiente.
     * <p>
     * El {@link Notifier} se resuelve a partir del tipo concreto de la notificación mediante
     * el {@link NotifierRegistry}. Una vez obtenido, se delega el envío llamando a
     * {@link Notifier#send(Notification)}.
     * </p>
     * @param notification notificación a enviar
     * @param <T>          tipo concreto de notificación
     * @return resultado del envío
     */
    public <T extends Notification> NotificationResult send(T notification) {
        Notifier<T> notifier = registry.get(notification);
        return notifier.send(notification);
    }

    /**
     * Envía una notificación de forma asíncrona.
     * <p>
     * Este método ejecuta {@link #send(Notification)} en un hilo del {@link #executor} y devuelve
     * un {@link CompletableFuture} que se completará cuando finalice el envío.
     * </p>
     *
     * <h2>Comportamiento</h2>
     * <ul>
     *   <li>Si {@link #send(Notification)} finaliza correctamente, el {@code CompletableFuture}
     *       se completa con el {@link NotificationResult} resultante.</li>
     *   <li>Si ocurre una excepción durante el envío (por ejemplo, porque no exista un notifier
     *       registrado o por un fallo del proveedor), el {@code CompletableFuture} se completa
     *       excepcionalmente con dicha excepción.</li>
     * </ul>
     *
     * <h2>Concurrencia</h2>
     * <p>
     * El trabajo se delega al {@link #executor}. El grado de paralelismo y el comportamiento bajo carga
     * dependen de la configuración de dicho {@code Executor} (en este caso, un pool fijo de 4 hilos).
     * </p>
     *
     * @param notification notificación a enviar
     * @param <T>          tipo concreto de notificación
     * @return futuro que se completará con el resultado del envío
     */
    public <T extends Notification> CompletableFuture<NotificationResult> sendAsync(T notification) {
        return CompletableFuture.supplyAsync(() -> send(notification), executor);
    }

    /**
     * Envía un lote de notificaciones de forma asíncrona y devuelve un futuro con la lista de resultados.
     * <p>
     * Para cada notificación se crea un {@link CompletableFuture} mediante {@link #sendAsync(Notification)}.
     * Después, se espera a que todas las operaciones finalicen usando {@link CompletableFuture#allOf(CompletableFuture[])}
     * y se recopilan los resultados en el mismo orden en que fueron generados los futuros.
     * </p>
     *
     * <h2>Comportamiento y manejo de errores</h2>
     * <ul>
     *   <li>Si <b>todas</b> las notificaciones se envían correctamente, el futuro resultante se completa
     *       con una {@link List} de {@link NotificationResult}.</li>
     *   <li>Si <b>alguna</b> operación falla, {@code allOf(...)} se completa excepcionalmente.
     *       En ese caso, la etapa {@code thenApply(...)} no se ejecuta y el {@code CompletableFuture}
     *       devuelto por este método también quedará completado excepcionalmente.</li>
     *   <li>Al usar {@link CompletableFuture#join()}, si existiera una excepción al recopilar resultados,
     *       se propagaría como {@link java.util.concurrent.CompletionException}.</li>
     * </ul>
     *
     * <h2>Concurrencia</h2>
     * <p>
     * El grado de paralelismo depende del {@link #executor}. Con un pool fijo, si el lote es mayor que el
     * número de hilos, algunas tareas esperarán en cola.
     * </p>
     *
     * @param notifications lista de notificaciones a enviar
     * @return futuro que se completará con la lista de resultados (o excepcionalmente si falla alguna)
     */
    public CompletableFuture<List<NotificationResult>> sendBatchAsync(List<? extends Notification> notifications) {
        List<CompletableFuture<NotificationResult>> futures =
                notifications.stream().map(this::sendAsync).toList();

        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).toList());
    }

    public void shutdown() {
        executor.shutdown();
    }
}

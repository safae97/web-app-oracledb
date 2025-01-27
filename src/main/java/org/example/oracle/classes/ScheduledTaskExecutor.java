package org.example.oracle.classes;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskExecutor {
    private static final ScheduledTaskExecutor INSTANCE = new ScheduledTaskExecutor();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ScheduledTaskExecutor() {
    }

    public static ScheduledTaskExecutor getInstance() {
        return INSTANCE;
    }

    /**
     * Programme une tâche à exécuter à une date et heure spécifique.
     * @param task La tâche à exécuter.
     * @param dateTime La date et l'heure pour exécuter la tâche.
     */
    public void schedule(Runnable task, LocalDateTime dateTime) {
        long delay = java.time.Duration.between(LocalDateTime.now(), dateTime).toMillis();
        if (delay > 0) {
            scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
        } else {
            throw new IllegalArgumentException("Scheduled date must be in the future.");
        }
    }
}

package com.ib.poc.whatsapp.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class FileCollectionDebouncer {

    private static final Logger log = LoggerFactory.getLogger(FileCollectionDebouncer.class);

    private final ConcurrentHashMap<String, ScheduledFuture<?>> pending = new ConcurrentHashMap<>();
    private final TaskScheduler scheduler;

    public FileCollectionDebouncer(TaskScheduler taskScheduler) {
        this.scheduler = taskScheduler;
    }

    public void schedule(String phone, Runnable task, long delayMs) {
        ScheduledFuture<?> existing = pending.remove(phone);
        if (existing != null) {
            existing.cancel(false);
            log.debug("Debounce reset. phone={} delayMs={}", phone, delayMs);
        }
        ScheduledFuture<?> future = scheduler.schedule(task, Instant.now().plusMillis(delayMs));
        pending.put(phone, future);
        log.debug("Debounce scheduled. phone={} delayMs={}", phone, delayMs);
    }

    public void cancel(String phone) {
        ScheduledFuture<?> f = pending.remove(phone);
        if (f != null) {
            f.cancel(false);
            log.debug("Debounce cancelled. phone={}", phone);
        }
    }
}

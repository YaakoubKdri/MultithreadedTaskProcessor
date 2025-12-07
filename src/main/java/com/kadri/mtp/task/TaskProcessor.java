package com.kadri.mtp.task;

import com.kadri.mtp.model.TaskMeta;
import com.kadri.mtp.model.TaskResult;
import com.kadri.mtp.model.TaskStatus;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class TaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);
    private final ExecutorService executor;
    private final ConcurrentMap<String, TaskMeta> tasks = new ConcurrentHashMap<>();
    private final ReentrantLock statsLock = new ReentrantLock();
    private int totalSubmitted = 0;

    public TaskProcessor() {
        int cores = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(Math.max(2, 2 * cores));
    }

    public String submitTask(String type){
        String id = UUID.randomUUID().toString();
        TaskCallable callable = new TaskCallable(id, type);
        CompletableFuture<TaskResult> promise = new CompletableFuture<>();
        TaskMeta meta = new TaskMeta(id, promise, type);
        tasks.put(id, meta);

        statsLock.lock();
        try {
            totalSubmitted++;
        }finally {
            statsLock.unlock();
        }

        Future<TaskResult> future = executor.submit(() -> {
            meta.setStatus(TaskStatus.RUNNING);
            meta.setStartedAt(System.currentTimeMillis());
            logger.info("Task {} started (type = {})", id, type);
            try {
                TaskResult r = callable.call();
                meta.setFinishedAt(System.currentTimeMillis());
                meta.setStatus(r.success()? TaskStatus.COMPLETED : TaskStatus.FAILED);
                ((CompletableFuture<TaskResult>) promise).complete(r);
                logger.info("Task {} finished status={} duration={}ms", id, meta.getStatus(), r.durationMillis());
                return r;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        meta.setFuture(future);
        return id;
    }

    public Optional<TaskMeta> getMeta(String id){
        return Optional.ofNullable(tasks.get(id));
    }

    public List<TaskMeta> listAll(){
        return new ArrayList<>(tasks.values());
    }

    public boolean cancel(String id){
        TaskMeta taskMeta = tasks.get(id);
        if (taskMeta == null) {
            return false;
        }
        Future<TaskResult> f = taskMeta.getFuture();
        boolean cancelled = f.cancel(true);
        if (cancelled ) {
            taskMeta.setStatus(TaskStatus.CANCELED);
            taskMeta.setFinishedAt(System.currentTimeMillis());
        }
        return cancelled;
    }

    public int getTotalSubmitted(){
        statsLock.lock();
        try {
            return totalSubmitted;
        }finally {
            statsLock.unlock();
        }
    }

    @PreDestroy
    public void shutdown(){
        logger.info("Shutting down executor");
        executor.shutdown();
        try {
            if(!executor.awaitTermination(5, TimeUnit.SECONDS)){
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

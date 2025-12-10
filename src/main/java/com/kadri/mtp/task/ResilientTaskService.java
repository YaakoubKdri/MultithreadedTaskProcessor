package com.kadri.mtp.task;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ResilientTaskService {
    private static final Logger log = LoggerFactory.getLogger(ResilientTaskService.class);
    private final TaskProcessor taskProcessor;

    public ResilientTaskService(TaskProcessor taskProcessor) {
        this.taskProcessor = taskProcessor;
    }

    @CircuitBreaker(name = "resilientTask", fallbackMethod = "submitFallback")
    @Retry(name = "resilientTask")
    public String submitResilientTask(String type){
        log.info("submitResilientTask called for type={}", type);

        maybeSimulateTransientFailure();
        String taskId = taskProcessor.submitTask(type);
        log.info("Task submitted with id={}", taskId);
        return taskId;
    }

    private String submitFallback(String type, Throwable exception){
        log.warn("submitFallback invoked for type={} due to: {}. Returning fallback id.", type, exception.toString());
        return "fallback-" + UUID.randomUUID();
    }

    private void maybeSimulateTransientFailure() {
        int r = ThreadLocalRandom.current().nextInt(0, 10);
        if(r < 3){
            log.debug("Simulating transient failure.");
            throw new RuntimeException("Simulating transient failure.");
        }
    }
}

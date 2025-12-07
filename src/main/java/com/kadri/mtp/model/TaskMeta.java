package com.kadri.mtp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.concurrent.Future;

@Getter
@Setter
public class TaskMeta {
    private final String id;
    private volatile TaskStatus status;
    private volatile long submittedAt;
    private volatile long startedAt;
    private volatile long finishedAt;
    private volatile Future<TaskResult> future;
    private volatile String type;

    public TaskMeta(String id, Future<TaskResult> future, String type) {
        this.id = id;
        this.future = future;
        this.type = type;
        this.status = TaskStatus.PENDING;
        this.submittedAt = Instant.now().toEpochMilli();
    }
}

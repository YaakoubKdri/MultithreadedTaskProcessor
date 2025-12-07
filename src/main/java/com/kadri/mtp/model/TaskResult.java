package com.kadri.mtp.model;

public record TaskResult(
        String taskId,
        boolean success,
        String message,
        long durationMillis
) {}

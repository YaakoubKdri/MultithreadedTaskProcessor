package com.kadri.mtp.task;

import com.kadri.mtp.model.TaskResult;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class TaskCallable implements Callable<TaskResult> {
    private final String taskId;
    private final String type;

    public TaskCallable(String taskId, String type) {
        this.taskId = taskId;
        this.type = type;
    }

    @Override
    public TaskResult call() throws Exception {
        long start = System.nanoTime();
        try{
            if(type.equals("PROCESS_ORDER")){
                Thread.sleep(ThreadLocalRandom.current().nextInt(300, 1200));
            } else if (type.equals("SEND_EMAIL")) {
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 600));
            }else {
                Thread.sleep(200);
            }
            long duration = (System.nanoTime() - start)/1_000_000;
            return new TaskResult(taskId, true, "OK: ", duration);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            long duration = (System.nanoTime() - start)/1_000_000;
            return new TaskResult(taskId, false, "Interrupted", duration);
        } catch (Exception exception){
            long duration = (System.nanoTime() - start)/1_000_000;
            return new TaskResult(taskId, false, "Failed: ", duration);
        }
    }
}

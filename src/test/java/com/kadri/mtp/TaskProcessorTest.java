package com.kadri.mtp;

import com.kadri.mtp.model.TaskMeta;
import com.kadri.mtp.model.TaskStatus;
import com.kadri.mtp.task.TaskProcessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskProcessorTest {

    @Test
    public void submitAndComplete() throws InterruptedException {
        TaskProcessor taskProcessor = new TaskProcessor();
        String id = taskProcessor.submitTask("SEND_EMAIL");

        TaskMeta meta;
        int tries = 0;

        do{
            Thread.sleep(1000);
            meta = taskProcessor.getMeta(id).orElseThrow();
            tries++;
        }while (meta.getStatus() == TaskStatus.PENDING || meta.getStatus() == TaskStatus.RUNNING && tries < 50);

        assertTrue(
                meta.getStatus() == TaskStatus.COMPLETED ||
                        meta.getStatus() == TaskStatus.FAILED
        );

        taskProcessor.shutdown();
    }
}

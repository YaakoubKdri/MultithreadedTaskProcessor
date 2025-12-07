package com.kadri.mtp.controller;

import com.kadri.mtp.task.TaskProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskProcessor processor;

    public TaskController(TaskProcessor processor) {
        this.processor = processor;
    }

    @PostMapping
    public ResponseEntity<?> submit(@RequestParam(defaultValue = "PROCESS_ORDER") String type){
        String id = processor.submitTask(type);
        return ResponseEntity.accepted().body(Map.of("taskId", id, "status", "SUBMITTED"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStatus(@PathVariable String id){
        return processor.getMeta(id)
                .map(meta -> ResponseEntity.ok(Map.of(
                        "id", meta.getId(),
                        "type", meta.getType(),
                        "status", meta.getStatus(),
                        "submittedAt", meta.getSubmittedAt(),
                        "startedAt", meta.getStartedAt(),
                        "finishedAt", meta.getFinishedAt()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> dashboard(){
        List<?> list = processor.listAll().stream()
                .map(meta -> Map.of(
                        "id", meta.getId(),
                        "type", meta.getType(),
                        "status", meta.getStatus(),
                        "submittedAt", meta.getSubmittedAt(),
                        "startedAt", meta.getStartedAt(),
                        "finishedAt", meta.getFinishedAt()
                )).toList();
        return ResponseEntity.ok(Map.of(
                "totalSubmitted", processor.getTotalSubmitted(),
                "tasks", list
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable String id){
        boolean ok = processor.cancel(id);
        if(ok) return ResponseEntity.ok(Map.of("id", id, "cancelled", true));
        return ResponseEntity.status(410).body(Map.of("id", id, "cancelled", false));
    }
}

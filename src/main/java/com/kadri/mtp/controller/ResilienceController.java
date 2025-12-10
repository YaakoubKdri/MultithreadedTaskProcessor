package com.kadri.mtp.controller;

import com.kadri.mtp.task.ResilientTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/resilience")
public class ResilienceController {

    private final ResilientTaskService resilientTaskService;

    public ResilienceController(ResilientTaskService resilientTaskService) {
        this.resilientTaskService = resilientTaskService;
    }

    @GetMapping
    public ResponseEntity<?> resilience(@RequestParam(defaultValue = "PROCESS_ORDER") String type){
        String result = resilientTaskService.submitResilientTask(type);
        return ResponseEntity.ok().body(
                Map.of(
                        "status", "ok",
                        "result", result
                        ));
    }
}

package com.example.activiti.controller;

import com.example.activiti.service.ActivitiService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/activiti")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ActivitiController {
    
    @Autowired
    private ActivitiService activitiService;
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startProcess(
            @RequestParam String processKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        log.info("Received request to start process: {}", processKey);
        
        try {
            ProcessInstance processInstance;
            if (variables == null) {
                processInstance = activitiService.startProcess(processKey);
            } else {
                processInstance = activitiService.startProcess(processKey, variables);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("processInstanceId", processInstance.getId());
            response.put("processDefinitionId", processInstance.getProcessDefinitionId());
            response.put("businessKey", processInstance.getBusinessKey());
            response.put("message", "Process started successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error starting process", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to start process: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/tasks")
    public ResponseEntity<Map<String, Object>> getTasks(@RequestParam(required = false) String assignee) {
        log.info("Received request to get tasks for assignee: {}", assignee);
        
        try {
            List<Task> tasks;
            if (assignee != null && !assignee.isEmpty()) {
                tasks = activitiService.getTasks(assignee);
            } else {
                tasks = activitiService.getAllTasks();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tasks", tasks);
            response.put("total", tasks.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting tasks", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get tasks: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completeTask(
            @RequestParam String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        log.info("Received request to complete task: {}", taskId);
        
        try {
            if (variables == null) {
                activitiService.completeTask(taskId);
            } else {
                activitiService.completeTask(taskId, variables);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task completed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error completing task", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to complete task: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/process-instances")
    public ResponseEntity<Map<String, Object>> getProcessInstances() {
        log.info("Received request to get running process instances");
        
        try {
            List<ProcessInstance> processInstances = activitiService.getRunningProcessInstances();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("processInstances", processInstances);
            response.put("total", processInstances.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting process instances", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get process instances: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Activiti Demo Application is running");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
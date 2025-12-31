package com.example.activiti.service;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ActivitiService {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    public ProcessInstance startProcess(String processKey, Map<String, Object> variables) {
        log.info("Starting process with key: {}", processKey);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, variables);
        log.info("Process started with ID: {}", processInstance.getId());
        return processInstance;
    }
    
    public ProcessInstance startProcess(String processKey) {
        return startProcess(processKey, new HashMap<>());
    }
    
    public List<Task> getTasks(String assignee) {
        log.info("Getting tasks for assignee: {}", assignee);
        return taskService.createTaskQuery()
                .taskAssignee(assignee)
                .orderByTaskCreateTime()
                .desc()
                .list();
    }
    
    public List<Task> getAllTasks() {
        log.info("Getting all tasks");
        return taskService.createTaskQuery()
                .orderByTaskCreateTime()
                .desc()
                .list();
    }
    
    public void completeTask(String taskId, Map<String, Object> variables) {
        log.info("Completing task: {} with variables: {}", taskId, variables);
        taskService.complete(taskId, variables);
        log.info("Task completed successfully");
    }
    
    public void completeTask(String taskId) {
        completeTask(taskId, new HashMap<>());
    }
    
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
    }
    
    public List<ProcessInstance> getRunningProcessInstances() {
        return runtimeService.createProcessInstanceQuery()
                .orderByProcessInstanceId()
                .desc()
                .list();
    }
    
    public void suspendProcessInstance(String processInstanceId) {
        log.info("Suspending process instance: {}", processInstanceId);
        runtimeService.suspendProcessInstanceById(processInstanceId);
        log.info("Process instance suspended successfully");
    }
    
    public void activateProcessInstance(String processInstanceId) {
        log.info("Activating process instance: {}", processInstanceId);
        runtimeService.activateProcessInstanceById(processInstanceId);
        log.info("Process instance activated successfully");
    }
}
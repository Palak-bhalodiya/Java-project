package com.promanage.controller;

import com.promanage.dto.ProjectDto;
import com.promanage.dto.TaskDto;
import com.promanage.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getAssignedProjects(Authentication authentication) {
        return ResponseEntity.ok(managerService.getAssignedProjects(authentication.getName()));
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<String> assignTask(@PathVariable Long projectId, @RequestBody TaskDto taskDto) {
        TaskDto result = managerService.assignTask(projectId, taskDto);
        return ResponseEntity.ok("Task successfully assigned to developer: " + result.developerName());    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDto>> getProjectTasks(@PathVariable Long projectId) {
        return ResponseEntity.ok(managerService.getProjectTasks(projectId));
    }

    @PutMapping("/tasks/{taskId}/review")
    public ResponseEntity<String> reviewTask(@PathVariable Long taskId, @RequestParam boolean approve, @RequestParam(required = false) String feedback) {
        return ResponseEntity.ok(managerService.reviewTask(taskId, approve, feedback));
    }

    @PutMapping("/projects/{projectId}/send-to-company")
    public ResponseEntity<String> sendProjectToCompany(
            @PathVariable Long projectId,
            @RequestParam String githubLink,
            Authentication authentication) {
        return ResponseEntity.ok(managerService.sendProjectToCompany(projectId, githubLink, authentication.getName()));
    }

    @PutMapping("/projects/{projectId}/complete")
    public ResponseEntity<String> completeProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(managerService.completeProject(projectId));
    }
}

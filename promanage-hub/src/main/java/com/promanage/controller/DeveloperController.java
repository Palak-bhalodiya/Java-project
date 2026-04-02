package com.promanage.controller;

import com.promanage.dto.TaskDto;
import com.promanage.entity.enums.TaskStatus;
import com.promanage.service.DeveloperService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/developer")
public class DeveloperController {

    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> getMyTasks(Authentication authentication) {
        return ResponseEntity.ok(developerService.getMyTasks(authentication.getName()));
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<String> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status,
            @RequestParam(required = false) String githubLink,
            Authentication authentication) {    // ← add this line
        return ResponseEntity.ok(developerService.updateTaskStatus(
                taskId, status, githubLink, authentication.getName()));
    }

//    @PutMapping("/tasks/{taskId}/feedback")  // ← new endpoint
//    public ResponseEntity<String> giveFeedback(
//            @PathVariable Long taskId,
//            @RequestParam String feedback) {
//        return ResponseEntity.ok(developerService.giveFeedback(taskId, feedback));
//    }
}
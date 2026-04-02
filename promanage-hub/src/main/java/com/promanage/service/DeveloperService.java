package com.promanage.service;

import com.promanage.dto.TaskDto;
import com.promanage.entity.Project;
import com.promanage.entity.Task;
import com.promanage.entity.User;
import com.promanage.entity.enums.ProjectStatus;
import com.promanage.entity.enums.TaskStatus;
import com.promanage.exception.ResourceNotFoundException;
import com.promanage.repository.ProjectRepository;
import com.promanage.repository.TaskRepository;
import com.promanage.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeveloperService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public DeveloperService(TaskRepository taskRepository, UserRepository userRepository,
                            ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public List<TaskDto> getMyTasks(String email) {
        User developer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return taskRepository.findByDeveloper(developer).stream()
                .map(this::mapToTaskDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public String updateTaskStatus(Long taskId, TaskStatus status, String githubLink, String email) {  // ← add email
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // ← check if task belongs to logged-in developer
        if (!task.getDeveloper().getEmail().equals(email)) {
            throw new RuntimeException("You are not authorized to update this task!");
        }

        task.setStatus(status);
        if (githubLink != null && !githubLink.isEmpty()) {
            task.setGithubLink(githubLink);
        }

        Project project = task.getProject();
        if (status == TaskStatus.IN_PROGRESS) {
            project.setStatus(ProjectStatus.IN_PROGRESS);
        } else if (status == TaskStatus.DONE) {
            project.setStatus(ProjectStatus.REVIEW);
        }
        projectRepository.save(project);
        taskRepository.save(task);

        return "Task '" + task.getTitle() + "' status updated to " + status;  // ← professional message
    }

//    public String giveFeedback(Long taskId, String feedback) {
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
//        String existing = task.getFeedback() != null ? task.getFeedback() : "";
//        String newFeedback = existing.isEmpty()
//                ? "Developer: " + feedback
//                : existing + "\n" + "Developer: " + feedback;
//        task.setFeedback(newFeedback);
//        taskRepository.save(task);
//        return "Feedback submitted successfully";
//    }

    private TaskDto mapToTaskDto(Task task) {
        String managerName = task.getProject().getManager() != null
                ? task.getProject().getManager().getName()
                : "Not Assigned";
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getGithubLink(),
                //task.getFeedback(),      // ← chat feedback
                task.getProject().getTitle(),
                task.getDeveloper().getName(),
                managerName
        );
    }
}
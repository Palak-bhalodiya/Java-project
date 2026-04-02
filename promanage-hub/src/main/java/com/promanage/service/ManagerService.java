package com.promanage.service;

import com.promanage.dto.ProjectDto;
import com.promanage.dto.TaskDto;
import com.promanage.entity.Bid;
import com.promanage.entity.Project;
import com.promanage.entity.Task;
import com.promanage.entity.User;
import com.promanage.entity.enums.BidStatus;
import com.promanage.entity.enums.ProjectStatus;
import com.promanage.entity.enums.Role;
import com.promanage.entity.enums.TaskStatus;
import com.promanage.exception.ResourceNotFoundException;
import com.promanage.repository.BidRepository;
import com.promanage.repository.ProjectRepository;
import com.promanage.repository.TaskRepository;
import com.promanage.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    public ManagerService(ProjectRepository projectRepository, TaskRepository taskRepository,
                          UserRepository userRepository, BidRepository bidRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    @Transactional
    public List<ProjectDto> getAssignedProjects(String email) {
        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return projectRepository.findByManager(manager).stream()
                .map(this::mapToProjectDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDto assignTask(Long projectId, TaskDto taskDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User developer = userRepository.findByName(taskDto.developerName())
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found"));

        if (developer.getRole() != Role.DEVELOPER) {
            throw new RuntimeException("Assigned user is not a developer");
        }

        Task task = new Task();
        task.setTitle(taskDto.title());
        task.setDescription(taskDto.description());
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setProject(project);
        task.setDeveloper(developer);

        project.setStatus(ProjectStatus.IN_PROGRESS);
        projectRepository.save(project);

        Task savedTask = taskRepository.save(task);
        return mapToTaskDto(savedTask);
    }

    @Transactional
    public List<TaskDto> getProjectTasks(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return taskRepository.findByProject(project).stream()
                .map(this::mapToTaskDto)
                .collect(Collectors.toList());
    }

//    public String reviewTask(Long taskId, boolean approve, String feedbackMessage) {
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
//        String existing = task.getFeedback() != null ? task.getFeedback() : "";
//
//        if (approve) {
//            task.setStatus(TaskStatus.DONE);
//            String newFeedback = existing.isEmpty()
//                    ? "Manager: Approved"
//                    : existing + "\n" + "Manager: Approved";
//            task.setFeedback(newFeedback);
//        } else {
//            task.setStatus(TaskStatus.REJECTED);
//            String newFeedback = existing.isEmpty()
//                    ? "Manager: " + feedbackMessage
//                    : existing + "\n" + "Manager: " + feedbackMessage;
//            task.setFeedback(newFeedback);
//        }
//        taskRepository.save(task);
//        return "Task reviewed successfully";
//    }

    public String reviewTask(Long taskId, boolean approve, String feedbackMessage) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (approve) {
            task.setStatus(TaskStatus.DONE);
        } else {
            task.setStatus(TaskStatus.REJECTED);
        }
        taskRepository.save(task);
        return approve
                ? "Task '" + task.getTitle() + "' approved successfully"
                : "Task '" + task.getTitle() + "' rejected successfully";
    }

    public String completeProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        project.setStatus(ProjectStatus.COMPLETED);
        projectRepository.save(project);
        return "Project marked as complete and ready for delivery";
    }

    private ProjectDto mapToProjectDto(Project project) {
        Double budget = bidRepository.findByProjectId(project.getId())
                .stream()
                .filter(b -> b.getStatus() == BidStatus.ACCEPTED_BY_USER)
                .map(Bid::getProposedCost)
                .findFirst()
                .orElse(project.getBudget());

        return new ProjectDto(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getRequiredLanguage(),
                project.getDeadline(),
                budget,
                project.getStatus(),
                project.getClient().getName(),
                project.getCompany() != null ? project.getCompany().getName() : null,
                project.getManager() != null ? project.getManager().getName() : null
        );
    }

    // ← only ONE mapToTaskDto method
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
                //task.getFeedback(),
                task.getProject().getTitle(),
                task.getDeveloper().getName(),
                managerName
        );
    }

    public String sendProjectToCompany(Long projectId, String githubLink, String managerEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (project.getStatus() != ProjectStatus.COMPLETED) {
            throw new RuntimeException("Project is not completed yet");
        }

        project.setGithubLink(githubLink);
        project.setStatus(ProjectStatus.DELIVERED_TO_COMPANY);
        projectRepository.save(project);

        return "Project '" + project.getTitle() + "' sent to '" + project.getCompany().getName() + "' with GitHub link: " + githubLink;
    }
}
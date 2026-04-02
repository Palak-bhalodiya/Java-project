package com.promanage.service;

import com.promanage.dto.ProjectDto;
import com.promanage.entity.Project;
import com.promanage.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    @Transactional
    public List<ProjectDto> getAvailableProjects() {
        return projectRepository.findAll()
                .stream()
                .filter(project -> project.getCompany() == null)
                .map(this::mapToProjectDto)
                .collect(Collectors.toList());
    }

    private ProjectDto mapToProjectDto(Project project) {
        return new ProjectDto(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getRequiredLanguage(),
                project.getDeadline(),
                project.getBudget(),
                project.getStatus(),
                project.getClient().getName(),
                project.getCompany() != null ? project.getCompany().getName() : null,
                project.getManager() != null ? project.getManager().getName() : null
        );
    }
}
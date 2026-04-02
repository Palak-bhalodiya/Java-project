package com.promanage.dto;

import com.promanage.entity.enums.TaskStatus;

public record TaskDto(
        Long taskId,
        String title,
        String description,
        TaskStatus status,
        String githubLink,
        //String feedback,
        String projectName,
        String developerName,
        String managerName
) {}
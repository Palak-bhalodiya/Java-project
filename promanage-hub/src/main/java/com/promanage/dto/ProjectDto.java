package com.promanage.dto;

import com.promanage.entity.enums.ProjectStatus;
import java.time.LocalDate;

public record ProjectDto(
    Long project_id,
    String title,
    String description,
    String requiredLanguage,
    LocalDate deadline,
    Double budget,
    ProjectStatus status,
    String clientName,
    String companyName,
    String managerName
) {}

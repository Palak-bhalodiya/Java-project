package com.promanage.dto;

import com.promanage.entity.enums.Role;

public record RegistrationDto(String name, String email, String password, Role role) {}

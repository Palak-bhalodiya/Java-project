package com.promanage.dto;

import com.promanage.entity.enums.Role;

public record UserDto(Long id, String name, String email, Role role) {}

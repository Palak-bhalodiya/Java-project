package com.promanage.service;

import com.promanage.dto.CompanyDto;
import com.promanage.dto.UserDto;
import com.promanage.repository.CompanyRepository;
import com.promanage.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public AdminService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .collect(Collectors.toList());
    }

    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(company -> new CompanyDto(
                        company.getId(),
                        company.getName(),
                        company.getDescription(),
                        company.getUser().getId()
                ))
                .collect(Collectors.toList());
    }
}

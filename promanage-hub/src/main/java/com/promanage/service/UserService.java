package com.promanage.service;

import com.promanage.dto.*;
import com.promanage.entity.*;
import com.promanage.entity.enums.BidStatus;
import com.promanage.entity.enums.ProjectStatus;
import com.promanage.exception.ResourceNotFoundException;
import com.promanage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//this is userservice class
@Service
public class UserService {

    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    public UserService(ProjectRepository projectRepository, CompanyRepository companyRepository,
            UserRepository userRepository, BidRepository bidRepository) {
        this.projectRepository = projectRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    public ProjectDto createProject(ProjectDto projectDto, String email) {
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = new Project();
        project.setTitle(projectDto.title());
        project.setDescription(projectDto.description());
        project.setRequiredLanguage(projectDto.requiredLanguage());
        project.setDeadline(projectDto.deadline());
        project.setBudget(projectDto.budget());
        project.setStatus(ProjectStatus.PENDING);
        project.setClient(client);

        Project saved = projectRepository.save(project);
        return mapToProjectDto(saved);
    }

    public List<CompanyDto> getRecommendedCompanies(String language) {
        return companyRepository.findAll().stream()
                .map(c -> new CompanyDto(c.getId(), c.getName(), c.getDescription(), c.getUser().getId()))
                .collect(Collectors.toList());
    }

    public String sendRequestToCompany(Long projectId, Long companyId, String email) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Bid bid = new Bid();
        bid.setProject(project);
        bid.setCompany(company);
        bid.setStatus(BidStatus.PENDING);
        bidRepository.save(bid);
        return "Request sent to company successfully";
    }

    @Transactional
    public List<BidDto> viewBids(Long projectId) {
        List<Bid> bids = bidRepository.findByProjectIdWithCompany(projectId);
        return bids.stream().map(bid -> new BidDto(
                bid.getId(),
                bid.getProject().getTitle(),
                bid.getProject().getClient().getName(), // ← String userName fix
                bid.getCompany().getName(),
                bid.getProposedCost(),
                bid.getStatus().name())).collect(Collectors.toList());
    }

    public String acceptBid(Long bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
        bid.setStatus(BidStatus.ACCEPTED_BY_USER);
        bidRepository.save(bid);

        Project project = bid.getProject();
        project.setCompany(bid.getCompany());
        project.setStatus(ProjectStatus.ASSIGNED);
        projectRepository.save(project);

        return "Bid accepted. Project assigned to " + bid.getCompany().getName();
    }

    public List<ProjectDto> getMyProjects(String email) {
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return projectRepository.findByClient(client).stream()
                .map(this::mapToProjectDto)
                .collect(Collectors.toList());
    }

    private ProjectDto mapToProjectDto(Project project) {
        return new ProjectDto(project.getId(), project.getTitle(), project.getDescription(),
                project.getRequiredLanguage(), project.getDeadline(), project.getBudget(), project.getStatus(),
                project.getClient().getName(),
                project.getCompany() != null ? project.getCompany().getName() : null,
                project.getManager() != null ? project.getManager().getName() : null);
    }

    private BidDto mapToBidDto(Bid bid) {
        return new BidDto(
                bid.getId(),
                bid.getProject().getTitle(),
                bid.getProject().getClient().getName(), // ← String userName fix
                bid.getCompany().getName(),
                bid.getProposedCost(),
                bid.getStatus().name());
    }

    @Transactional
    public String deleteProject(Long projectId, String email) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // ← only owner can delete
        if (!project.getClient().getEmail().equals(email)) {
            throw new RuntimeException("You are not authorized to delete this project!");
        }

        // ← can only delete if not sent to company yet
        if (project.getStatus() != ProjectStatus.PENDING) {
            throw new RuntimeException("Cannot delete project! It is already in progress.");
        }

        projectRepository.delete(project);
        return "Project '" + project.getTitle() + "' deleted successfully!";
    }

}
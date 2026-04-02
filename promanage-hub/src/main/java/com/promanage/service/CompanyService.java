package com.promanage.service;

import com.promanage.dto.BidDto;
import com.promanage.dto.CompanyDto;
import com.promanage.entity.Bid;
import com.promanage.entity.Company;
import com.promanage.entity.Project;
import com.promanage.entity.User;
import com.promanage.entity.enums.BidStatus;
import com.promanage.entity.enums.ProjectStatus;
import com.promanage.entity.enums.Role;
import com.promanage.exception.ResourceNotFoundException;
import com.promanage.repository.BidRepository;
import com.promanage.repository.CompanyRepository;
import com.promanage.repository.ProjectRepository;
import com.promanage.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository,
                          BidRepository bidRepository, ProjectRepository projectRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.projectRepository = projectRepository;
    }

    public String createProfile(CompanyDto companyDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Company company = new Company();
        company.setName(companyDto.name());
        company.setDescription(companyDto.description());
        company.setUser(user);
        companyRepository.save(company);
        return "Company profile created";
    }

    public String submitBid(Long projectId, Double cost, String username) {
        Company company = companyRepository.findByUserEmail(username)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Bid bid = new Bid();
        bid.setCompany(company);
        bid.setProject(project);
        bid.setProposedCost(cost);
        bid.setStatus(BidStatus.PENDING);
        bidRepository.save(bid);
        return "Bid submitted successfully!";
    }

    public String createBid(Long projectId, Double cost, String username) {
        Company company = companyRepository.findByUserEmail(username)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Bid bid = new Bid();
        bid.setCompany(company);
        bid.setProject(project);
        bid.setProposedCost(cost);
        bid.setStatus(BidStatus.PENDING);
        bidRepository.save(bid);
        return "Bid submitted successfully for project '" + project.getTitle() +
                "' by company '" + company.getName() +
                "' with proposed cost: ₹" + cost;
    }

    public String submitBidCost(Long bidId, Double cost, String email) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
        bid.setProposedCost(cost);
        bid.setStatus(BidStatus.ACCEPTED_BY_COMPANY);
        bidRepository.save(bid);
        return "Bid cost submitted successfully";
    }

    @Transactional
    public List<BidDto> viewRequests(String email) {
        Company company = companyRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        List<Bid> bids = bidRepository.findCompanyRequests(company.getId());
        return bids.stream()
                .map(this::mapToBidDto)
                .collect(Collectors.toList());
    }

    private BidDto mapToBidDto(Bid bid) {
        return new BidDto(
                bid.getId(),
                bid.getProject().getTitle(),
                bid.getProject().getClient().getName(),
                bid.getCompany().getName(),
                bid.getProposedCost(),
                bid.getStatus().name()
        );
    }

    public String rejectRequest(Long bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
        bid.setStatus(BidStatus.REJECTED);
        bidRepository.save(bid);
        return "Request rejected";
    }

    public String assignManager(Long projectId, Long managerId, String email) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        Company company = companyRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new RuntimeException("Selected user is not a manager");
        }

        project.setManager(manager);
        project.setCompany(company);
        project.setStatus(ProjectStatus.PENDING); // ← PENDING
        projectRepository.save(project);

        return "Manager '" + manager.getName() +
                "' assigned successfully to project '" + project.getTitle() +
                "' by company '" + company.getName() + "'";
    }

    public String deliverProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (project.getStatus() != ProjectStatus.DELIVERED_TO_COMPANY) {
            throw new RuntimeException("Project has not been sent to company by manager yet");
        }
        project.setStatus(ProjectStatus.DELIVERED_TO_CLIENT);
        projectRepository.save(project);
        return "Project '" + project.getTitle() + "' delivered to client '" + project.getClient().getName() + "' successfully";

    }

    @Transactional
    public List<BidDto> viewBids(Long projectId) {
        List<Bid> bids = bidRepository.findByProjectId(projectId);
        return bids.stream().map(bid -> new BidDto(
                bid.getId(),
                bid.getProject().getTitle(),
                bid.getProject().getClient().getName(),
                bid.getCompany().getName(),
                bid.getProposedCost(),
                bid.getStatus().name()
        )).collect(Collectors.toList());
    }
}
package com.promanage.controller;

import com.promanage.dto.BidDto;
import com.promanage.dto.CompanyDto;
import com.promanage.dto.ProjectDto;
import com.promanage.entity.Project;
import com.promanage.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.promanage.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyService companyService;

    private final ProjectService projectService;

    public CompanyController(CompanyService companyService, ProjectService projectService) {
        this.companyService = companyService;
        this.projectService = projectService;
    }

    @PostMapping("/profile")
    public ResponseEntity<String> createProfile(@RequestBody CompanyDto companyDto, Authentication authentication) {
        return ResponseEntity.ok(companyService.createProfile(companyDto, authentication.getName()));
    }



    @GetMapping("/projects")
    public List<ProjectDto> getProjects() {
        return projectService.getAvailableProjects();  // was: getAllProjects()
    }


    @PostMapping("/bids")
    public ResponseEntity<String> createBid(
            @RequestParam Long projectId,
            @RequestParam Double cost,
            Authentication authentication) {

        return ResponseEntity.ok(
                companyService.createBid(projectId, cost, authentication.getName())
        );
    }

    @PostMapping("/projects/{projectId}/bid")
    public ResponseEntity<String> submitBid(
            @PathVariable Long projectId,
            @RequestParam Double cost,
            Authentication authentication) {

        return ResponseEntity.ok(
                companyService.submitBid(projectId, cost, authentication.getName())
        );
    }

    @GetMapping("/requests")
    public ResponseEntity<List<BidDto>> viewRequests(Authentication authentication) {
        return ResponseEntity.ok(companyService.viewRequests(authentication.getName()));
    }

    @PutMapping("/bids/{bidId}/cost")
    public ResponseEntity<String> submitBidCost(@PathVariable Long bidId, @RequestParam Double cost, Authentication authentication) {
        return ResponseEntity.ok(companyService.submitBidCost(bidId, cost, authentication.getName()));
    }

    @PutMapping("/bids/{bidId}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable Long bidId) {
        return ResponseEntity.ok(companyService.rejectRequest(bidId));
    }

    @PutMapping("/projects/{projectId}/assign-manager/{managerId}")
    public ResponseEntity<String> assignManager(@PathVariable Long projectId, @PathVariable Long managerId, Authentication authentication) {
        return ResponseEntity.ok(companyService.assignManager(projectId, managerId, authentication.getName()));
    }

    @PutMapping("/projects/{projectId}/deliver")
    public ResponseEntity<String> deliverProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(companyService.deliverProject(projectId));
    }
}

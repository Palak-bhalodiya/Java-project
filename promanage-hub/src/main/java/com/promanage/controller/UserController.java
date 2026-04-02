package com.promanage.controller;

import com.promanage.dto.BidDto;
import com.promanage.dto.CompanyDto;
import com.promanage.dto.ProjectDto;
import com.promanage.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto, Authentication authentication) {
        return new ResponseEntity<>(userService.createProject(projectDto, authentication.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDto>> getCompanies(@RequestParam(required = false) String language) {
        return ResponseEntity.ok(userService.getRecommendedCompanies(language));
    }

    @PostMapping("/projects/{projectId}/request/{companyId}")
    public ResponseEntity<String> sendRequest(@PathVariable Long projectId, @PathVariable Long companyId, Authentication authentication) {
        return ResponseEntity.ok(userService.sendRequestToCompany(projectId, companyId, authentication.getName()));
    }

    @GetMapping("/projects/{projectId}/bids")
    public ResponseEntity<List<BidDto>> viewBids(@PathVariable Long projectId) {
        return ResponseEntity.ok(userService.viewBids(projectId));
    }

    @PutMapping("/bids/{bidId}/accept")
    public ResponseEntity<String> acceptBid(@PathVariable Long bidId) {
        return ResponseEntity.ok(userService.acceptBid(bidId));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getMyProjects(Authentication authentication) {
        return ResponseEntity.ok(userService.getMyProjects(authentication.getName()));
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<String> deleteProject(
            @PathVariable Long projectId,
            Authentication authentication) {
        return ResponseEntity.ok(userService.deleteProject(projectId, authentication.getName()));
    }
}

package com.promanage.dto;

public class BidDto {

    private Long bid;
    private String projectName;
    private String userName;
    private String companyName;
    private Double proposedCost;
    private String status;

    public BidDto(Long bid, String projectName, String userName, String companyName, Double proposedCost, String status) {
        this.bid = bid;
        this.projectName = projectName;
        this.userName = userName;
        this.companyName = companyName;
        this.proposedCost = proposedCost;
        this.status = status;
    }

    public Long getBid() { return bid; }
    public String getProjectName() { return projectName; }
    public String getUserName() { return userName; }
    public String getCompanyName() { return companyName; }
    public Double getProposedCost() { return proposedCost; }
    public String getStatus() { return status; }
}
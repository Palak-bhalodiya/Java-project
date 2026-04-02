package com.promanage.repository;

import com.promanage.entity.Bid;
import com.promanage.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByCompany(Company company);

    List<Bid> findByProjectId(Long projectId);  // ← Add this

    @Query("SELECT b FROM Bid b " +
            "JOIN FETCH b.project p " +
            "JOIN FETCH p.client " +
            "JOIN FETCH b.company " +
            "WHERE b.company.id = :companyId")
    List<Bid> findCompanyRequests(@Param("companyId") Long companyId);

    @Query("SELECT b FROM Bid b " +
            "JOIN FETCH b.project p " +
            "JOIN FETCH p.client " +
            "JOIN FETCH b.company " +
            "WHERE b.project.id = :projectId")
    List<Bid> findByProjectIdWithCompany(@Param("projectId") Long projectId);
}
package com.promanage.repository;

import com.promanage.entity.Company;
import com.promanage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUser(User user);

    Optional<Company> findByUserEmail(String email);
}

package com.promanage.repository;

import com.promanage.entity.Company;
import com.promanage.entity.Project;
import com.promanage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByClient(User client);
    List<Project> findByCompany(Company company);
    List<Project> findByManager(User manager);
}

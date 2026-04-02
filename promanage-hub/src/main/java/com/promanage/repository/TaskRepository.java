package com.promanage.repository;

import com.promanage.entity.Project;
import com.promanage.entity.Task;
import com.promanage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByDeveloper(User developer);
}

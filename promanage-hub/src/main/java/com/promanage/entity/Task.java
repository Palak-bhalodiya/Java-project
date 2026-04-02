package com.promanage.entity;

import com.promanage.entity.enums.TaskStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    private String githubLink;

    @Column(columnDefinition = "TEXT")
    private String feedback;  // ← single chat field

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id")
    private User developer;

    public Task() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }
//    public String getFeedback() { return feedback; }
//    public void setFeedback(String feedback) { this.feedback = feedback; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public User getDeveloper() { return developer; }
    public void setDeveloper(User developer) { this.developer = developer; }
}
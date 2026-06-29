package com.pxmart.permission.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permission_application")
public class PermissionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String applicantName;

    @Column
    private String applicantEmail;

    @Column(length = 500)
    private String requestItem;

    @Column(length = 2000)
    private String description;

    @Column(length = 2000)
    private String requestAccounts;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PermissionRow> rows = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(length = 500)
    private String reviewerComment;

    private LocalDateTime reviewedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public PermissionApplication() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getApplicantEmail() { return applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }

    public String getRequestItem() { return requestItem; }
    public void setRequestItem(String requestItem) { this.requestItem = requestItem; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequestAccounts() { return requestAccounts; }
    public void setRequestAccounts(String requestAccounts) { this.requestAccounts = requestAccounts; }

    public List<PermissionRow> getRows() { return rows; }
    public void setRows(List<PermissionRow> rows) { this.rows = rows; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public String getReviewerComment() { return reviewerComment; }
    public void setReviewerComment(String reviewerComment) { this.reviewerComment = reviewerComment; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

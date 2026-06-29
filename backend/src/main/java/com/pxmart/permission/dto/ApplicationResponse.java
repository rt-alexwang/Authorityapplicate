package com.pxmart.permission.dto;

import com.pxmart.permission.entity.PermissionApplication;
import com.pxmart.permission.entity.PermissionRow;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationResponse {

    private Long id;
    private String applicantName;
    private String applicantEmail;
    private String requestItem;
    private String description;
    private String requestAccounts;
    private List<PermissionRowDto> rows;
    private PermissionApplication.ApplicationStatus status;
    private String statusLabel;
    private String reviewerComment;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    public static ApplicationResponse from(PermissionApplication app) {
        ApplicationResponse r = new ApplicationResponse();
        r.setId(app.getId());
        r.setApplicantName(app.getApplicantName());
        r.setApplicantEmail(app.getApplicantEmail());
        r.setRequestItem(app.getRequestItem());
        r.setDescription(app.getDescription());
        r.setRequestAccounts(app.getRequestAccounts());
        r.setStatus(app.getStatus());
        r.setStatusLabel(switch (app.getStatus()) {
            case PENDING -> "待審核";
            case APPROVED -> "已核准";
            case REJECTED -> "已拒絕";
        });
        r.setReviewerComment(app.getReviewerComment());
        r.setReviewedAt(app.getReviewedAt());
        r.setCreatedAt(app.getCreatedAt());
        if (app.getRows() != null) {
            r.setRows(app.getRows().stream().map(ApplicationResponse::rowToDto).collect(Collectors.toList()));
        }
        return r;
    }

    private static PermissionRowDto rowToDto(PermissionRow row) {
        PermissionRowDto dto = new PermissionRowDto();
        dto.setLocalIp(row.getLocalIp());
        dto.setLocalDb(row.getLocalDb());
        dto.setRemoteIp(row.getRemoteIp());
        dto.setRemoteDb(row.getRemoteDb());
        dto.setObjectType(row.getObjectType());
        dto.setObjectName(row.getObjectName());
        dto.setCanSelect(row.isCanSelect());
        dto.setCanInsert(row.isCanInsert());
        dto.setCanUpdate(row.isCanUpdate());
        dto.setCanDelete(row.isCanDelete());
        dto.setCanExecute(row.isCanExecute());
        dto.setCanAlter(row.isCanAlter());
        return dto;
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

    public List<PermissionRowDto> getRows() { return rows; }
    public void setRows(List<PermissionRowDto> rows) { this.rows = rows; }

    public PermissionApplication.ApplicationStatus getStatus() { return status; }
    public void setStatus(PermissionApplication.ApplicationStatus status) { this.status = status; }

    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }

    public String getReviewerComment() { return reviewerComment; }
    public void setReviewerComment(String reviewerComment) { this.reviewerComment = reviewerComment; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

package com.pxmart.permission.dto;

import com.pxmart.permission.entity.PermissionApplication;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private String applicantName;
    private String applicantEmail;
    private String department;
    private String targetSystem;
    private String accessReason;
    private String accessScope;
    private LocalDate expectedStartDate;
    private LocalDate expectedEndDate;
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
        r.setDepartment(app.getDepartment());
        r.setTargetSystem(app.getTargetSystem());
        r.setAccessReason(app.getAccessReason());
        r.setAccessScope(app.getAccessScope());
        r.setExpectedStartDate(app.getExpectedStartDate());
        r.setExpectedEndDate(app.getExpectedEndDate());
        r.setStatus(app.getStatus());
        r.setStatusLabel(switch (app.getStatus()) {
            case PENDING -> "待審核";
            case APPROVED -> "已核准";
            case REJECTED -> "已拒絕";
        });
        r.setReviewerComment(app.getReviewerComment());
        r.setReviewedAt(app.getReviewedAt());
        r.setCreatedAt(app.getCreatedAt());
        return r;
    }
}

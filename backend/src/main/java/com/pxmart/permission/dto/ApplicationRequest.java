package com.pxmart.permission.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class ApplicationRequest {

    @NotBlank(message = "申請人姓名為必填")
    private String applicantName;

    private String applicantEmail;

    private String requestItem;

    private String description;

    private String requestAccounts;

    private List<PermissionRowDto> rows;

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
}

package com.pxmart.permission.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ApplicationRequest {
    @NotBlank(message = "申請人姓名為必填")
    private String applicantName;

    @NotBlank(message = "信箱為必填")
    @Email(message = "信箱格式不正確")
    private String applicantEmail;

    @NotBlank(message = "部門為必填")
    private String department;

    @NotBlank(message = "申請系統為必填")
    private String targetSystem;

    @NotBlank(message = "申請原因為必填")
    private String accessReason;

    @NotBlank(message = "存取範圍為必填")
    private String accessScope;

    @NotNull(message = "預計開始日期為必填")
    private LocalDate expectedStartDate;

    private LocalDate expectedEndDate;
}

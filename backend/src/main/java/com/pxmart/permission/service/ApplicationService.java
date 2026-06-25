package com.pxmart.permission.service;

import com.pxmart.permission.dto.ApplicationRequest;
import com.pxmart.permission.dto.ApplicationResponse;
import com.pxmart.permission.dto.ReviewRequest;
import com.pxmart.permission.entity.PermissionApplication;
import com.pxmart.permission.entity.PermissionApplication.ApplicationStatus;
import com.pxmart.permission.repository.PermissionApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final PermissionApplicationRepository repository;

    public ApplicationResponse create(ApplicationRequest req) {
        PermissionApplication app = new PermissionApplication();
        app.setApplicantName(req.getApplicantName());
        app.setApplicantEmail(req.getApplicantEmail());
        app.setDepartment(req.getDepartment());
        app.setTargetSystem(req.getTargetSystem());
        app.setAccessReason(req.getAccessReason());
        app.setAccessScope(req.getAccessScope());
        app.setExpectedStartDate(req.getExpectedStartDate());
        app.setExpectedEndDate(req.getExpectedEndDate());
        return ApplicationResponse.from(repository.save(app));
    }

    public List<ApplicationResponse> getAll() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream().map(ApplicationResponse::from).collect(Collectors.toList());
    }

    public ApplicationResponse getById(Long id) {
        return ApplicationResponse.from(findOrThrow(id));
    }

    public ApplicationResponse approve(Long id, ReviewRequest req) {
        PermissionApplication app = findOrThrow(id);
        app.setStatus(ApplicationStatus.APPROVED);
        app.setReviewerComment(req.getComment());
        app.setReviewedAt(LocalDateTime.now());
        return ApplicationResponse.from(repository.save(app));
    }

    public ApplicationResponse reject(Long id, ReviewRequest req) {
        PermissionApplication app = findOrThrow(id);
        app.setStatus(ApplicationStatus.REJECTED);
        app.setReviewerComment(req.getComment());
        app.setReviewedAt(LocalDateTime.now());
        return ApplicationResponse.from(repository.save(app));
    }

    public PermissionApplication getEntityById(Long id) {
        return findOrThrow(id);
    }

    private PermissionApplication findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("申請單不存在：" + id));
    }
}

package com.pxmart.permission.service;

import com.pxmart.permission.dto.ApplicationRequest;
import com.pxmart.permission.dto.ApplicationResponse;
import com.pxmart.permission.dto.PermissionRowDto;
import com.pxmart.permission.dto.ReviewRequest;
import com.pxmart.permission.entity.PermissionApplication;
import com.pxmart.permission.entity.PermissionApplication.ApplicationStatus;
import com.pxmart.permission.entity.PermissionRow;
import com.pxmart.permission.repository.PermissionApplicationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final PermissionApplicationRepository repository;
    private final MailService mailService;

    public ApplicationService(PermissionApplicationRepository repository, MailService mailService) {
        this.repository = repository;
        this.mailService = mailService;
    }

    public ApplicationResponse create(ApplicationRequest req) {
        PermissionApplication app = new PermissionApplication();
        app.setApplicantName(req.getApplicantName());
        app.setApplicantEmail(req.getApplicantEmail());
        app.setRequestItem(req.getRequestItem());
        app.setDescription(req.getDescription());
        app.setRequestAccounts(req.getRequestAccounts());
        if (req.getRows() != null) {
            for (PermissionRowDto dto : req.getRows()) {
                PermissionRow row = dtoToRow(dto, app);
                app.getRows().add(row);
            }
        }
        PermissionApplication saved = repository.save(app);
        mailService.sendApplicationReceived(saved);
        return ApplicationResponse.from(saved);
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

    public List<ApplicationResponse> search(String q) {
        String kw = q == null ? "" : q.trim();
        return repository
                .findByApplicantNameContainingIgnoreCaseOrRequestAccountsContainingIgnoreCaseOrderByCreatedAtDesc(kw, kw)
                .stream().map(ApplicationResponse::from).collect(Collectors.toList());
    }

    public PermissionApplication getEntityById(Long id) {
        return findOrThrow(id);
    }

    private PermissionApplication findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("申請單不存在：" + id));
    }

    private PermissionRow dtoToRow(PermissionRowDto dto, PermissionApplication app) {
        PermissionRow row = new PermissionRow();
        row.setApplication(app);
        row.setLocalIp(dto.getLocalIp());
        row.setLocalDb(dto.getLocalDb());
        row.setRemoteIp(dto.getRemoteIp());
        row.setRemoteDb(dto.getRemoteDb());
        row.setObjectType(dto.getObjectType());
        row.setObjectName(dto.getObjectName());
        row.setCanSelect(dto.isCanSelect());
        row.setCanInsert(dto.isCanInsert());
        row.setCanUpdate(dto.isCanUpdate());
        row.setCanDelete(dto.isCanDelete());
        row.setCanExecute(dto.isCanExecute());
        row.setCanAlter(dto.isCanAlter());
        return row;
    }
}

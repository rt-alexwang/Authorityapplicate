package com.pxmart.permission.controller;

import com.pxmart.permission.entity.PermissionApplication;
import com.pxmart.permission.service.ApplicationService;
import com.pxmart.permission.service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
public class PdfController {

    private final ApplicationService applicationService;
    private final PdfService pdfService;

    public PdfController(ApplicationService applicationService, PdfService pdfService) {
        this.applicationService = applicationService;
        this.pdfService = pdfService;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        PermissionApplication app = applicationService.getEntityById(id);
        byte[] pdf = pdfService.generatePdf(app);
        String filename = "permission-app-" + String.format("%06d", id) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

package com.pxmart.permission.controller;

import com.pxmart.permission.dto.ApplicationRequest;
import com.pxmart.permission.dto.ApplicationResponse;
import com.pxmart.permission.dto.ReviewRequest;
import com.pxmart.permission.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService service;

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody ApplicationRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApplicationResponse> approve(@PathVariable Long id, @RequestBody ReviewRequest req) {
        return ResponseEntity.ok(service.approve(id, req));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApplicationResponse> reject(@PathVariable Long id, @RequestBody ReviewRequest req) {
        return ResponseEntity.ok(service.reject(id, req));
    }
}

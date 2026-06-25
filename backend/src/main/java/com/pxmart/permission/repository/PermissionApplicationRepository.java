package com.pxmart.permission.repository;

import com.pxmart.permission.entity.PermissionApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PermissionApplicationRepository extends JpaRepository<PermissionApplication, Long> {
    List<PermissionApplication> findByStatusOrderByCreatedAtDesc(PermissionApplication.ApplicationStatus status);
    List<PermissionApplication> findAllByOrderByCreatedAtDesc();
}

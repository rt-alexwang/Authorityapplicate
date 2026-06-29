package com.pxmart.permission.repository;

import com.pxmart.permission.entity.OptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OptionItemRepository extends JpaRepository<OptionItem, Long> {
    List<OptionItem> findByTypeOrderBySortOrderAsc(String type);
    boolean existsByType(String type);
}

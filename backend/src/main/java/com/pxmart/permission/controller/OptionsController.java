package com.pxmart.permission.controller;

import com.pxmart.permission.entity.OptionItem;
import com.pxmart.permission.repository.OptionItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/options")
public class OptionsController {

    private final OptionItemRepository repo;

    public OptionsController(OptionItemRepository repo) {
        this.repo = repo;
    }

    /** 取得指定類型的選項清單 */
    @GetMapping("/{type}")
    public ResponseEntity<List<String>> getOptions(@PathVariable String type) {
        List<String> values = repo.findByTypeOrderBySortOrderAsc(type)
                .stream().map(OptionItem::getValue).collect(Collectors.toList());
        return ResponseEntity.ok(values);
    }

    /** 一次取得所有類型 */
    @GetMapping
    public ResponseEntity<Map<String, List<String>>> getAllOptions() {
        List<OptionItem> all = repo.findAll();
        Map<String, List<String>> result = all.stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .collect(Collectors.groupingBy(
                        OptionItem::getType,
                        Collectors.mapping(OptionItem::getValue, Collectors.toList())
                ));
        return ResponseEntity.ok(result);
    }

    /** 新增自訂選項（使用者在前端輸入新值時呼叫） */
    @PostMapping("/{type}")
    public ResponseEntity<Void> addOption(@PathVariable String type, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        if (value == null || value.isBlank()) return ResponseEntity.badRequest().build();
        boolean exists = repo.findByTypeOrderBySortOrderAsc(type)
                .stream().anyMatch(o -> o.getValue().equals(value));
        if (!exists) {
            int maxOrder = repo.findByTypeOrderBySortOrderAsc(type)
                    .stream().mapToInt(OptionItem::getSortOrder).max().orElse(0);
            repo.save(new OptionItem(type, value, maxOrder + 1));
        }
        return ResponseEntity.ok().build();
    }
}

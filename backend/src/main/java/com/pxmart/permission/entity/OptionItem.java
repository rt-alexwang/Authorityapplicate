package com.pxmart.permission.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "option_item")
public class OptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String value;

    private int sortOrder;

    public OptionItem() {}

    public OptionItem(String type, String value, int sortOrder) {
        this.type = type;
        this.value = value;
        this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}

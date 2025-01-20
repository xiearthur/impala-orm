package com.example.impala.example.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private String categoryId;
    private String parentId;
    private String categoryName;
    private Integer categoryLevel;
    private Integer sortOrder;
    private Boolean isLeaf;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
package com.example.impala.example.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private String productId;
    private String categoryId;
    private String brandId;
    private String productName;
    private String productDesc;
    private BigDecimal marketPrice;
    private BigDecimal salePrice;
    private Integer stockQuantity;
    private Integer saleStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String dt;  // 分区字段
}

// OrderDetail.java
package com.example.impala.example.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetail {
    private String detailId;
    private String orderId;
    private String productId;
    private String skuId;
    private String productName;
    private String skuSpec;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private LocalDateTime createTime;
    private String dt;  // 分区字段
}
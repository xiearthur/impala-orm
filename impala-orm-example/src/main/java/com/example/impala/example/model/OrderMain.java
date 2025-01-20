package com.example.impala.example.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderMain {
    private String orderId;
    private String userId;
    private Integer orderStatus;
    private BigDecimal orderAmount;
    private Integer paymentMethod;
    private String shippingAddress;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime completeTime;
    private String dt;  // 分区字段
}
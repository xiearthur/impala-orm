package com.example.impala.example.controller;

import com.example.impala.example.service.OrderAnalysisService;
import com.example.impala.example.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(
            @PathVariable String orderId,
            @RequestParam String dt) {
        try {
            return ResponseEntity.ok(orderService.getOrderDetail(orderId, dt));
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return ResponseEntity.status(500).body("获取订单详情失败");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserOrders(
            @PathVariable String userId,
            @RequestParam String dt) {
        try {
            return ResponseEntity.ok(orderService.getUserOrders(userId, dt));
        } catch (Exception e) {
            log.error("获取用户订单失败", e);
            return ResponseEntity.status(500).body("获取用户订单失败");
        }
    }

    @GetMapping("/stats/daily")
    public ResponseEntity<?> getDailyStats(@RequestParam String dt) {
        try {
            return ResponseEntity.ok(orderService.getDailySummary(dt));
        } catch (Exception e) {
            log.error("获取每日统计失败", e);
            return ResponseEntity.status(500).body("获取每日统计失败");
        }
    }
}
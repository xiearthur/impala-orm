package com.example.impala.example.service;

import com.example.impala.example.mapper.OrderDetailMapper;
import com.example.impala.example.mapper.OrderMainMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderMainMapper orderMainMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    public Map<String, Object> getOrderDetail(String orderId, String dt) {
        Map<String, Object> result = new HashMap<>();
        result.put("order", orderMainMapper.findById(orderId, dt));
        result.put("items", orderDetailMapper.findByOrderId(orderId, dt));
        return result;
    }

    public List<Map<String, Object>> getUserOrders(String userId, String dt) {
        return orderMainMapper.findByUserId(userId, dt);
    }

    public Map<String, Object> getDailySummary(String dt) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("daily_stats", orderMainMapper.getDailyStats(dt));
        summary.put("order_stats", orderDetailMapper.getStatistics(dt));
        return summary;
    }
}
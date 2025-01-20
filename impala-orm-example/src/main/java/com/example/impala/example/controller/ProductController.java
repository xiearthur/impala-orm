package com.example.impala.example.controller;

import com.example.impala.example.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/analysis")
    public ResponseEntity<?> getProductAnalysis(@RequestParam String dt) {
        try {
            return ResponseEntity.ok(productService.getProductAnalysis(dt));
        } catch (Exception e) {
            log.error("获取商品分析失败", e);
            return ResponseEntity.status(500).body("获取商品分析失败");
        }
    }

    @GetMapping("/categories/tree")
    public ResponseEntity<?> getCategoryTree() {
        try {
            return ResponseEntity.ok(productService.getCategoryTree());
        } catch (Exception e) {
            log.error("获取类目树失败", e);
            return ResponseEntity.status(500).body("获取类目树失败");
        }
    }
}
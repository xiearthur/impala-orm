package com.example.impala.example.service;

import com.example.impala.example.mapper.CategoryMapper;
import com.example.impala.example.mapper.ProductMapper;
import com.example.impala.example.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public Map<String, Object> getProductAnalysis(String dt) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("product_stats", productMapper.getProductSalesStats(dt));
        analysis.put("category_stats", productMapper.getCategorySalesStats(dt));
        return analysis;
    }

    public Map<String, Object> getCategoryTree() {
        List<Category> topCategories = categoryMapper.findTopCategories();
        return topCategories.stream()
                .map(cat -> {
                    Map<String, Object> node = new HashMap<>();
                    node.put("category", cat);
                    node.put("children", categoryMapper.findByParentId(cat.getCategoryId()));
                    return node;
                })
                .collect(Collectors.toMap(
                        m -> ((Category)m.get("category")).getCategoryId(),
                        m -> m
                ));
    }
}
package com.example.impala.example.mapper;

import com.example.impala.example.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

// ProductMapper.java
@Mapper
public interface ProductMapper {
    @Select("SELECT * FROM product WHERE product_id = #{productId} AND dt = #{dt}")
    Product findById(@Param("productId") String productId, @Param("dt") String dt);

    @Select("SELECT p.*, c.category_name, " +
            "COUNT(DISTINCT od.order_id) as order_count, " +
            "SUM(od.quantity) as total_sold, " +
            "SUM(od.actual_amount) as total_amount " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.category_id " +
            "LEFT JOIN order_detail od ON p.product_id = od.product_id AND p.dt = od.dt " +
            "WHERE p.dt = #{dt} " +
            "GROUP BY p.product_id, p.category_id, p.brand_id, p.product_name, " +
            "p.product_desc, p.market_price, p.sale_price, p.stock_quantity, " +
            "p.sale_status, p.create_time, p.update_time, p.dt, " +
            "c.category_name")
    List<Map<String, Object>> getProductSalesStats(@Param("dt") String dt);

    @Select("SELECT c.category_name, " +
            "COUNT(DISTINCT p.product_id) as product_count, " +
            "SUM(od.quantity) as total_sold, " +
            "SUM(od.actual_amount) as total_amount " +
            "FROM category c " +
            "LEFT JOIN product p ON c.category_id = p.category_id " +
            "LEFT JOIN order_detail od ON p.product_id = od.product_id AND p.dt = od.dt " +
            "WHERE p.dt = #{dt} " +
            "GROUP BY c.category_name")
    List<Map<String, Object>> getCategorySalesStats(@Param("dt") String dt);
}
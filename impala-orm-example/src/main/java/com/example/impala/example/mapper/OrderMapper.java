package com.example.impala.example.mapper;

import com.example.impala.example.model.OrderMain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    // 基础查询
    @Select("SELECT * FROM order_main WHERE order_id = #{orderId} AND dt = #{dt}")
    OrderMain getById(@Param("orderId") String orderId, @Param("dt") String dt);

    // 复杂查询 - 订单详情聚合
    @Select("SELECT " +
            "    om.*, " +
            "    COUNT(od.detail_id) as item_count, " +
            "    STRING_AGG(CONCAT(od.product_name, '(', od.quantity, ')'), ',') as items " +
            "FROM order_main om " +
            "LEFT JOIN order_detail od ON om.order_id = od.order_id AND om.dt = od.dt " +
            "WHERE om.dt = #{dt} " +
            "GROUP BY om.order_id, om.user_id, om.order_status, om.order_amount, " +
            "         om.payment_method, om.shipping_address, om.create_time, " +
            "         om.pay_time, om.delivery_time, om.complete_time, om.dt")
    List<Map<String, Object>> findOrdersWithDetails(@Param("dt") String dt);

    // 复杂查询 - 销售分析
    @Select("SELECT " +
            "    c.category_name, " +
            "    COUNT(DISTINCT om.order_id) as order_count, " +
            "    SUM(od.actual_amount) as total_sales " +
            "FROM order_detail od " +
            "JOIN order_main om ON od.order_id = om.order_id AND od.dt = om.dt " +
            "JOIN product p ON od.product_id = p.product_id AND od.dt = p.dt " +
            "JOIN category c ON p.category_id = c.category_id " +
            "WHERE od.dt = #{dt} " +
            "GROUP BY c.category_name " +
            "ORDER BY total_sales DESC")
    List<Map<String, Object>> analyzeOrdersByCategory(@Param("dt") String dt);

    // 复杂查询 - 热销商品
    @Select("SELECT " +
            "    p.product_id, " +
            "    p.product_name, " +
            "    c.category_name, " +
            "    COUNT(DISTINCT od.order_id) as order_count, " +
            "    SUM(od.quantity) as total_quantity, " +
            "    SUM(od.actual_amount) as total_amount " +
            "FROM order_detail od " +
            "JOIN product p ON od.product_id = p.product_id AND od.dt = p.dt " +
            "JOIN category c ON p.category_id = c.category_id " +
            "WHERE od.dt = #{dt} " +
            "GROUP BY p.product_id, p.product_name, c.category_name " +
            "HAVING SUM(od.quantity) >= #{minQuantity} " +
            "ORDER BY total_quantity DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findHotProducts(@Param("dt") String dt,
                                              @Param("minQuantity") int minQuantity,
                                              @Param("limit") int limit);
}
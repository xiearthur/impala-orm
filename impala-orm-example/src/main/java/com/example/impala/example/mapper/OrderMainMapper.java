package com.example.impala.example.mapper;

import com.example.impala.example.model.OrderMain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMainMapper {
    @Select("SELECT * FROM order_main WHERE order_id = #{orderId} AND dt = #{dt}")
    OrderMain findById(@Param("orderId") String orderId, @Param("dt") String dt);

    @Select("SELECT om.*, " +
            "COUNT(DISTINCT od.product_id) as product_count, " +
            "SUM(od.quantity) as total_quantity " +
            "FROM order_main om " +
            "LEFT JOIN order_detail od ON om.order_id = od.order_id AND om.dt = od.dt " +
            "WHERE om.dt = #{dt} " +
            "GROUP BY om.order_id, om.user_id, om.order_status, om.order_amount, " +
            "om.payment_method, om.shipping_address, om.create_time, " +
            "om.pay_time, om.delivery_time, om.complete_time, om.dt " +
            "HAVING om.user_id = #{userId}")
    List<Map<String, Object>> findByUserId(@Param("userId") String userId, @Param("dt") String dt);

    @Select("SELECT DATE_FORMAT(om.create_time, '%Y-%m-%d') as order_date, " +
            "COUNT(*) as order_count, " +
            "SUM(om.order_amount) as total_amount, " +
            "COUNT(DISTINCT om.user_id) as customer_count " +
            "FROM order_main om " +
            "WHERE om.dt = #{dt} " +
            "GROUP BY DATE_FORMAT(om.create_time, '%Y-%m-%d') " +
            "ORDER BY order_date")
    List<Map<String, Object>> getDailyStats(@Param("dt") String dt);
}

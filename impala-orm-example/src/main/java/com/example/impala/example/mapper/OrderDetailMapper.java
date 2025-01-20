// OrderDetailMapper.java
package com.example.impala.example.mapper;

import com.example.impala.example.model.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDetailMapper {
    @Select("SELECT * FROM order_detail WHERE order_id = #{orderId} AND dt = #{dt}")
    List<OrderDetail> findByOrderId(@Param("orderId") String orderId, @Param("dt") String dt);

    @Select("SELECT od.*, p.category_id, c.category_name " +
            "FROM order_detail od " +
            "JOIN product p ON od.product_id = p.product_id AND od.dt = p.dt " +
            "JOIN category c ON p.category_id = c.category_id " +
            "WHERE od.dt = #{dt}")
    List<Map<String, Object>> findDetailWithCategory(@Param("dt") String dt);

    @Select("SELECT SUM(actual_amount) as total_amount, " +
            "COUNT(DISTINCT order_id) as order_count, " +
            "COUNT(*) as item_count " +
            "FROM order_detail " +
            "WHERE dt = #{dt}")
    Map<String, Object> getStatistics(@Param("dt") String dt);
}
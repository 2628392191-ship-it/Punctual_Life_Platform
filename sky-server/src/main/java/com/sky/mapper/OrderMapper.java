package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    @Insert("insert into orders (number, status, user_id, address_book_id, order_time,  pay_status, amount, remark, user_name, phone, address, consignee, estimated_delivery_time, delivery_status, pack_amount, tableware_number, tableware_status) " +
            "values ( #{number}, #{status}, #{userId}, #{addressBookId}, #{orderTime},  #{payStatus}, #{amount}, #{remark}, #{userName}, #{phone}, #{address}, #{consignee}, #{estimatedDeliveryTime}, #{deliveryStatus}, #{packAmount}, #{tablewareNumber}, #{tablewareStatus})")
    int insert(Orders orders);

    //根据时间和状态查询订单
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> findOrdersByStatusAndOrderTime(Integer status, LocalDateTime time);

    //根据状态和时间查询订单
    @Select("select * from orders where status = #{status}")
    List<Orders> listByStatus( Integer status);

    //根据状态和日期查询订单（查询指定日期的订单）
    @Select("select * from orders where status = #{status} and DATE(order_time) = DATE(#{orderDate})")
    List<Orders> listByStatusAndDate(@Param("status") Integer status, @Param("orderDate") LocalDateTime orderDate);

    //查询所有订单
    @Select("select * from orders")
    List<Orders> statistics();

    //修改订单
    int update(Orders orders);

    //订单支付成功更新状态
    @Update("update orders set pay_status = #{payStatus},status=#{status} where number = #{number}")
    int PaySuccess(Orders  orders);

    //订单分页查询
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    //订单取消
    @Update("update orders set rejection_reason=#{rejectionReason},status=#{status} where id = #{id}")
    int reject(Orders orders);

    //根据订单id查询对应订单
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    //订单营业额统计
    Double orderStatistics(Map<String, Object> map);

    //订单数量统计
    Integer orderCount(Map<String, Object> map);

    //订单销量top10排名
    List<GoodsSalesDTO> top10(Map<String, Object> map);

}

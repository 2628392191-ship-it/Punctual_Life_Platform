package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.*;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    @Insert("insert into orders (number, status, user_id, address_book_id, order_time,  pay_status, amount, remark, user_name, phone, address, consignee, estimated_delivery_time, delivery_status, pack_amount, tableware_number, tableware_status) " +
            "values ( #{number}, #{status}, #{userId}, #{addressBookId}, #{orderTime},  #{payStatus}, #{amount}, #{remark}, #{userName}, #{phone}, #{address}, #{consignee}, #{estimatedDeliveryTime}, #{deliveryStatus}, #{packAmount}, #{tablewareNumber}, #{tablewareStatus})")
    int insert(Orders orders);

    //根据时间和状态查询订单
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> findOrdersByStatusAndOrderTime(Integer status, LocalDateTime time);


    @Select("select * from orders")
    List<Orders> statistics();

    int update(Orders orders);

    //订单分页查询
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Update("update orders set status = #{status} where id = #{id}")
    int cancel(Orders orders);

    @Update("update orders set status = #{status} where id = #{id}")
    int complete(Orders  orders);

    @Update("update orders set status = #{status} where id = #{id}")
    int reject(Orders orders);

    @Update("update orders set status = #{status} where id = #{id}")
    int confirm(Orders orders);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    @Update("update orders set status = #{status} where id = #{id}")
    int delivery(Orders orders);

}

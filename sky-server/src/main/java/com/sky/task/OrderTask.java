package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    @Resource
    private OrderService orderService;

    /**
     * 定时任务，每隔1分钟查询一次订单状态，判断是否支付超时
     * 先去订单表查询是否有订单支付超时
     * 根据他的订单时间和状态来找，
     * 遍历每一条订单，然后设置订单的状态为已取消，设置取消原因 支付超时，自动取消，设置取消时间，当前时间
     * 支付倒计时5分钟，只有支付成功才能够派送
     */
     @Scheduled(cron = "0 */5 * * * ? ") //整点执行
   //@Scheduled(fixedDelay = 5000) 间隔5秒执行一次
     public void processTimeoutOrder(){
         log.info("处理支付超时订单");
         //计算15分钟前的时间
         LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(15);
         //进行数据表查询，找到15分钟前的订单
         List<Orders> ordersByStatusAndOrderTime = orderMapper.findOrdersByStatusAndOrderTime(Orders.PENDING_PAYMENT, localDateTime);
         if (ordersByStatusAndOrderTime != null&&!ordersByStatusAndOrderTime.isEmpty()){
         ordersByStatusAndOrderTime.forEach(orders -> {
             orders.setCancelReason("支付超时,自动取消");
             orders.setStatus(Orders.CANCELLED);
             orders.setCancelTime(LocalDateTime.now());
             orderMapper.update(orders);
          });
         }
     }

     /**
      * 定时任务，每隔30秒查询一次订单状态，判断是否送达超时
      * 先去订单表查询是否有订单送达超时
      * 根据他的订单时间和状态来找，
      * 遍历每条订单，然后设置订单的状态为已完成，设置送达时间，当前时间
      */
     @Scheduled(cron="0/30 * * * * ?") //每30秒查询1个小时前的的订单
   //@Scheduled(cron = "0 0 1 * * ? ")
     public void processDeliveryOrder(){
         log.info("处理处于超时派送状态的订单");
         //查询1小时前的派送中的订单
         LocalDateTime localDateTime = LocalDateTime.now().minusHours(1);
         //进行数据表查询，找到1小时前的订单
         List<Orders> ordersByStatusAndOrderTime = orderMapper.findOrdersByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, localDateTime);
         if (ordersByStatusAndOrderTime != null&&!ordersByStatusAndOrderTime.isEmpty()){
             ordersByStatusAndOrderTime.forEach(orders -> {
                 orders.setDeliveryStatus(Orders.COMPLETED);
                 orders.setDeliveryTime(LocalDateTime.now());
                 orderMapper.update(orders);
             });
         }
     }


}

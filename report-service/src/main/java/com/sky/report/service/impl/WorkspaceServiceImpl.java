package com.sky.report.service.impl;

import com.sky.api.client.DishClient;
import com.sky.api.client.OrderClient;
import com.sky.api.client.SetmealClient;
import com.sky.api.client.UserClient;

import com.sky.entity.Orders;
import com.sky.gateway.utils.common.constant.StatusConstant;
import com.sky.report.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {
       @Resource
       private OrderClient orderClient;
       @Resource
       private UserClient userClient;
       @Resource
       private SetmealClient setmealClient;
       @Resource
       private DishClient dishClient;

       @Override
       public BusinessDataVO getBusinessData(LocalDateTime beginTime, LocalDateTime endTime){
              Map<String, Object> map=new HashMap<>();
              map.put("beginTime", beginTime);
              map.put("endTime", endTime);
              map.put("status", Orders.COMPLETED);
              return fillBusinessData(map);
       }

       @Override
       public SetmealOverViewVO overviewSetmeals(){
              return SetmealOverViewVO.builder()
                      .discontinued(setmealClient.listByStatus(StatusConstant.DISABLE).getData().size())
                      .sold(setmealClient.listByStatus(StatusConstant.ENABLE).getData().size())
                      .build();
       }

       @Override
       public DishOverViewVO overviewDishes(){
              return DishOverViewVO.builder()
                      .discontinued(dishClient.listByStatus(StatusConstant.DISABLE).getData().size())
                      .sold(dishClient.listByStatus(StatusConstant.ENABLE).getData().size())
                      .build();
       }

       //需要将订单修改为当天订单信息，目前为所有的订单状态信息
       @Override
       public OrderOverViewVO overviewOrders(){
             return OrderOverViewVO.builder()
                     .allOrders(orderClient.total().getData().size())
                     .cancelledOrders(orderClient.listByStatusAndDate(Orders.CANCELLED, LocalDateTime.now()).getData().size())
                     .completedOrders(orderClient.listByStatusAndDate(Orders.COMPLETED, LocalDateTime.now()).getData().size())
                     .deliveredOrders(orderClient.listByStatusAndDate(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now()).getData().size())
                     .waitingOrders(orderClient.listByStatusAndDate(Orders.TO_BE_CONFIRMED, LocalDateTime.now()).getData().size())
                     .build();
       }

       public BusinessDataVO fillBusinessData(Map<String ,Object> map){
              Double valueStatistics = orderClient.orderStatistics(map).getData();
              Integer userStatistics = userClient.UserStatisticsTotal(map).getData();
              Integer validOrder = orderClient.orderCount(map).getData();
              map.remove("status");
              Integer orderCount = orderClient.orderCount(map).getData();
              double orderCompletionRate=0.0;
              if(orderCount != 0) orderCompletionRate = validOrder.doubleValue()/orderCount;
              double unitPrice = 0.0;
              if(validOrder != 0) unitPrice = valueStatistics/validOrder;
              return BusinessDataVO.builder()
                     .turnover(valueStatistics)
                     .validOrderCount(validOrder)
                     .orderCompletionRate(orderCompletionRate)
                     .unitPrice(unitPrice)
                     .newUsers(userStatistics)
                     .build();
       }


}

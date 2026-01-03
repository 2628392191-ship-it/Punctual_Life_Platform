package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.OrderService;
import com.sky.service.SetmealService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

       @Autowired
       private OrderMapper orderMapper;
       @Autowired
       private UserMapper userMapper;
       @Autowired
       private SetmealMapper setmealMapper;
       @Autowired
       private DishMapper dishMapper;

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
                      .discontinued(setmealMapper.listByStatus(StatusConstant.DISABLE).size())
                      .sold(setmealMapper.listByStatus(StatusConstant.ENABLE).size())
                      .build();
       }

       @Override
       public DishOverViewVO overviewDishes(){
              return DishOverViewVO.builder()
                      .discontinued(dishMapper.listByStatus(StatusConstant.DISABLE).size())
                      .sold(dishMapper.listByStatus(StatusConstant.ENABLE).size())
                      .build();
       }

       //需要将订单修改为当天订单信息，目前为所有的订单状态信息
       @Override
       public OrderOverViewVO overviewOrders(){
             return OrderOverViewVO.builder()
                     .allOrders(orderMapper.statistics().size())
                     .cancelledOrders(orderMapper.listByStatusAndDate(Orders.CANCELLED, LocalDateTime.now()).size())
                     .completedOrders(orderMapper.listByStatusAndDate(Orders.COMPLETED, LocalDateTime.now()).size())
                     .deliveredOrders(orderMapper.listByStatusAndDate(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now()).size())
                     .waitingOrders(orderMapper.listByStatusAndDate(Orders.TO_BE_CONFIRMED, LocalDateTime.now()).size())
                     .build();
       }

       public BusinessDataVO fillBusinessData(Map<String ,Object> map){
              Double valueStatistics = orderMapper.orderStatistics(map);
              Integer userStatistics = userMapper.UserStatistics(map);
              Integer validOrder = orderMapper.orderCount(map);
              map.remove("status");
              Integer orderCount = orderMapper.orderCount(map);
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

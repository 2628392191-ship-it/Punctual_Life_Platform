package com.sky.order.service.impl;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.api.client.ShopClient;
import com.sky.api.client.UserClient;

import com.sky.dto.*;
import com.sky.entity.*;

import com.sky.gateway.utils.common.constant.MessageConstant;
import com.sky.gateway.utils.common.context.BaseContext;
import com.sky.gateway.utils.common.exception.OrderBusinessException;
import com.sky.gateway.utils.common.result.PageResult;
import com.sky.order.mapper.OrderDetailMapper;
import com.sky.order.mapper.OrderMapper;
import com.sky.order.service.OrderService;
import com.sky.order.websocket.WebSocketServer;

import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WebSocketServer webSocketServer;
    @Resource
    private UserClient userClient;
    @Resource
    private ShopClient shopClient;

    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO){
        //创建订单对象，并准备实施添加
        Orders build = Orders.builder().build();
        BeanUtils.copyProperties(ordersSubmitDTO, build);
        User user = userClient.getUserById(BaseContext.getCurrentId()).getData();
        AddressBook addressBook = userClient.getAddressBookById(ordersSubmitDTO.getAddressBookId()).getData();

        //对订单数据的填充
        fillOrder(build,addressBook,user);
        int addorder = orderMapper.insert(build);

        //对订单详细的填充
        //详细需要从购物车来拿
        //从购物车中获取数据
        List<ShoppingCart> carts =shopClient.list().getData();
       List<OrderDetail> orderDetails =carts.stream().map(cart->{
              OrderDetail detail=OrderDetail.builder().build();
              BeanUtils.copyProperties(cart,detail);
             //需要主键回填，要不然为null
              detail.setOrderId(build.getId());
              return detail;
       }).collect(Collectors.toList());
        int addorderdetail = orderDetailMapper.addAll(orderDetails);
        if(addorder>0&&addorderdetail>0) {
            log.info("订单提交成功");
            shopClient.clean();
        }
        return OrderSubmitVO.builder()
                .id(build.getId())
                .orderNumber(build.getNumber())
                .orderAmount(build.getAmount())
                .orderTime(build.getOrderTime())
                .build();
    }


    /**
     * 管理端订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO){
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> pa = orderMapper.pageQuery(ordersPageQueryDTO);
        return new PageResult(pa.getTotal(), pa.getResult());
    }

    /**
     * 用户端订单分页查询
     * @param ordersPageQueryDTO
     * @return
     * 将OrderVO属性填充（并不需要单独设置一个mapper来处理这样做反而复杂化，而且也不需要）
     * Done!!
     */
    @Override
    public PageResult UserPageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> pa=orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> orderVOS=new ArrayList<>();
        //对要查询的这一页的数据进行填充并以VO对象返回
        if(pa!=null&&!pa.isEmpty()) {
            orderVOS=pa.stream().map(order -> {
                List<OrderDetail> byOrderId = orderDetailMapper.getByOrderId(order.getId());
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order,orderVO);
                orderVO.setOrderDetailList(byOrderId);
                return orderVO;
            }).collect(Collectors.toList());
        }
        //返回的是分页集合
        return new PageResult(pa.getTotal(), orderVOS);
    }


    /**
     * 管理端订单取消
     * @param ordersCancelDTO
     * 订单取消应该是超时未支付，商家只能够拒绝订单
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO){
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orderMapper.update( orders);
    }

    @Override
    public void UserCancel(Long id){
        Orders byId = orderMapper.getById(id);
        byId.setStatus(Orders.CANCELLED);
    }




    /**
     * 订单确认
     * @param id
     */
    @Override
    public void complete(Long id){
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update( orders);
    }

    /**
     * 订单拒绝
     * @param rejectionDTO
     */
    @Override
    public void reject(OrdersRejectionDTO rejectionDTO){
        Orders orders = orderMapper.getById(rejectionDTO.getId());
        orders.setCancelReason(rejectionDTO.getRejectionReason());
        orders.setStatus(Orders.CANCELLED);
        orders.setPayStatus(Orders.REFUND);
        orders.setRejectionReason(rejectionDTO.getRejectionReason());
        orderMapper.reject(orders);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetition(Long id){
        Orders byId = orderMapper.getById(id);
        orderMapper.insert(byId);
    }



    /**
     * 订单确认
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO){
        Orders orders = orderMapper.getById(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id){
        Orders orders = orderMapper.getById(id);
        List<OrderDetail> byOrderId = orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(byOrderId);
        return orderVO;
    }

    /**
     * 订单配送
     * @param id
     */
    @Override
    public void delivery(Long id){
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }


    /**
     * 订单统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics(){
        List<Orders> statistics = orderMapper.statistics();
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        if(statistics!=null&&!statistics.isEmpty()){
            statistics.forEach(orders -> {
                Integer status = orders.getStatus();
                //判断是否为待接单状态
                if(status.equals(Orders.TO_BE_CONFIRMED))
                    orderStatisticsVO.setToBeConfirmed((orderStatisticsVO.getToBeConfirmed() == null ? 0 : orderStatisticsVO.getToBeConfirmed()) + 1);
                //判断是否为已接单状态
                if(status.equals(Orders.CONFIRMED))
                    orderStatisticsVO.setConfirmed((orderStatisticsVO.getConfirmed() == null ? 0 : orderStatisticsVO.getConfirmed()) + 1);
                //判断是否为派送中状态
                if(status.equals(Orders.DELIVERY_IN_PROGRESS))
                    orderStatisticsVO.setDeliveryInProgress((orderStatisticsVO.getDeliveryInProgress() == null ? 0 : orderStatisticsVO.getDeliveryInProgress()) + 1);
            });
        }
        return orderStatisticsVO;
    }

    /**
     * 用户催单
     * @param id
     */
    @Override
    public void reminder(Long id){
        //先在数据库中查询是否有订单
        Orders byId = orderMapper.getById(id);
        if(byId==null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        //用户催单功能使用WebSocket进行推送
        Map<String,Object> map=new HashMap<>();
        map.put("type",2);
        map.put("orderId",id);
        map.put("content","订单号"+byId.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    /**
     * 支付成功，修改订单状态
     * @param ordersPaymentDTO
     */
    @Override
    public void PaySuccess(OrdersPaymentDTO ordersPaymentDTO) {
        //支付成功后填充成功状态信息
        Orders build = Orders.builder()
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .number(ordersPaymentDTO.getOrderNumber())
                .build();
        orderMapper.PaySuccess(build);

         /**
         * 推送消息
         * type为消息类型，1为来单提醒，2为订单状态提醒
         * orderId为订单Id
         * content为消息内容
         */
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", build.getNumber());
        map.put("content", "订单号" + build.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
           List<LocalDate> dateList = new ArrayList<>();
           //开始时间-结束时间---这段时间的订单营业额
           while (!begin.isAfter(end)) {
               //把开始的日期加入集合
               dateList.add(begin);
               //循环递增
               begin = begin.plusDays(1);
           }

           //构建每日营业额的一个集合（这段时间段的总营业额）
           List<Double> turnoverList = new ArrayList<>();
           //遍历这段时间
           dateList.forEach(date -> {
                //目的：拿到这一天有订单完成的营业额
                LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

                //向数据库查询
                Map<String, Object> map=new HashMap<>();
                map.put("beginTime", beginTime);
                map.put("endTime", endTime);
                map.put("status", Orders.COMPLETED);
                //拿到这一天的营业额
                Double turnover = orderMapper.orderStatistics(map);
                //进行判断是否营业额为null
                turnoverList.add(turnover == null ? 0.0 : turnover);
           });
       return new TurnoverReportVO(StringUtils.join(dateList, ","), StringUtils.join(turnoverList, ","));
    }


    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.isAfter(end)) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        List<Integer> orderCountList=new ArrayList<>();
        List<Integer> validOrderCountList=new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<String, Object> map=new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            //每日订单总量
            Integer orderCount = orderMapper.orderCount(map);
            orderCountList.add(orderCount == null ? 0 : orderCount);

            //每日有效订单数量
            map.put("status", Orders.COMPLETED);
            Integer validOrder = orderMapper.orderCount(map);
            validOrderCountList.add(validOrder == null ? 0 : validOrder);
        });

        //订单总量
        //使用stream流来实现总量的填充
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        //有效订单总量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        //订单有效率
        double rate=0.0;
        if(totalOrderCount!=0) rate=validOrderCount.doubleValue() / totalOrderCount;

    return new OrderReportVO(StringUtils.join(dateList, ","),
            StringUtils.join(orderCountList, ","),
            StringUtils.join(validOrderCountList, ","), totalOrderCount, validOrderCount, rate);
    }


    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end){
         //冗余，建议设计函数去冗余
         LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
         LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
         Map<String, Object> map=new HashMap<>();
         map.put("beginTime", beginTime);
         map.put("endTime", endTime);
         map.put("status", Orders.COMPLETED);
         List<GoodsSalesDTO> goodsSalesDTO = orderMapper.top10(map);

         String namelist = goodsSalesDTO.stream().map(GoodsSalesDTO::getName).collect(Collectors.joining(","));
         String numberlist = goodsSalesDTO.stream().map(dto->dto.getNumber().toString()).collect(Collectors.joining(","));
         return new SalesTop10ReportVO(namelist, numberlist);
    }

    @Override
    public List<Orders> listByStatusAndDate(Integer status, LocalDateTime orderDate) {
        return orderMapper.listByStatusAndDate(status, orderDate);
    }

    @Override
    public List<Orders> totalOrders() {
        return orderMapper.statistics();
    }

    @Override
    public Double orderStatistics(Map<String, Object> map) {
        return orderMapper.orderStatistics(map);
    }

    @Override
    public Integer orderCount(Map<String, Object> map) {
        return orderMapper.orderCount(map);
    }


    private void fillOrder(Orders build, AddressBook addressBook, User user) {
        build.setNumber(UUID.randomUUID().toString().replace("-", ""));
        build.setUserId(BaseContext.getCurrentId());
        build.setOrderTime(LocalDateTime.now());
        build.setStatus(Orders.PENDING_PAYMENT);
        build.setUserName(user.getName());
        /* 补充数据
         * 设置id：自增id，不用设置
         * 设置number：订单号，使用UUID Done!
         * 设置user_id：用户id，Done!
         * 设置id：address_book_id，页面有提供不用设置
         * 设置order_time：下单时间，Done!
         * 设置checkout_time：付款时间，目前无法设置，等后续更新设置
         * 设置pay_method：支付方式，下单才知道，后续更新设置
         * 设置pay_status：支付状态，目前为未支付，Done!
         * 设置amount：订单金额，页面提供
         * 设置remark：备注，前端提供
         *  Done!!!
         *  1.收货人电话，需要设置   phone
         *  2.收货人地址，需要设置   address
         *  3.下单的这个用户的昵称   user_name
         *  4.收货人姓名         consignee
         * 设置cancel_reason：取消原因，目前为空，后续更新设置
         * rejection_reason：拒绝原因，目前为空，后续更新设置
         * estimateDeliveryTime：预计送达时间，页面提供，不用设置
         * delivery_status：配送状态，页面提供，不用设置
         * pack_amount：打包费，页面提供，不用设置
         * tablewareNumber: 餐具数量，页面提供，不用设置
         * tablewareStatus：餐具状态，页面提供，不用设置
         **/
        build.setPayStatus(Orders.UN_PAID);
        build.setPhone(addressBook.getPhone());
        build.setAddress(addressBook.getDetail());
        build.setUserName(user.getName());
        build.setConsignee(addressBook.getConsignee());
        //移除当前线程的用户id
        BaseContext.removeCurrentId();
    }


}

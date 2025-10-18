package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.AddressBookService;
import com.sky.service.OrderService;
import com.sky.service.shoppingCartService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private shoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailMapper orderDetailMapper;


    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO){
        //创建订单对象，并准备实施添加
        Orders build = Orders.builder().build();
        BeanUtils.copyProperties(ordersSubmitDTO, build);
        User user = userMapper.getById(BaseContext.getCurrentId());
        AddressBook addressBook = addressBookService.getById(ordersSubmitDTO.getAddressBookId());

        //对订单数据的填充
        fillOrder(build,addressBook,user);
        int addorder = orderMapper.insert(build);

        //TODO::对订单详细的填充
        //详细需要从购物车来拿
        //TODO::从购物车中获取数据
        List<ShoppingCart> carts =shoppingCartService.list();
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
            shoppingCartService.deleteCart();
        }
        return OrderSubmitVO.builder()
                .id(build.getId())
                .orderNumber(build.getNumber())
                .orderAmount(build.getAmount())
                .orderTime(build.getOrderTime())
                .build();
    }






    private void fillOrder(Orders build,AddressBook addressBook,User user) {
        build.setNumber(UUID.randomUUID().toString().replace("-", ""));
        build.setUserId(BaseContext.getCurrentId());
        build.setOrderTime(LocalDateTime.now());
        build.setStatus(Orders.PENDING_PAYMENT);
        /* 补充数据
         * 设置id：自增id，不用设置
         * 设置number：订单号，使用UUID Done!
         * 设置user_id：用户id，Done!
         * 设置id：address_book_id，页面有提供不用设置
         * 设置order_time：下单时间，Done!
         * TODO::设置checkout_time：付款时间，目前无法设置，等后续更新设置
         * TODO::设置pay_method：支付方式，下单才知道，后续更新设置
         * 设置pay_status：支付状态，目前为未支付，Done!
         * 设置amount：订单金额，页面提供
         * 设置remark：备注，前端提供
         * TODO:: Done!!!
         *  1.收货人电话，需要设置   phone
         *  2.收货人地址，需要设置   address
         *  3.下单的这个用户的昵称   user_name
         *  4.收货人姓名         consignee
         * TODO::设置cancel_reason：取消原因，目前为空，后续更新设置
         * TODO::rejection_reason：拒绝原因，目前为空，后续更新设置
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
    }


}

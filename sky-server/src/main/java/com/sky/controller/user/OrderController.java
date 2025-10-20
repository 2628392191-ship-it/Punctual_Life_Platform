package com.sky.controller.user;


import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "用户订单接口")
@RestController("UserOrderController")
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @ApiOperation("提交订单")
    @RequestMapping("/submit")
    public Result<OrderSubmitVO> OrderSubmit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO submit = orderService.submit(ordersSubmitDTO);
        return Result.success(submit);
    }

    @ApiOperation("订单支付")
    @PutMapping("/payment")
    public Result pay(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("调用订单支付功能");
        return Result.success();
    }

}

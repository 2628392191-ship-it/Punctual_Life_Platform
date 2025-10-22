package com.sky.controller.user;


import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "用户订单接口")
@RestController("UserOrderController")
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @ApiOperation("用户提交订单")
    @RequestMapping("/submit")
    public Result<OrderSubmitVO> OrderSubmit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO submit = orderService.submit(ordersSubmitDTO);
        return Result.success(submit);
    }

    @ApiOperation("用户催单")
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Long id){
        log.info("用户催单");
        orderService.reminder(id);
        return Result.success();
    }

    @ApiOperation("订单支付")
    @PutMapping("/payment")
    public Result pay(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("调用订单支付功能");
        orderService.PaySuccess(ordersPaymentDTO);
        return Result.success();
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id){
        log.info("用户取消订单");
        orderService.UserCancel(id);
        return Result.success();
    }


    @ApiOperation("用户查询订单详情")
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> ordersDetails(@PathVariable Long id){
        log.info("查询订单详情");
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    //TODO::这里的分页查询有问题，需要改请求参数
    @ApiOperation("历史订单查询")
    @GetMapping("/historyOrders")
    public Result<PageResult> page(Integer status,int page,int pageSize) {
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
         ordersPageQueryDTO.setStatus(status);
         ordersPageQueryDTO.setPage(page);
         ordersPageQueryDTO.setPageSize(pageSize);
        log.info("分页查询：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.UserPageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }


    @ApiOperation("再来一单")
    @PostMapping("/repetition/{id}")
    public Result repertition(@PathVariable Long id){
        log.info("再来一单");
        orderService.repetition(id);
        return Result.success();
    }


}

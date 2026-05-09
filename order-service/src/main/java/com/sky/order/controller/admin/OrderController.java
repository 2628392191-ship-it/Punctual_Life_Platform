package com.sky.order.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;

import com.sky.gateway.utils.common.result.PageResult;
import com.sky.gateway.utils.common.result.Result;
import com.sky.order.service.OrderService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/order")
@Api(tags = "管理端-订单管理")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("订单搜索")
    @GetMapping("/conditionSearch")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("分页查询订单");
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("订单统计")
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics(){
        log.info("订单统计");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单");
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    @ApiOperation("完成订单")
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id){
        log.info("完成订单");
        orderService.complete(id);
        return Result.success();
    }

    @ApiOperation("拒单")
    @PutMapping("/rejection")
    public Result reject(@RequestBody OrdersRejectionDTO rejectionDTO){
        log.info("拒绝订单");
        orderService.reject(rejectionDTO);
        return Result.success();
    }

    @ApiOperation("接单")
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单");
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    @ApiOperation("查看订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Long id){
        log.info("查看订单详情");
        OrderVO ordervo = orderService.details(id);
        return Result.success(ordervo);
    }

    @ApiOperation("派送订单")
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id){
        log.info("派送订单");
        orderService.delivery(id);
        return Result.success();
    }

    @ApiOperation(("根据状态和日期来查询订单"))
    @GetMapping("/StatusAndDate")
    public Result<List<Orders>> listByStatusAndDate(@RequestParam Integer status,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate orderDate){
        return Result.success(orderService.listByStatusAndDate(status, orderDate.atStartOfDay()));
    }

    @ApiOperation("查询总订单")
    @GetMapping("/total")
    public Result<List<Orders>> total(){
        return Result.success(orderService.totalOrders());
    }

    @ApiOperation("当天营业额")
    @PostMapping("/turnoverStatistics")
    public Result<Double> orderStatistics(@RequestBody Map<String ,Object> map){
        return Result.success(orderService.orderStatistics(map));
    }

    @ApiOperation("查询订单量")
    @PostMapping("/orderCount")
    public Result<Integer> orderCount(@RequestBody Map<String ,Object> map){
        return Result.success(orderService.orderCount(map));
    }

    @ApiOperation("报表营业额统计")
    @GetMapping("/reportTurnoverStatistics")
    public Result<TurnoverReportVO> reportTurnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        return Result.success(orderService.turnoverStatistics(begin, end));
    }

    @ApiOperation("订单统计")
    @GetMapping("/orderStatistics")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        return Result.success(orderService.ordersStatistics(begin, end));
    }

    @ApiOperation("销量top10")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("销量top10：{}到{}", begin, end);
        return Result.success(orderService.top10(begin, end));
    }
}

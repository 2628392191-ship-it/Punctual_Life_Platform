package com.sky.api.client;

import com.sky.entity.Orders;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FeignClient("order-service")
public interface OrderClient {

    @GetMapping("/admin/order/StatusAndDate")
    Result<List<Orders>> listByStatusAndDate(@RequestParam Integer status, @RequestParam String orderDate);

    @GetMapping("/admin/order/total")
    Result<List<Orders>> total();

    @PostMapping("/admin/order/turnoverStatistics")
    Result<Double> orderStatistics(@RequestBody Map<String ,Object> map);

    @PostMapping("/admin/order/orderCount")
    Result<Integer> orderCount(@RequestBody Map<String ,Object> map);

    @GetMapping("/admin/order/reportTurnoverStatistics")
    Result<TurnoverReportVO> reportTurnoverStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @GetMapping("/admin/order/orderStatistics")
    Result<OrderReportVO> orderStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @GetMapping("/admin/order/top10")
    Result<SalesTop10ReportVO> top10(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end);

}

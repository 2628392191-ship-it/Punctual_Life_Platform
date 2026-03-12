package com.sky.api.client;

import com.sky.entity.Orders;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.OrderReportVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@FeignClient("order-service")
public interface OrderClient {

    @GetMapping("/admin/order/StatusAndDate")
    Result<List<Orders>> listByStatusAndDate(@SpringQueryMap Integer status, @SpringQueryMap LocalDateTime orderDate);

    @GetMapping("/admin/order/total")
    Result<List<Orders>> total();

    @GetMapping("/admin/order/turnoverStatistics")
    Result<Double> orderStatistics(@RequestBody Map<String ,Object> map);

    @GetMapping("/admin/order/orderCount")
    Result<Integer> orderCount(@RequestBody Map<String ,Object> map);

    @GetMapping("/admin/order/turnoverStatistics")
    TurnoverReportVO turnoverStatistics(@SpringQueryMap @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                        @SpringQueryMap @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @GetMapping("/admin/order/reportTurnoverStatistics")
    TurnoverReportVO reportTurnoverStatistics(@SpringQueryMap @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                              @SpringQueryMap @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @GetMapping("/admin/order/orderStatistics")
    OrderReportVO orderStatistics(@SpringQueryMap @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                  @SpringQueryMap @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @GetMapping("/admin/order//top10")
    SalesTop10ReportVO top10(@SpringQueryMap @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                             @SpringQueryMap @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end);

}

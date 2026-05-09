package com.sky.api.client;

import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient("report-service")
public interface ReportClient {

    @GetMapping("/admin/report/businessData")
    Result<BusinessDataVO> businessData(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @GetMapping("/admin/report/turnoverStatistics")
    Result<TurnoverReportVO> turnoverStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @GetMapping("/admin/report/userStatistics")
    Result<UserReportVO> userStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @GetMapping("/admin/report/ordersStatistics")
    Result<OrderReportVO> ordersStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @GetMapping("/admin/report/top10")
    Result<SalesTop10ReportVO> top10(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);
}

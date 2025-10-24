package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.service.UserService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.time.LocalDate;

@Api(tags="数据统计相关接口")
@Slf4j
@RestController
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReportService reportService;


    @ApiOperation("营业额统计")
    @GetMapping("/turnoverStatistics")
    public Result turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额统计：{}到{}", begin, end);
        return Result.success(orderService.turnoverStatistics(begin, end));
    }

    @ApiOperation("用户统计")
    @GetMapping("/userStatistics")
    public Result userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("用户统计：{}到{}", begin, end);
        return Result.success(userService.UserStatistics(begin, end));
    }

    @ApiOperation("订单统计")
    @GetMapping("/ordersStatistics")
    public Result ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("订单统计：{}到{}", begin, end);
        return Result.success(orderService.ordersStatistics(begin, end));
    }

    @ApiOperation("销量top10")
    @GetMapping("/top10")
    public Result top10(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("销量top10：{}到{}", begin, end);
        return Result.success(orderService.top10(begin, end));
    }

    @ApiOperation("导出Excel报表接口")
    @GetMapping("/export")
    public void export()  {
        log.info("导出Excel报表接口");
        try {
            reportService.export();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

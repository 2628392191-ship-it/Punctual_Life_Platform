package com.sky.report.controller;

import com.sky.gateway.utils.common.result.Result;
import com.sky.report.service.ReportService;
import com.sky.report.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@Api(tags="管理端-数据统计")
@Slf4j
@RestController
@RequestMapping("/admin/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @Autowired
    private WorkspaceService workspaceService;

    @ApiOperation("区间经营数据")
    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("区间经营数据：{}到{}", begin, end);
        return Result.success(workspaceService.getBusinessData(begin.atStartOfDay(), end.atTime(LocalTime.MAX)));
    }

    @ApiOperation("营业额统计")
    @GetMapping("/turnoverStatistics")
    public Result turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, 
                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额统计：{}到{}", begin, end);
        return Result.success(reportService.turnoverStatistics(begin, end));
    }

    @ApiOperation("用户统计")
    @GetMapping("/userStatistics")
    public Result userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin, 
                                @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("用户统计：{}到{}", begin, end);
        return Result.success(reportService.userStatistics(begin, end));
    }

    @ApiOperation("订单统计")
    @GetMapping("/ordersStatistics")
    public Result ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin, 
                                   @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("订单统计：{}到{}", begin, end);
        return Result.success(reportService.ordersStatistics(begin, end));
    }

    @ApiOperation("销量top10")
    @GetMapping("/top10")
    public Result top10(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin, 
                       @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("销量top10：{}到{}", begin, end);
        return Result.success(reportService.top10(begin, end));
    }

    @ApiOperation("导出Excel报表接口")
    @GetMapping("/export")
    public void export() {
        log.info("导出Excel报表接口");
        try {
            reportService.export();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

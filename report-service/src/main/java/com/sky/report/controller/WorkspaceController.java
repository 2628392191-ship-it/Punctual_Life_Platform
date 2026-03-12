package com.sky.report.controller;

import com.sky.result.Result;
import com.sky.report.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Api(tags = "工作台接口")
@Slf4j
public class WorkspaceController {
     @Autowired
     private WorkspaceService workspaceService;

     @ApiOperation("查询今日运营数据")
     @GetMapping("/businessData")
     public Result<BusinessDataVO> getBusinessData(){
         log.info("查询今日运营数据");
         LocalDateTime begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
         LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
         return Result.success(workspaceService.getBusinessData(begin, end));
     }

     @ApiOperation("查询套餐总览")
     @GetMapping("/overviewSetmeals")
     public Result overviewSetmeals(){
         log.info("查询套餐总览");
         return Result.success(workspaceService.overviewSetmeals());
     }

    @ApiOperation("查询菜品总览")
    @GetMapping("/overviewDishes")
    public Result overviewDishes(){
         log.info("查询菜品总览");
         return Result.success(workspaceService.overviewDishes());
     }

    @ApiOperation("查询订单管理数据")
    @GetMapping("/overviewOrders")
    public Result overviewOrders(){
        log.info("查询订单管理数据");
        return Result.success(workspaceService.overviewOrders());
    }

}

package com.sky.user.controller;

import com.sky.dto.UserLoginDTO;

import com.sky.entity.User;
import com.sky.gateway.utils.common.result.Result;
import com.sky.user.service.UserService;
import com.sky.vo.UserLoginVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Api(tags="用户端-用户接口")
@RestController
@RequestMapping("/user/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("微信登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信登录：{}",userLoginDTO.getCode());
        return Result.success(userService.wxlogin(userLoginDTO));
    }

    @ApiOperation(("根据id查询用户"))
    @GetMapping("/{userId}")
    public Result<User> getUserById(@PathVariable Long userId){
        return Result.success(userService.getById(userId));
    }

    @ApiOperation("查询当天用户统计")
    @GetMapping("/statistics")
    Result<Integer> UserStatisticsTotal(@RequestBody Map<String, Object> map){
        return Result.success(userService.UserStatisticsTotal(map));
    }

    @ApiOperation("用户统计")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("用户统计：{}到{}", begin, end);
        return Result.success(userService.UserStatistics(begin, end));
    }
}

package com.sky.shop.controller.admin;

import com.sky.gateway.utils.common.constant.MessageConstant;
import com.sky.gateway.utils.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理端-店铺管理")
@RestController
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("设置店铺状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态：{}", status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set("status", status);
        return Result.success();
    }

    @ApiOperation("查询店铺营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status = (Integer)redisTemplate.opsForValue().get(MessageConstant.SHOP_STATUS);
        log.info("查询店铺营业状态:{}",status);
        return Result.success(status==null? 0:status);
    }
}

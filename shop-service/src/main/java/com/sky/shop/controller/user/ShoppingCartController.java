package com.sky.shop.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.gateway.utils.common.result.Result;
import com.sky.shop.service.shoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userShoppingCartController")
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "用户端-购物车接口")
public class ShoppingCartController {

    @Autowired
    private shoppingCartService shoppingCartService;

    @ApiOperation("查询购物车列表")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车: {}", shoppingCartDTO);
        shoppingCartService.addCart(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation("减少购物车商品")
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("减少购物车商品: {}", shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public Result clean() {
        shoppingCartService.deleteCart();
        return Result.success();
    }
}

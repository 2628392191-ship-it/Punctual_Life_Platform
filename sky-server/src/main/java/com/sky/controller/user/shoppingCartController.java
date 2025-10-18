package com.sky.controller.user;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.shoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户购物车接口")
@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class shoppingCartController {
    @Autowired
    private shoppingCartService cartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("添加购物车")
    @PostMapping("/add")
     public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
         log.info("添加购物车");
         cartService.addCart(shoppingCartDTO);
         return Result.success();
     }

     /**
      * 查看购物车
      * @return
      */
     @ApiOperation("查看购物车")
     @GetMapping("/list")
     public Result list(){
         log.info("查看购物车");
         List<ShoppingCart> list = cartService.list();
         return Result.success(list);
     }

     /**
      * 清空购物车
      * @return
      */
     @ApiOperation("清空购物车")
     @DeleteMapping("/clean")
      public Result clean(){
         log.info("清空购物车");
         cartService.deleteCart();
         return Result.success();
     }


}

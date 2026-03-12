package com.sky.api.client;

import com.sky.entity.ShoppingCart;
import com.sky.gateway.utils.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("shop-service")
public interface ShopClient {

    @GetMapping("/user/shoppingCart/list")
    Result<List<ShoppingCart>> list();

    @DeleteMapping("/user/shoppingCart/clean")
    Result clean();
}

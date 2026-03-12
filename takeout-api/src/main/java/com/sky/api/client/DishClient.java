package com.sky.api.client;

import com.sky.entity.Dish;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.DishVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("dish-service")
public interface DishClient {

    @GetMapping("/admin/dish/{id}")
    Result<DishVO> getById(@PathVariable Long id);

    @GetMapping("/admin/dish/list-by-status")
    Result<List<Dish>> listByStatus(@RequestParam Integer status);
}

package com.sky.api.client;

import com.sky.entity.Setmeal;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("setmeal-service")
public interface SetmealClient {

    @GetMapping("/admin/setmeal/dish/{setmealId}")
    Result<List<DishItemVO>> getSetmealByDishIds(@PathVariable Long setmealId);

    @GetMapping("/admin/setmeal/{id}")
    Result<SetmealVO> getById(@PathVariable Long id);

    @GetMapping("/admin/setmeal/list/{status}")
    Result<List<Setmeal>> listByStatus(@PathVariable Integer status);
}

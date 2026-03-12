package com.sky.setmeal.controller.user;


import com.sky.entity.Setmeal;
import com.sky.gateway.utils.common.constant.StatusConstant;
import com.sky.gateway.utils.common.result.Result;
import com.sky.setmeal.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "用户端-套餐接口")
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Cacheable(cacheNames= "setmeal",key = "#a0")
    @ApiOperation("查询分类id所对应的套餐")
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Long categoryId) {
        log.info("查询套餐对应的分类id：{}", categoryId);
        Setmeal setmeal = Setmeal.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        List<Setmeal> setmealList =setmealService.findSetmealByCategoryId(setmeal);
        return Result.success(setmealList);
    }

    @ApiOperation("根据套餐id查询包含的菜品")
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> getSetmealDishById(@PathVariable Long id) {
        log.info("查询套餐id对应的菜品：{}", id);
        List<DishItemVO> setmealVOList = setmealService.getDishBySetmealbyId(id);
        return Result.success(setmealVOList);
    }
}

package com.sky.dish.controller.user;


import com.sky.entity.Dish;

import com.sky.dish.service.DishService;
import com.sky.gateway.utils.common.constant.StatusConstant;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController("userDishController")
@Api(tags = "用户端-菜品接口")
@RequestMapping("/user/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("根据菜品id查询菜品详情")
    @GetMapping("/detail/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("查询菜品详情id：{}", id);
        DishVO dishVO = dishService.findById(id);
        return Result.success(dishVO);
    }

    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("分类id：{}", categoryId);
        String key="dish_"+categoryId;
        List<DishVO> list=(List<DishVO>)redisTemplate.opsForValue().get(key);
        if(list!=null&&!list.isEmpty()){
            log.info("从redis中获取数据");
            return Result.success(list);
        }

        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        list = dishService.DishWithFlavors(dish);
        redisTemplate.opsForValue().set(key,list);
        return Result.success(list);
    }
}

package com.sky.dish.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;

import com.sky.dish.service.DishService;
import com.sky.gateway.utils.common.result.PageResult;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags="管理端-菜品管理")
@Slf4j
public class DishController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DishService dishService;

    private void cleanCache(String key){
        log.info("清理缓存：{}", key);
        Set keys = redisTemplate.keys(key);
        redisTemplate.delete(keys);
    }

    @ApiOperation("新增菜品")
    @PostMapping
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.addDish(dishDTO);
        String key="dish_"+dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    @ApiOperation("菜品分页")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.PageQueryDish(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("菜品删除")
    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids){
        log.info("菜品删除 {}", ids);
        dishService.deleteDish(ids);
        cleanCache("dish_*");
        return Result.success();
    }

    @ApiOperation("根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.findById(id);
        return Result.success(dishVO);
    }

    @ApiOperation("修改菜品")
    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);
        dishService.updateDish(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    @ApiOperation("停售/起售菜品")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("停售/起售菜品：{}", id);
        dishService.startOrStop(status, id);
        cleanCache("dish_*");
        return Result.success();
    }


    @ApiOperation("根据状态查询菜品")
    @GetMapping("/list-by-status")
    public Result<List<Dish>> listByStatus(@RequestParam Integer status){
        return Result.success(dishService.listByStatus(status));
    }
}

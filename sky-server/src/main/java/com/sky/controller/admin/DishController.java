package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags="菜品管理")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @ApiOperation("新增菜品")
    @PostMapping
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.addDish(dishDTO);
        return Result.success();
    }


    @ApiOperation("菜品分页")
    @GetMapping("/page")
    public Result<PageResult>  pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.PageQueryDish(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("菜品删除")
    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids){
        log.info("菜品删除 {}", ids);
        dishService.deleteDish(ids);
        return Result.success();
    }

    @ApiOperation("根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishDTO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}", id);
        DishDTO dishDTO = dishService.findById(id);
        return Result.success(dishDTO);
    }

    @ApiOperation("修改菜品")
    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }



}

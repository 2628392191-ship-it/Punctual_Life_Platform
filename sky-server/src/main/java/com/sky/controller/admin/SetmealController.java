package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;

@Api(tags = "套餐相关接口")
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CacheManager cacheManager;


    @ApiOperation("查询分类id的套餐")
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Long categoryId) {
        log.info("查询套餐对应的分类id：{}", categoryId);
        Setmeal setmeal =Setmeal.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        List<Setmeal>  setmealVOList =setmealService.findSetmealByCategoryId(setmeal);
        return Result.success(setmealVOList);
    }

    @CacheEvict(cacheNames = "setmeal",allEntries = true)
    @ApiOperation("添加套餐")
    @PostMapping
    public Result add(@RequestBody SetmealDTO setmealDTO) {
        log.info("添加套餐：{}", setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    @CacheEvict(cacheNames = "setmeal",key="#setmealDTO.id")
    @ApiOperation("修改套餐")
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);
        //TODO:setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }





}

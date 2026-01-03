package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AdviceName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags="菜品管理")
@Slf4j
public class DishController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DishService dishService;

    /**
     * 清理缓存中菜品数据
     * @param key
     */
    private void CleanCache(String key){
        log.info("清理缓存：{}", key);
        Set keys = redisTemplate.keys(key);
        redisTemplate.delete(keys);
    }

    @ApiOperation("新增菜品")
    @PostMapping
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.addDish(dishDTO);

        //删除缓存中同名的菜品
        String key="dish_"+dishDTO.getCategoryId();
        CleanCache(key);
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
        //删除缓存中同名的菜品
        CleanCache("dish_*");
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
        CleanCache("dish_"+dishDTO.getId());
        return Result.success();
    }

    @ApiOperation("停售/起售菜品")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("停售/起售菜品：{}", id);
        dishService.startOrStop(status, id);
        CleanCache("dish_*");
        return Result.success();
    }


}

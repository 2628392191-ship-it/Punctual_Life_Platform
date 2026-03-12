package com.sky.setmeal.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.gateway.utils.common.result.PageResult;
import com.sky.gateway.utils.common.result.Result;
import com.sky.setmeal.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "管理端-套餐管理")
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

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
        return setmealService.updateSetmeal(setmealDTO);
    }

    @ApiOperation("分页查询套餐")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("根据套餐id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("查询套餐id：{}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @CacheEvict(cacheNames = "setmeal", key="#id")
    @ApiOperation("批量删除套餐")
    @DeleteMapping
    public Result delete(@RequestParam Long id) {
        log.info("批量删除：{}", id);
        return Result.success();
    }

    @ApiOperation("起售、停售套餐")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,@RequestParam Long id) {
         log.info("起售、停售套餐：{}", id);
         return setmealService.startOrStop(status, id);
    }

    @ApiOperation("根据菜品id查询套餐")
    @GetMapping("/{setmealId}")
    public Result<List<DishItemVO>> getSetmealByDishIds(@PathVariable Long setmealId){
        List<DishItemVO> dishBySetmealbyId = setmealService.getDishBySetmealbyId(setmealId);
        return Result.success(dishBySetmealbyId);
    }

    @ApiOperation("根据状态查询套餐")
    @GetMapping("/{status}")
    Result<List<Setmeal>> listByStatus(@PathVariable Integer status){
         return Result.success(setmealService.listByStatus(status));
    }
}

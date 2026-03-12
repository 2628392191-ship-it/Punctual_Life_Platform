package com.sky.setmeal.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.gateway.utils.common.result.PageResult;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    List<Setmeal> findSetmealByCategoryId(Setmeal setmeal);

    List<DishItemVO> getDishBySetmealbyId(Long SetmealId);

    void addSetmeal(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO getById(Long id);

    Result updateSetmeal(SetmealDTO setmealDTO);

    Result startOrStop(Integer status, Long id);

    List<Setmeal> listByStatus(Integer status);
}

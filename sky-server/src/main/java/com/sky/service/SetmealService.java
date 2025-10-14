package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    List<Setmeal> findSetmealByCategoryId(Setmeal setmeal);

    List<DishItemVO> getDishBySetmealbyId(Long SetmealId);
}

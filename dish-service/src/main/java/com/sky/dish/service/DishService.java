package com.sky.dish.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;

import com.sky.gateway.utils.common.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    void addDish(DishDTO dishDTO);

    PageResult PageQueryDish(DishPageQueryDTO dishPageQueryDTO);

    void deleteDish(List<Long> ids);

    DishVO findById(Long id);

    void updateDish(DishDTO dishDTO);

    void startOrStop(Integer status, Long id);

    List<DishVO> DishWithFlavors(Dish dish);

    List<Dish> listByStatus(Integer status);

}

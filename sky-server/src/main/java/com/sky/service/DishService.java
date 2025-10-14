package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    void addDish(DishDTO dishDTO);

    PageResult PageQueryDish(DishPageQueryDTO dishPageQueryDTO);

    void deleteDish(List<Long> ids);
    //根据菜品id查询菜品及它的口味数据
    DishVO findById(Long id);

    void updateDish(DishDTO dishDTO);

    void startOrStop(Integer status, Long id);
    //根据分类id查询菜品集合
    List<DishVO> DishWithFlavors(Dish  dish);


}

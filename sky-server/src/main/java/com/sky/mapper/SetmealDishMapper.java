package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

   //通过菜品id查询是否有套餐
   List<Long> getSetmealDishByDishId(List<Long> dishIds);

}

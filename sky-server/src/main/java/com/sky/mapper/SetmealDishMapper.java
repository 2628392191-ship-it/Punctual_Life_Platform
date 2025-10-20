package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

   //通过菜品id查询是否有套餐
   List<Long> getSetmealDishByDishId(List<Long> dishIds);

   //通过套餐id查询菜品ids --用户端
   public List<DishItemVO> getSetmealDishBySetmealId(Long SetmealId);

   //批量插入
   public int insertBatch(List<SetmealDish> setmealDishes);


   @Select("select * from setmeal_dish where setmeal_id = #{SetmealId}")
   public List<SetmealDish> findSetmealDishBySetmealId(Long SetmealId);


}

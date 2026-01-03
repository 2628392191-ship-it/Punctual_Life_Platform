package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
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

   int updateDetail(SetmealDish setmealDish);

   /**
    * 根据套餐id删除套餐菜品关联记录
    *
    * @param setmealId
    */
   @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
   void deleteBySetmealId(Long setmealId);
}

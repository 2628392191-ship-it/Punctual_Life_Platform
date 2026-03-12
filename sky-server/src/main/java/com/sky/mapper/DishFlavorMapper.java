package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    //批量插入口味数据
    void insertBatch(List<DishFlavor> flavors);

    //通过菜品id批量删除口味
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    int deleteByDishId(Long ids);

    //通过菜品id查询对应口味表数据
    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> findByDishId(Long dishId);
}

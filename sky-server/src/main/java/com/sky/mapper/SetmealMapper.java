package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    //TODO:根据分类id查询套餐
    public List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);



}

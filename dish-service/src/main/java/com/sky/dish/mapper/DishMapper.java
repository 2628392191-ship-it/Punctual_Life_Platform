package com.sky.dish.mapper;

import com.github.pagehelper.Page;

import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;

import com.sky.gateway.utils.common.annotation.AutoFill;
import com.sky.gateway.utils.common.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into dish (name, category_id, price, status, create_time, update_time, create_user, update_user) values (#{name}, #{categoryId}, #{price}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    int insertDish(Dish dish);

    Page<DishVO> PageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Delete("delete from dish where id in (#{id})")
    void DeleteById(Long ids);

    @Select("select * from Dish where id=#{id}")
    Dish findDishById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void updateDish(Dish dish);

    @AutoFill(value = OperationType.UPDATE)
    @Update("update dish set status=#{status},update_time=#{updateTime} where id=#{id}")
    void updateDishStatus(Dish dish);

    @Select("select * from dish where category_id=#{categoryId} and status=#{status}")
    List<Dish> findDish(Dish dish);

    @Select("select * from dish where status=#{status}")
    List<Dish> listByStatus(Integer status);
}

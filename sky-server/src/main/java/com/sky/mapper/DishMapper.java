package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {
    //主键回填需要@Options注解实现，或者通过xml也可以
    //第一个为数据库中的id字段，第二个为实体类中的id字段
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into dish (name, category_id, price, status, create_time, update_time, create_user, update_user) values (#{name}, #{categoryId}, #{price}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    int insertDish(Dish dish);

    Page<DishVO> PageQuery(DishPageQueryDTO dishPageQueryDTO);

    //通过id批量删除菜品
    @Delete("delete from dish where id in (#{id})")
    void DeleteById(Long ids);

    //通过id查询菜品
    @Select("select * from Dish where id=#{id}")
    Dish findDishById(Long id);

    //批量修改菜品且要先删除原口味表的数据
    @AutoFill(value = OperationType.UPDATE)
    void updateDish(Dish dish);

    //修改菜品状态(不能和批量修改一起使用，因为批量修改涉及到口味表的删除的添加)
    @AutoFill(value = OperationType.UPDATE)
    @Update("update dish set status=#{status},update_time=#{updateTime} where id=#{id}")
    void updateDishStatus(Dish dish);

    //根据分类id查询起售菜品
    @Select("select * from dish where category_id=#{categoryId} and status=#{status}")
    List<Dish> findDish(Dish dish);

}

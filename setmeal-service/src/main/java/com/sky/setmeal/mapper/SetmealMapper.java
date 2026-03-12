package com.sky.setmeal.mapper;

import com.github.pagehelper.Page;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.gateway.utils.common.annotation.AutoFill;
import com.sky.gateway.utils.common.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    /**
     * 动态条件查询套餐
     * @return
     * */

    public List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into setmeal (name, category_id, price, status,description,image,create_time,update_time,create_user,update_user) values (#{name},#{categoryId},#{price},#{status},#{description},#{image},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    int insert(Setmeal setmeal);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @Select("select * from setmeal where status = #{status}")
    List<Setmeal> listByStatus(Integer status);

    @AutoFill(value = OperationType.UPDATE)
    int update(Setmeal setmeal);
}

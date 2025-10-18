package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface shoppingCartMapper {
    @Insert("insert into shopping_cart (name,image,dish_id,setmeal_id,dish_flavor,number,amount,create_time,user_id) values (#{name},#{image},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime},#{userId})")
    int addCart(ShoppingCart shoppingCart);

    //查询操作
    //这里采用集合是为了后续方便能够展示购物车的数据
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number=#{number} where id = #{id}")
    int update(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    int delete(Long userId);

}

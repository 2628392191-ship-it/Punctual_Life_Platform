package com.sky.service.impl;

import ch.qos.logback.core.util.ContextUtil;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.shoppingCartMapper;
import com.sky.service.shoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class shoppingCartServiceImpl implements shoppingCartService {

    @Autowired
    private shoppingCartMapper shoppingcartmapper;
    @Autowired
    private DishMapper dishmapper;
    @Autowired
    private SetmealMapper setmealmapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 添加购物车
     * 1.要执行数据的转化，把dto转化为实体类
     * 2.查询数据库，判定这道菜 |套餐，到底是新的数据还是旧的数据
     * 3.如果查询的是新的购物车数据，那么就执行数据库的添加操作
     *
     *
     * @param shoppingCartDTO
     *
     */
    @Override
    public void addCart(ShoppingCartDTO shoppingCartDTO){
        ShoppingCart shoppingCart = ShoppingCart.builder().build();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        List<ShoppingCart> list = shoppingcartmapper.list(shoppingCart);
        //每次向购物车添加，判断是菜品或套餐或菜品+口味，只有一种菜被添加！
        //先判断集合数据是否存在，若不存在则实行添加操作，若有则数量+1
        if(list!=null&&!list.isEmpty()){
            //故使用list集合的第一个元素并判断是否是新数据
            //若该数据存在，则进行数量的更新
            list.get(0).setNumber(list.get(0).getNumber()+1);
            shoppingcartmapper.update(list.get(0));
        }
        //若该数据不存在则进行添加
        else{
               //Done：判断是否菜品id为null，若不为null则说明它是菜品进行添加
               if(shoppingCartDTO.getDishId()!=null){
                   Dish dishById = dishmapper.findDishById(shoppingCart.getDishId());
                   addShopCart(shoppingCart,dishById.getName(),dishById.getImage(),dishById.getPrice());
               }
               else{
               //Done：根据上步的操作就能判断是套餐，进行添加
                   Setmeal byId = setmealmapper.getById(shoppingCart.getSetmealId());
                   addShopCart(shoppingCart,byId.getName(),byId.getImage(),byId.getPrice());
               }
        }

    }

    /**
     * 获取购物车数据
     * @return
     */
    @Override
    public List<ShoppingCart> list(){
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return shoppingcartmapper.list(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void deleteCart(){
        shoppingcartmapper.delete(BaseContext.getCurrentId());
    }

    /**
     * 添加购物车数据（简化代码的）
     * @param shoppingCart
     * @param name
     * @param image
     * @param amount
     */
    public void addShopCart(ShoppingCart shoppingCart, String name, String image, BigDecimal amount){
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCart.setName(name);
        shoppingCart.setImage(image);
        shoppingCart.setAmount(amount);
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingcartmapper.addCart(shoppingCart);
    }



}

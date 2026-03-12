package com.sky.dish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.api.client.SetmealClient;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;

import com.sky.dish.mapper.DishFlavorMapper;
import com.sky.dish.mapper.DishMapper;

import com.sky.dish.service.DishService;
import com.sky.gateway.utils.common.constant.MessageConstant;
import com.sky.gateway.utils.common.constant.StatusConstant;
import com.sky.gateway.utils.common.exception.DeletionNotAllowedException;
import com.sky.gateway.utils.common.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Resource
    private SetmealClient setmealClient;

    @Transactional
    public void addDish(DishDTO dishDTO) {
        Dish dish = Dish.builder().build();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insertDish(dish);

        Long id = dish.getId();
        List<DishFlavor> list = dishDTO.getFlavors();

        if(list!=null && !list.isEmpty()){
            list.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.insertBatch(list);
        }
    }

    @Override
    public PageResult PageQueryDish(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> pa = dishMapper.PageQuery(dishPageQueryDTO);
        long total = pa.getTotal();
        List<DishVO> records = pa.getResult();
        return new PageResult(total, records);
    }

    @Transactional
    @Override
    public void deleteDish(List<Long> ids) {

        ids.forEach(id -> {
            //起售菜品无法删除
            Dish dishByIdAndStatus = dishMapper.findDishById(id);
            if(dishByIdAndStatus!=null&&dishByIdAndStatus.getStatus().equals(StatusConstant.ENABLE))
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);

            //与套餐绑定的菜品无法删除
            List<DishItemVO> mealDish = setmealClient.getSetmealByDishIds(id).getData();
            if(mealDish!=null && !mealDish.isEmpty()) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        });

        ids.forEach(id -> {
            dishFlavorMapper.deleteByDishId(id);
            dishMapper.DeleteById(id);
        });
    }

    @Transactional
    @Override
    public DishVO findById(Long id){
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dishMapper.findDishById(id), dishVO);
        dishVO.setFlavors(dishFlavorMapper.findByDishId(id));
        return dishVO;
    }

    @Transactional
    @Override
    public void updateDish(DishDTO dishDTO) {
        Dish dish = Dish.builder().build();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateDish(dish);

        List<DishFlavor> list = dishDTO.getFlavors();
        if(list!=null&&!list.isEmpty()) {
            dishFlavorMapper.deleteByDishId(dishDTO.getId());
            list.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(list);
        }
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.updateDishStatus(dish);
    }

    @Override
    public List<DishVO> DishWithFlavors(Dish dished) {
        List<Dish> dish = dishMapper.findDish(dished);
        List<DishVO> dishVO = new ArrayList<>();
        for (Dish dish1 : dish) {
            DishVO dishVO1 = DishVO.builder().build();
            BeanUtils.copyProperties(dish1, dishVO1);
            dishVO1.setFlavors(dishFlavorMapper.findByDishId(dish1.getId()));
            dishVO.add(dishVO1);
        }
        return dishVO;
    }

    @Override
    public List<Dish> listByStatus(Integer status) {
         return dishMapper.listByStatus(status);
    }
}

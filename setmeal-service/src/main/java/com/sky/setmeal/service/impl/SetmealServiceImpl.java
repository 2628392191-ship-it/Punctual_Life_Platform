package com.sky.setmeal.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.gateway.utils.common.result.PageResult;
import com.sky.gateway.utils.common.result.Result;
import com.sky.setmeal.mapper.SetmealDishMapper;
import com.sky.setmeal.mapper.SetmealMapper;

import com.sky.setmeal.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public List<Setmeal> findSetmealByCategoryId(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    @Override
    public List<DishItemVO> getDishBySetmealbyId(Long SetmealId){
        return setmealDishMapper.getSetmealDishBySetmealId(SetmealId);
    }



    //需要增加逻辑，套餐的价格应该等于每道菜品及份数乘积的价格之和
    @Transactional
    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = Setmeal.builder().build();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes!=null&&!setmealDishes.isEmpty()){
         for (SetmealDish setmealDish : setmealDishes) {
           setmealDish.setSetmealId(setmeal.getId());
         }
        }
        setmealDishMapper.insertBatch(setmealDishes);
    }


    @Override
    public SetmealVO getById(Long id){
        Setmeal byId = setmealMapper.getById(id);
        SetmealVO setmealVO = SetmealVO.builder().build();
        BeanUtils.copyProperties(byId, setmealVO);
        List<SetmealDish> setmealDishBySetmealId = setmealDishMapper.findSetmealDishBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishBySetmealId);
        return setmealVO;
    }

    @Transactional
    @Override
    public Result updateSetmeal(SetmealDTO setmealDTO) {
        //更新套餐
        Setmeal setmeal = Setmeal.builder().build();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        //更新关联的菜品集合
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes!=null&&!setmealDishes.isEmpty()){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDishMapper.updateDetail(setmealDish);
            }
            setmealDTO.setSetmealDishes(setmealDishMapper.findSetmealDishBySetmealId(setmealDTO.getId()));
        }
        return Result.success(setmealDTO);
    }

    @Override
    public Result startOrStop(Integer status, Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
        return Result.success();
    }

    @Override
    public List<Setmeal> listByStatus(Integer status) {
        List<Setmeal> setmeals = setmealMapper.listByStatus(status);
        return setmeals;
    }


    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> pa= setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(pa.getTotal(), pa.getResult());
    }

}

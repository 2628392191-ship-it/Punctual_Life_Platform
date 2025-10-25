package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;


@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     * 操作两张表，dish和dish_flavor
     * 使用集合插入口味表
     */
    @Transactional
    public void addDish(DishDTO dishDTO) {
         Dish dish =Dish.builder()
                 .build();
         BeanUtils.copyProperties(dishDTO, dish);
         dishMapper.insertDish(dish);

        Long id = dish.getId();
        List<DishFlavor> list=dishDTO.getFlavors();

        //只有有口味才添加
        if(list!=null && !list.isEmpty()){
        //遍历集合，给每一个数据设置它们的菜品id，这样就知道这个口味数据是哪一道菜的数据
            list.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.insertBatch(list);
        }

    }

    /**
     * 菜品分页查询
     * 需求分析：
     * 根据页码展示菜品信息
     * 每页展示10条数据
     * 分页查询时可以根据需要输入菜品名称、菜品分类、菜品状态进行查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult PageQueryDish(DishPageQueryDTO dishPageQueryDTO) {

        //设置查询是几页，每页多少条
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> pa=dishMapper.PageQuery(dishPageQueryDTO);
        long total = pa.getTotal();
        List<DishVO> records = pa.getResult();
        return new PageResult(total, records);
    }

    /** 批量删除菜品
     * 需求分析：
     * 可以一次删除一个菜品，也可以批量删除菜品
     * 起售中的菜品不能删除
     * 被套餐关联的菜品不能删除
     * 删除菜品后，关联的口味数据也需要删除掉
     */
    @Transactional
    @Override
    public void deleteDish(List<Long> ids) {
        //1.先看是否有起售的菜品，若有则无法删除
        //TODO: 判断是否有起售的菜品
        ids.forEach(id -> {
            Dish dishByIdAndStatus = dishMapper.findDishById(id);
            if(dishByIdAndStatus!=null&&dishByIdAndStatus.getStatus().equals(StatusConstant.ENABLE))
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        });
        //2.再看是否有套餐关联的菜品，若有则无法删除
        //TODO: 判断是否有套餐关联的菜品
        List<Long> mealDish = setmealDishMapper.getSetmealDishByDishId(ids);
        if(mealDish!=null && !mealDish.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //3.若前两个都没有，则删除菜品和菜品口味数据
        ids.forEach(id -> {
            dishFlavorMapper.deleteByDishId(id);
            dishMapper.DeleteById(id);
        });
    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @Transactional
    @Override
    public DishVO findById(Long id){
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dishMapper.findDishById(id), dishVO);
        dishVO.setFlavors(dishFlavorMapper.findByDishId(id));
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     * 修改菜品和菜品口味数据
     */
    @Transactional
    @Override
    public void updateDish(DishDTO dishDTO) {
        Dish dish = Dish.builder().build();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateDish(dish);
        //重新插入口味数据
        List<DishFlavor> list=dishDTO.getFlavors();
        //此处通过判断传来的数据是否为空，若为空则不插，若不为空则进行删除再重新插入
        if(list!=null&&!list.isEmpty()) {
            //删除原有的口味数据
            dishFlavorMapper.deleteByDishId(dishDTO.getId());
            list.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(list);
        }
        else return;
    }

    /**
     * 停售/起售菜品
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.updateDishStatus(dish);
    }

    /**
     * 根据分类id查询菜品
     * @param dished
     * @return
     */
    @Override
    public List<DishVO> DishWithFlavors(Dish dished) {
        List<Dish> dish = dishMapper.findDish(dished);
        List<DishVO> dishVO = new ArrayList<>();
        for (Dish dish1 : dish) {
            DishVO dishVO1 =DishVO.builder().build();
            //将dish1的数据复制给dishVO1
            BeanUtils.copyProperties(dish1, dishVO1);
            //设置DishVO里的flavors集合数据
            dishVO1.setFlavors(dishFlavorMapper.findByDishId(dish1.getId()));
            //将dishVO1添加到dishVO集合中
            dishVO.add(dishVO1);
        }
        return dishVO;
    }




}

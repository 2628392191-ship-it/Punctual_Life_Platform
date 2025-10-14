package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController("UserDishController")
@Api(tags = "用户菜品接口")
@RequestMapping("/user/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("分类id：{}", categoryId);
        //将菜品信息保存在redis中，缓存数据；若有需要则从redis中拿
        String key="dish_"+categoryId;
        List<DishVO> list=(List<DishVO>)redisTemplate.opsForValue().get(key);
        if(list!=null&&!list.isEmpty()){
            log.info("从redis中获取数据");
            return Result.success(list);}


        //通过分类的id查询菜品数
        //若缓存中没有，则从mysqlsh数据库中查询数据放到redis中,且把数据库查询到的返回给前端
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        list = dishService.DishWithFlavors(dish);
        redisTemplate.opsForValue().set(key,list);
        return Result.success(list);
    }



}

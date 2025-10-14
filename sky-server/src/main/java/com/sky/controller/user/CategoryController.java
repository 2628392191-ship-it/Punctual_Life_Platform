package com.sky.controller.user;


import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags="用户分类接口")
@Slf4j
@RestController("UserCategoryController")
@RequestMapping("/user/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation("查询分类")
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        log.info("查询分类");
        if(type == null) log.warn("type为空");
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

}

package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类管理")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    @ApiOperation("新增分类")
    @PostMapping
    public Result add(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     *
     * @param categoryDTO
     * @return
     */
    @ApiOperation("分类分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryDTO) {
        log.info("分页查询：{}", categoryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用/禁用分类
     *
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启用/禁用分类")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用或禁用分类：{}", id);
        categoryService.updateCategoryStatus(status, id);
        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     * @return
     */
   @ApiOperation("修改分类信息")
   @PutMapping
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类信息：{}", categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 删除分类
     *
     * @param id
     * @return
     */
    @ApiOperation("删除分类")
    @DeleteMapping
    public Result deleteCategory(Long id) {
        log.info("删除分类：{}", id);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @ApiOperation("通过分类类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type) {
        log.info("查询分类：{}", type);
        if(type == null) {
            log.warn("警告：type参数为空，将查询所有分类或返回空结果");
            // 根据业务逻辑决定如何处理 null 参数
        }
        List<Category> list = categoryService.list(type);
        log.info("查询到 {} 条分类数据", list.size());
        return Result.success(list);
    }

}

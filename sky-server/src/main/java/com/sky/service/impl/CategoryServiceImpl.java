package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category build = Category.builder()
                .status(1)
                .build();
        BeanUtils.copyProperties(categoryDTO, build);
        categoryMapper.insert(build);
    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> pa = categoryMapper.pageQuery(categoryPageQueryDTO);
        long total = pa.getTotal();
        List<Category> records = pa.getResult();
        return new PageResult(total, records);
    }

    /**
     * 修改分类状态
     * @param status
     * @param id
     */
    @Override
    public void updateCategoryStatus(Integer status, Long id) {
         Category build = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.updateCategory(build);
    }
    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void updateCategory(CategoryDTO categoryDTO){
        Category build = Category.builder()
                .id(categoryDTO.getId())
                .type(categoryDTO.getType())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .build();
        categoryMapper.updateCategory(build);
    }

    /**
     * 删除分类
     * @param id
     */
    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

     /**
     * 查询分类
     * @return
     */
   @Override
    public List< Category> list(Integer type){
       return categoryMapper.list(type);
   }


}

package com.sky.category.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.gateway.utils.common.result.PageResult;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.category.mapper.CategoryMapper;

import com.sky.category.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category build = Category.builder()
                .status(1)
                .build();
        BeanUtils.copyProperties(categoryDTO, build);
        categoryMapper.insert(build);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> pa = categoryMapper.pageQuery(categoryPageQueryDTO);
        long total = pa.getTotal();
        List<Category> records = pa.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void updateCategoryStatus(Integer status, Long id) {
        Category build = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.updateCategory(build);
    }

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

    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public List<Category> list(Integer type){
        return categoryMapper.list();
    }
}

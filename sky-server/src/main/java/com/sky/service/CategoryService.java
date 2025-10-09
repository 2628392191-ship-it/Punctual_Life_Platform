package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;

public interface CategoryService {

    void addCategory(CategoryDTO categoryDTO);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void updateCategoryStatus(Integer status, Long id);

    void updateCategory(CategoryDTO categoryDTO);

    void deleteCategory(Long id);
}

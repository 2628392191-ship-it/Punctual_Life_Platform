import request from '@/utils/request'

// 分页查询分类
export const getCategoryPage = (params) => {
  return request({
    url: '/admin/category/page',
    method: 'get',
    params
  })
}

// 新增分类
export const addCategory = (data) => {
  return request({
    url: '/admin/category',
    method: 'post',
    data
  })
}

// 修改分类
export const updateCategory = (data) => {
  return request({
    url: '/admin/category',
    method: 'put',
    data
  })
}

// 删除分类
export const deleteCategory = (id) => {
  return request({
    url: '/admin/category',
    method: 'delete',
    params: { id }
  })
}

// 启用禁用分类
export const enableOrDisableCategory = (status, id) => {
  return request({
    url: `/admin/category/status/${status}`,
    method: 'post',
    params: { id }
  })
}

// 根据类型查询分类列表 (1 菜品分类, 2 套餐分类)
export const getCategoryList = (type) => {
  return request({
    url: '/admin/category/list',
    method: 'get',
    params: { type }
  })
}

import request from '@/utils/request'

// 分页查询菜品
export const getDishPage = (params) => {
  return request({
    url: '/admin/dish/page',
    method: 'get',
    params
  })
}

// 新增菜品
export const addDish = (data) => {
  return request({
    url: '/admin/dish',
    method: 'post',
    data
  })
}

// 修改菜品
export const updateDish = (data) => {
  return request({
    url: '/admin/dish',
    method: 'put',
    data
  })
}

// 批量删除菜品
export const deleteDish = (ids) => {
  return request({
    url: '/admin/dish',
    method: 'delete',
    params: { ids } // axios 会处理为 ids=1,2,3
  })
}

// 启用禁用菜品
export const enableOrDisableDish = (status, id) => {
  return request({
    url: `/admin/dish/status/${status}`,
    method: 'post',
    params: { id }
  })
}

// 根据ID查询菜品
export const getDishById = (id) => {
  return request({
    url: `/admin/dish/${id}`,
    method: 'get'
  })
}

// 根据分类ID查询菜品列表 (这里使用后端提供的 admin 接口或 user 接口，视情况而定)
// 当前 admin 接口只提供 list-by-status，我们可能需要根据分类展示，暂时预留
export const getDishList = (params) => {
  return request({
    url: '/admin/dish/list-by-status',
    method: 'get',
    params
  })
}

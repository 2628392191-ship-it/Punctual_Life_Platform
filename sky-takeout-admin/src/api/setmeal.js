import request from '@/utils/request'

// 分页查询套餐
export const getSetmealPage = (params) => {
  return request({
    url: '/admin/setmeal/page',
    method: 'get',
    params
  })
}

// 新增套餐
export const addSetmeal = (data) => {
  return request({
    url: '/admin/setmeal',
    method: 'post',
    data
  })
}

// 修改套餐
export const updateSetmeal = (data) => {
  return request({
    url: '/admin/setmeal',
    method: 'put',
    data
  })
}

// 根据ID查询套餐
export const getSetmealById = (id) => {
  return request({
    url: `/admin/setmeal/${id}`,
    method: 'get'
  })
}

// 删除套餐 (后端目前只支持单个 ID)
export const deleteSetmeal = (id) => {
  return request({
    url: '/admin/setmeal',
    method: 'delete',
    params: { id }
  })
}

// 启用禁用套餐
export const enableOrDisableSetmeal = (status, id) => {
  return request({
    url: `/admin/setmeal/status/${status}`,
    method: 'post',
    params: { id }
  })
}

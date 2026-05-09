import request from '@/utils/request'

// 获取今日运营数据
export const getBusinessData = () => {
  return request({
    url: '/admin/workspace/businessData',
    method: 'get'
  })
}

// 获取订单统计数据
export const getOrderOverview = () => {
  return request({
    url: '/admin/workspace/overviewOrders',
    method: 'get'
  })
}

// 获取菜品总览
export const getDishOverview = () => {
  return request({
    url: '/admin/workspace/overviewDishes',
    method: 'get'
  })
}

// 获取套餐总览
export const getSetmealOverview = () => {
  return request({
    url: '/admin/workspace/overviewSetmeals',
    method: 'get'
  })
}

// 报表接口
export const getTurnoverStatistics = (params) => {
  return request({
    url: '/admin/report/turnoverStatistics',
    method: 'get',
    params
  })
}

export const getUserStatistics = (params) => {
  return request({
    url: '/admin/report/userStatistics',
    method: 'get',
    params
  })
}

export const getOrdersStatistics = (params) => {
  return request({
    url: '/admin/report/ordersStatistics',
    method: 'get',
    params
  })
}

export const getTop10Statistics = (params) => {
  return request({
    url: '/admin/report/top10',
    method: 'get',
    params
  })
}

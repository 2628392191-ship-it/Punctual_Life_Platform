import request from '@/utils/request'

// 条件搜索订单
export const getOrderPage = (params) => {
  return request({
    url: '/admin/order/conditionSearch',
    method: 'get',
    params
  })
}

// 查询订单详情
export const getOrderDetail = (id) => {
  return request({
    url: `/admin/order/details/${id}`,
    method: 'get'
  })
}

// 接单
export const confirmOrder = (data) => {
  return request({
    url: '/admin/order/confirm',
    method: 'put',
    data
  })
}

// 拒单
export const rejectOrder = (data) => {
  return request({
    url: '/admin/order/rejection',
    method: 'put',
    data
  })
}

// 取消订单
export const cancelOrder = (data) => {
  return request({
    url: '/admin/order/cancel',
    method: 'put',
    data
  })
}

// 派送订单
export const deliveryOrder = (id) => {
  return request({
    url: `/admin/order/delivery/${id}`,
    method: 'put'
  })
}

// 完成订单
export const completeOrder = (id) => {
  return request({
    url: `/admin/order/complete/${id}`,
    method: 'put'
  })
}

// 各个状态的订单数量统计
export const getOrderStatistics = () => {
  return request({
    url: '/admin/order/statistics',
    method: 'get'
  })
}

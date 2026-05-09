import request from '@/utils/request'

// 获取店铺营业状态
export const getShopStatus = () => {
  return request({
    url: '/admin/shop/status',
    method: 'get'
  })
}

// 设置店铺营业状态
export const setShopStatus = (status) => {
  return request({
    url: `/admin/shop/${status}`,
    method: 'put'
  })
}

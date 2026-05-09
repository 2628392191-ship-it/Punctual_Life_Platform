import request from '@/utils/request'

// 图片上传
export const uploadFile = (data) => {
  return request({
    url: '/admin/common/upload',
    method: 'post',
    data
  })
}

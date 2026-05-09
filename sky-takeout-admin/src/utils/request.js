import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: '/', // vite 中配置了代理
  timeout: 10000
})

// Request interceptor
service.interceptors.request.use(
  config => {
    // 每次发送请求之前添加 adminToken
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['adminToken'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    // 如果返回 code 为 1，表示成功
    if (res.code === 1) {
      return res.data
    } else {
      // 业务错误
      ElMessage({
        message: res.msg || 'Error',
        type: 'error',
        duration: 5 * 1000
      })

      // 特殊情况处理（如：未登录 / 登录已过期可能不会返回 1）
      // 这个根据后端实际返回的状态码或者 code 来判断，这里做个简单示例
      if (res.code === 0 && res.msg === 'NOTLOGIN') {
         localStorage.removeItem('token')
         router.push('/login')
      }

      return Promise.reject(new Error(res.msg || 'Error'))
    }
  },
  error => {
    // Http 错误处理
    console.log('err' + error)
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    ElMessage({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service

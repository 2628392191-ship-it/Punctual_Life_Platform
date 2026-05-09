import request from '@/utils/request'

// 登录
export const login = (data) => {
  return request({
    url: '/admin/employee/login',
    method: 'post',
    data
  })
}

// 退出
export const logout = () => {
  return request({
    url: '/admin/employee/logout',
    method: 'post'
  })
}

// 分页查询员工
export const getEmployeePage = (params) => {
  return request({
    url: '/admin/employee/page',
    method: 'get',
    params
  })
}

// 新增员工
export const addEmployee = (data) => {
  return request({
    url: '/admin/employee',
    method: 'post',
    data
  })
}

// 修改员工
export const updateEmployee = (data) => {
  return request({
    url: '/admin/employee',
    method: 'put',
    data
  })
}

// 启用禁用员工
export const enableOrDisableEmployee = (status, id) => {
  return request({
    url: `/admin/employee/status/${status}`,
    method: 'post',
    params: { id }
  })
}

// 根据ID查询员工
export const getEmployeeById = (id) => {
  return request({
    url: `/admin/employee/${id}`,
    method: 'get'
  })
}

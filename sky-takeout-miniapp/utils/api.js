// API 请求封装
const app = getApp()

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = app.globalData.token || wx.getStorageSync('userToken')
    const header = {
      'Content-Type': 'application/json'
    }
    if (token) {
      header['userToken'] = token
    }

    wx.request({
      url: app.globalData.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      success(res) {
        if (res.statusCode === 401) {
          wx.removeStorageSync('userToken')
          wx.removeStorageSync('userInfo')
          app.globalData.token = null
          app.globalData.userInfo = null
          // 避免重复跳转：仅在非登录页时才跳转
          const pages = getCurrentPages()
          const currentPage = pages[pages.length - 1]
          if (!currentPage || currentPage.route !== 'pages/login/login') {
            wx.navigateTo({ url: '/pages/login/login' })
          }
          reject(new Error('未登录'))
          return
        }
        if (res.statusCode >= 200 && res.statusCode < 300) {
          const result = res.data
          if (result.code === 1) {
            resolve(result.data)
          } else {
            wx.showToast({ title: result.msg || '请求失败', icon: 'none', duration: 2000 })
            reject(new Error(result.msg || '请求失败'))
          }
        } else {
          wx.showToast({ title: '服务器繁忙(' + res.statusCode + ')', icon: 'none' })
          reject(new Error('HTTP ' + res.statusCode))
        }
      },
      fail(err) {
        wx.showToast({ title: '网络连接失败，请检查后端服务', icon: 'none', duration: 2000 })
        reject(err)
      }
    })
  })
}

const get = (url, data) => request({ url, method: 'GET', data })
const post = (url, data) => request({ url, method: 'POST', data })
const put = (url, data) => request({ url, method: 'PUT', data })
const del = (url, data) => request({ url, method: 'DELETE', data })

module.exports = { request, get, post, put, del }

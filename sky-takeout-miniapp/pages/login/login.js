// pages/login/login.js
const { post } = require('../../utils/api')
const app = getApp()

Page({
  data: {
    loading: false,
    loginFailed: false,
    errorMsg: ''
  },

  handleLogin() {
    this.setData({ loading: true, loginFailed: false, errorMsg: '' })

    wx.login({
      success: async (res) => {
        if (!res.code) {
          this.setData({ loading: false, loginFailed: true, errorMsg: '获取微信登录凭证失败' })
          return
        }
        console.log('微信登录code:', res.code)
        try {
          const data = await post('/user/user/login', { code: res.code })
          const userInfo = { id: data.id, openid: data.openid }
          wx.setStorageSync('userToken', data.token)
          wx.setStorageSync('userInfo', userInfo)
          app.globalData.token = data.token
          app.globalData.userInfo = userInfo

          wx.showToast({ title: '登录成功', icon: 'success' })
          setTimeout(() => {
            wx.reLaunch({ url: '/pages/index/index' })
          }, 600)
        } catch (e) {
          console.error('登录请求失败:', e)
          this.setData({
            loading: false,
            loginFailed: true,
            errorMsg: e.message || '登录失败，请确认后端服务已启动'
          })
        }
      },
      fail: (err) => {
        console.error('wx.login失败:', err)
        this.setData({
          loading: false,
          loginFailed: true,
          errorMsg: '微信登录失败，请在微信开发者工具中测试'
        })
      }
    })
  }
})

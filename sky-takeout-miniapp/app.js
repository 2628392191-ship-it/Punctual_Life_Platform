// 准时达生活平台 - 小程序入口
App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://localhost:8080' // 网关地址，正式上线需替换
  },

  onLaunch() {
    const token = wx.getStorageSync('userToken')
    const userInfo = wx.getStorageSync('userInfo')
    if (token) {
      this.globalData.token = token
      this.globalData.userInfo = userInfo
    }
  },

  checkLogin() {
    if (!this.globalData.token) {
      const pages = getCurrentPages()
      const currentPage = pages[pages.length - 1]
      // 避免在登录页重复跳转
      if (currentPage && currentPage.route === 'pages/login/login') {
        return false
      }
      wx.navigateTo({ url: '/pages/login/login' })
      return false
    }
    return true
  },

  logout() {
    this.globalData.token = null
    this.globalData.userInfo = null
    wx.removeStorageSync('userToken')
    wx.removeStorageSync('userInfo')
    wx.reLaunch({ url: '/pages/index/index' })
  }
})

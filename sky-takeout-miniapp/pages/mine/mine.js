// pages/mine/mine.js
const { get } = require('../../utils/api')
const app = getApp()

Page({
  data: {
    userInfo: {},
    shopOpen: true
  },

  onShow() {
    if (!app.globalData.token) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    this.setData({
      userInfo: app.globalData.userInfo || wx.getStorageSync('userInfo') || {}
    })
    this.fetchShopStatus()
  },

  async fetchShopStatus() {
    try {
      const status = await get('/user/shop/status')
      this.setData({ shopOpen: status === 1 })
    } catch (e) {}
  },

  goAddress() {
    wx.navigateTo({ url: '/pages/address/address' })
  },

  goToOrders() {
    wx.switchTab({ url: '/pages/order/order' })
  },

  goShopStatus() {
    const msg = this.data.shopOpen ? '当前店铺营业中' : '当前店铺已打烊'
    wx.showToast({ title: msg, icon: 'none' })
  },

  handleLogout() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout()
        }
      }
    })
  }
})

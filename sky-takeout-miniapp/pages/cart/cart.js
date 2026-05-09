// pages/cart/cart.js
const { get, post } = require('../../utils/api')
const app = getApp()

Page({
  data: {
    cartItems: [],
    totalPrice: 0,
    allChecked: true
  },

  onShow() {
    if (!app.globalData.token) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    this.fetchCart()
  },

  async fetchCart() {
    try {
      const items = await get('/user/shoppingCart/list')
      const cartItems = (items || []).map(item => ({
        ...item,
        lineTotal: ((item.amount || 0) * (item.number || 1)).toFixed(2)
      }))
      const totalPrice = cartItems.reduce((sum, i) => sum + parseFloat(i.lineTotal), 0).toFixed(2)
      this.setData({ cartItems, totalPrice })
    } catch (e) {
      console.error('获取购物车失败', e)
    }
  },

  async addItem(e) {
    const item = e.currentTarget.dataset.item
    try {
      await post('/user/shoppingCart/add', {
        dishId: item.dishId,
        setmealId: item.setmealId,
        dishFlavor: item.dishFlavor || ''
      })
      this.fetchCart()
    } catch (e) {}
  },

  async subItem(e) {
    const item = e.currentTarget.dataset.item
    try {
      await post('/user/shoppingCart/sub', {
        dishId: item.dishId,
        setmealId: item.setmealId,
        dishFlavor: item.dishFlavor || ''
      })
      this.fetchCart()
    } catch (e) {}
  },

  async clearCart() {
    try {
      const { del } = require('../../utils/api')
      await del('/user/shoppingCart/clean')
      this.setData({ cartItems: [], totalPrice: 0 })
      wx.showToast({ title: '已清空', icon: 'success' })
    } catch (e) {
      wx.showToast({ title: '清空失败', icon: 'none' })
    }
  },

  goCheckout() {
    wx.navigateTo({ url: '/pages/address/address?from=cart' })
  }
})

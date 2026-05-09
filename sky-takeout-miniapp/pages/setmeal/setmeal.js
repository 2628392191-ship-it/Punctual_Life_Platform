// pages/setmeal/setmeal.js
const { get, post } = require('../../utils/api')
const app = getApp()

Page({
  data: {
    setmeal: {},
    dishItems: []
  },

  onLoad(options) {
    if (!options.id) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      return
    }
    this.fetchDetail(options.id)
  },

  async fetchDetail(id) {
    try {
      const data = await get('/user/setmeal/detail/' + id)
      if (data) {
        this.setData({
          setmeal: data.setmeal || {},
          dishItems: data.dishItems || []
        })
      }
    } catch (e) {
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  async addToCart() {
    if (!app.checkLogin()) return
    try {
      await post('/user/shoppingCart/add', {
        dishId: null,
        setmealId: this.data.setmeal.id,
        dishFlavor: ''
      })
      wx.showToast({ title: '已加入购物车', icon: 'success' })
    } catch (e) {
      wx.showToast({ title: '添加失败', icon: 'none' })
    }
  }
})

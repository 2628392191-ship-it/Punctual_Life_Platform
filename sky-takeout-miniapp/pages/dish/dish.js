// pages/dish/dish.js
const { get, post } = require('../../utils/api')
const app = getApp()

Page({
  data: {
    dish: {},
    selectedFlavor: '',
    flavors: []
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
      const dish = await get('/user/dish/detail/' + id)
      if (dish) {
        this.setData({
          dish,
          flavors: dish.flavors || []
        })
      }
    } catch (e) {
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  selectFlavor(e) {
    this.setData({ selectedFlavor: e.currentTarget.dataset.name })
  },

  async addToCart() {
    if (!app.checkLogin()) return
    try {
      await post('/user/shoppingCart/add', {
        dishId: this.data.dish.id,
        setmealId: null,
        dishFlavor: this.data.selectedFlavor
      })
      wx.showToast({ title: '已加入购物车', icon: 'success' })
    } catch (e) {
      wx.showToast({ title: '添加失败', icon: 'none' })
    }
  }
})

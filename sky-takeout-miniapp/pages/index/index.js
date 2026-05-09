// pages/index/index.js
const { get, post } = require('../../utils/api')
const app = getApp()

Page({
  data: {
    shopOpen: true,
    categories: [],
    currentCategory: 0,
    goodsList: []
  },

  onLoad() {
    this.fetchShopStatus()
    this.fetchCategories()
    this.fetchGoods()
  },

  onShow() {
    if (!app.globalData.token) {
      wx.navigateTo({ url: '/pages/login/login' })
    }
  },

  onPullDownRefresh() {
    this.fetchGoods(this.data.currentCategory)
    wx.stopPullDownRefresh()
  },

  async fetchShopStatus() {
    try {
      const status = await get('/user/shop/status')
      this.setData({ shopOpen: status === 1 })
    } catch (e) {
      console.error('获取店铺状态失败', e)
    }
  },

  async fetchCategories() {
    try {
      const list = await get('/user/category/list', { type: 1 })
      const iconMap = { '热销': '🔥', '主食': '🍚', '饮品': '🥤', '小吃': '🍟', '甜品': '🍰', '汤品': '🍲', '套餐': '📦' }
      const categories = (list || []).map(c => ({
        ...c,
        icon: iconMap[c.name] || '🍽️'
      }))
      this.setData({ categories })
    } catch (e) {
      console.error('获取分类失败', e)
    }
  },

  async fetchGoods(categoryId) {
    try {
      let dishes = []
      if (categoryId && categoryId !== 0) {
        dishes = await get('/user/dish/list', { categoryId })
      } else {
        dishes = await get('/user/dish/list', {})
      }
      dishes = (dishes || []).map(d => ({ ...d, key: 'dish_' + d.id, type: 'dish' }))

      let setmeals = []
      try {
        setmeals = await get('/user/setmeal/list', {})
        setmeals = (setmeals || []).map(s => ({ ...s, key: 'setmeal_' + s.id, type: 'setmeal', name: s.name + '（套餐）' }))
      } catch (e) {}

      let goods = [...dishes, ...setmeals]
      if (categoryId && categoryId !== 0) {
        goods = goods.filter(g => g.categoryId === categoryId)
      }
      this.setData({ goodsList: goods })
    } catch (e) {
      console.error('获取菜品失败', e)
    }
  },

  switchCategory(e) {
    const id = e.currentTarget.dataset.id
    this.setData({ currentCategory: id })
    this.fetchGoods(id)
  },

  goDetail(e) {
    const item = e.currentTarget.dataset.item
    if (item.type === 'dish') {
      wx.navigateTo({ url: '/pages/dish/dish?id=' + item.id })
    } else if (item.type === 'setmeal') {
      wx.navigateTo({ url: '/pages/setmeal/setmeal?id=' + item.id })
    }
  },

  async addToCart(e) {
    if (!app.checkLogin()) return
    const item = e.currentTarget.dataset.item
    try {
      await post('/user/shoppingCart/add', {
        dishId: item.type === 'dish' ? item.id : null,
        setmealId: item.type === 'setmeal' ? item.id : null,
        dishFlavor: ''
      })
      wx.showToast({ title: '已加入购物车', icon: 'success', duration: 1200 })
    } catch (e) {
      wx.showToast({ title: '添加失败', icon: 'none' })
    }
  }
})

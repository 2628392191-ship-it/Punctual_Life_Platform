// custom-tab-bar/index.js
Component({
  data: {
    selected: 0,
    list: [
      { pagePath: '/pages/index/index', text: '首页', icon: '🏠' },
      { pagePath: '/pages/order/order', text: '订单', icon: '📋' },
      { pagePath: '/pages/cart/cart', text: '购物车', icon: '🛒' },
      { pagePath: '/pages/mine/mine', text: '我的', icon: '👤' },
    ]
  },

  methods: {
    switchTab(e) {
      const data = e.currentTarget.dataset
      const url = data.path
      wx.switchTab({ url })
    }
  }
})

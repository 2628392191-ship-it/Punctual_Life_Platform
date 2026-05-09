// pages/order/order.js
const { get, put } = require('../../utils/api')
const { ORDER_STATUS } = require('../../utils/constants')
const app = getApp()

Page({
  data: {
    tabs: [
      { label: '待支付', value: '1' },
      { label: '待接单', value: '2' },
      { label: '已接单', value: '3' },
      { label: '派送中', value: '4' },
      { label: '已完成', value: '5' },
    ],
    currentTab: '',
    orders: [],
    detailOrder: null,
    showDetail: false
  },

  onShow() {
    if (!app.globalData.token) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    this.fetchOrders()
  },

  onLoad(options) {
    if (options.detail) {
      this.fetchOrderDetail(options.detail)
    }
  },

  async fetchOrders() {
    try {
      const params = { page: 1, pageSize: 50 }
      const status = this.data.currentTab
      if (status !== '' && status != null) {
        params.status = Number(status)
      }
      const data = await get('/user/order/historyOrders', params)
      const rawOrders = data.records || data || []
      const orders = rawOrders.map(order => ({
        ...order,
        _statusLabel: ORDER_STATUS[order.status] || '未知',
        _statusColor: ({ 1:'#fa8c16', 2:'#1677ff', 3:'#1677ff', 4:'#52c41a', 5:'#52c41a', 6:'#999' })[order.status] || '#999'
      }))
      this.setData({ orders })
    } catch (e) {
      console.error('获取订单失败', e)
    }
  },

  async fetchOrderDetail(id) {
    try {
      const order = await get('/user/order/orderDetail/' + id)
      if (order) {
        order._statusLabel = ORDER_STATUS[order.status] || '未知'
        order._statusColor = ({ 1:'#fa8c16', 2:'#1677ff', 3:'#1677ff', 4:'#52c41a', 5:'#52c41a', 6:'#999' })[order.status] || '#999'
        this.setData({ detailOrder: order, showDetail: true })
      }
    } catch (e) {
      wx.showToast({ title: '获取订单详情失败', icon: 'none' })
    }
  },

  closeDetail() {
    this.setData({ showDetail: false, detailOrder: null })
  },

  switchTab(e) {
    this.setData({ currentTab: e.currentTarget.dataset.status })
    this.fetchOrders()
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    this.fetchOrderDetail(id)
  },

  async goPay(e) {
    const id = e.currentTarget.dataset.id
    const order = this.data.orders.find(o => o.id == id) || this.data.detailOrder
    const orderNumber = (order && (order.number || order.orderNumber)) || String(id)
    try {
      wx.showLoading({ title: '支付中...' })
      await put('/user/order/payment', { orderNumber: orderNumber, payMethod: 1 })
      wx.hideLoading()
      wx.showToast({ title: '支付成功', icon: 'success' })
      this.fetchOrders()
      if (this.data.showDetail) {
        this.fetchOrderDetail(id)
      }
    } catch (e) {
      wx.hideLoading()
      wx.showToast({ title: '支付失败', icon: 'none' })
    }
  },

  cancelOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '取消订单',
      content: '确定要取消该订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await put('/user/order/cancel/' + id)
            wx.showToast({ title: '已取消', icon: 'success' })
            this.fetchOrders()
          } catch (e) {
            wx.showToast({ title: '取消失败', icon: 'none' })
          }
        }
      }
    })
  }
})

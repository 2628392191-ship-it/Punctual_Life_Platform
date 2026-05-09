// pages/payment/payment.js
const { put } = require('../../utils/api')
const app = getApp()

Page({
  data: {
    orderId: null,
    orderNumber: '',
    amount: '0.00',
    methods: [
      { value: 'wechat', name: '微信支付', icon: '💳' },
      { value: 'alipay', name: '支付宝', icon: '📱' }
    ],
    selectedMethod: 'wechat',
    countdown: '15:00',
    paying: false
  },
  _timer: null,

  onLoad(options) {
    if (!options.orderId) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      return
    }
    this.setData({
      orderId: options.orderId,
      orderNumber: options.orderNumber || options.orderId,
      amount: options.amount || '0.00'
    })
    this.startCountdown()
  },

  onUnload() {
    if (this._timer) clearInterval(this._timer)
  },

  startCountdown() {
    let seconds = 15 * 60
    this._timer = setInterval(() => {
      seconds--
      if (seconds <= 0) {
        clearInterval(this._timer)
        wx.showToast({ title: '订单已超时', icon: 'none' })
        setTimeout(() => wx.navigateBack(), 1500)
        return
      }
      const m = Math.floor(seconds / 60)
      const s = seconds % 60
      this.setData({
        countdown: m + ':' + (s < 10 ? '0' + s : s)
      })
    }, 1000)
  },

  selectMethod(e) {
    this.setData({ selectedMethod: e.currentTarget.dataset.value })
  },

  async doPay() {
    this.setData({ paying: true })
    try {
      await put('/user/order/payment', {
        orderNumber: this.data.orderNumber,
        payMethod: this.data.selectedMethod === 'wechat' ? 1 : 2
      })
      if (this._timer) clearInterval(this._timer)
      wx.showToast({ title: '支付成功', icon: 'success' })
      setTimeout(() => {
        wx.switchTab({ url: '/pages/order/order' })
      }, 1200)
    } catch (e) {
      wx.showToast({ title: '支付失败，请重试', icon: 'none' })
    } finally {
      this.setData({ paying: false })
    }
  }
})

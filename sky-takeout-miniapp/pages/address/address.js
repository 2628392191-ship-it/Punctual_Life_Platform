// pages/address/address.js
const { get, post } = require('../../utils/api')
const app = getApp()

Page({
  data: {
    fromCart: false,
    addresses: [],
    selectedAddress: null,
    cartItems: [],
    totalPrice: 0,
    remark: '',
    submitting: false
  },

  onLoad(options) {
    if (options.from === 'cart') {
      this.setData({ fromCart: true })
      this.fetchCartItems()
    }
    this.fetchAddresses()
  },

  async fetchCartItems() {
    try {
      const items = await get('/user/shoppingCart/list')
      const cartItems = (items || []).map(i => ({
        ...i,
        lineTotal: ((i.amount || 0) * (i.number || 1)).toFixed(2)
      }))
      const totalPrice = cartItems.reduce((sum, i) => sum + parseFloat(i.lineTotal), 0).toFixed(2)
      this.setData({ cartItems, totalPrice })
    } catch (e) {}
  },

  async fetchAddresses() {
    try {
      const list = await get('/user/addressBook/list')
      const addresses = list || []
      const defaultAddr = addresses.find(a => a.isDefault == 1)
      this.setData({
        addresses,
        selectedAddress: defaultAddr || (addresses.length > 0 ? addresses[0] : null)
      })
    } catch (e) {}
  },

  selectAddress(e) {
    this.setData({ selectedAddress: e.currentTarget.dataset.item })
  },

  chooseWxAddress() {
    wx.chooseAddress({
      success: async (res) => {
        try {
          await post('/user/addressBook', {
            consignee: res.userName,
            phone: res.telNumber,
            provinceName: res.provinceName,
            cityName: res.cityName,
            districtName: res.countyName,
            detail: res.detailInfo,
            isDefault: 0
          })
          this.fetchAddresses()
        } catch (e) {
          wx.showToast({ title: '添加地址失败', icon: 'none' })
        }
      },
      fail: () => {}
    })
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  async submitOrder() {
    if (!this.data.selectedAddress) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' })
      return
    }
    this.setData({ submitting: true })
    try {
      // 1. 提交订单
      const orderResult = await post('/user/order/submit', {
        addressBookId: this.data.selectedAddress.id,
        payMethod: 1,
        remark: this.data.remark,
        amount: parseFloat(this.data.totalPrice),
        estimatedDeliveryTime: null,
        deliveryStatus: 1,
        tablewareNumber: 1,
        tablewareStatus: 1,
        packAmount: 0
      })
      // 2. 获取订单ID
      const orderId = orderResult.id || orderResult.orderId
      const orderNumber = orderResult.orderNumber || orderId
      // 3. 清空购物车
      try {
        const { del } = require('../../utils/api')
        await del('/user/shoppingCart/clean')
      } catch (e) {}
      // 4. 跳转支付页面
      if (orderId) {
        wx.redirectTo({
          url: '/pages/payment/payment?orderId=' + orderId +
            '&orderNumber=' + orderNumber +
            '&amount=' + this.data.totalPrice
        })
      } else {
        wx.showToast({ title: '下单成功', icon: 'success' })
        setTimeout(() => wx.switchTab({ url: '/pages/order/order' }), 1000)
      }
    } catch (e) {
      wx.showToast({ title: '下单失败', icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  }
})

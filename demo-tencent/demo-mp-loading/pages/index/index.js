// index.js
Page({
  handleClickShowLoading() {
    wx.showLoading({
      title: '稍等。。。',
      mask: true,
    })

    setTimeout(() => {
      wx.hideLoading()
    }, 3000)
  }
})
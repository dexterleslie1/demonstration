// index.js
Page({
  handleClickShowToastSuccess() {
    wx.showToast({
      title: '标题',
      icon: 'success'
    })
  },
  handleClickShowToastError() {
    wx.showToast({
      title: '标题',
      icon: 'error'
    })
  },
  handleClickShowToastLoading() {
    wx.showToast({
      title: '标题',
      icon: 'loading'
    })
  },
  handleClickShowToastLoadingDuration5000ms() {
    wx.showToast({
      title: '标题',
      icon: 'loading',
      duration: 5000
    })
  }
})

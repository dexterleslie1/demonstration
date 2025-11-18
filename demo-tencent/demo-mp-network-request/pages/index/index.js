// index.js
Page({
  handleClickGet() {
    wx.request({
      url: 'http://127.0.0.1:8080/api/v1/get',
      data: {
        param1: 'p1',
      },
      header: {
        header1: 'h1'
      },
      method: 'GET',
      success: (res) => {
        console.log(res)
      },
      fail: (error) => {
        console.error(error)
      },
      complete: () => {

      }
    })
  }
})
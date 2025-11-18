// index.js
Page({
  handleClick() {
    wx.login({
      success: (res) => {
        let code = res.code
        wx.request({
          url: 'http://localhost:8080/api/v1/jscode2session',
          data: {
            code: code,
          },
          method: 'GET',
          success: (res) => {
            wx.showModal({
              title: '提示',
              content: `登录成功，服务器响应：${JSON.stringify(res.data)}`,
              showCancel: false,
            })
          },
          fail: (error) => {
            wx.showModal({
              title: '提示',
              content: `登录失败，原因：${error}`,
              showCancel: false,
            })
          }
        })
      },
    })
  }
})
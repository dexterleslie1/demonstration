// index.js
Page({
  data: {
    myNickname: "",
    myAvatarUrl: "",
  },
  handleGetUserProfile() {
    wx.getUserProfile({
      desc: '显示昵称',
      success: res => {
        // console.log(res);
        this.setData({
          myNickname: res.userInfo.nickName,
          myAvatarUrl: res.userInfo.avatarUrl,
        })
      }
    })
  }
})
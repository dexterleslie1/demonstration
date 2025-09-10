import React, { Component } from 'react'

export default class Login extends Component {
  state = { userId: localStorage.getItem("userId"), merchantId: localStorage.getItem("merchantId") }

  render() {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center'
      }}>
        <div class='form-container'>
          <div class='form-item'>
            用户ID：<input type="text" value={this.state.userId} onChange={(e) => {
              this.setState({ userId: e.target.value })
            }} />
          </div>
          <div class='form-item'>
            商家ID：<input type="text" value={this.state.merchantId} onChange={(e) => {
              this.setState({ merchantId: e.target.value })
            }} />
          </div>
          <div class="form-operation-panel" style={{
            marginTop: '5px',
            display: 'flex',
            justifyContent: 'flex-end'
          }}>
            <button onClick={(e) => {
              localStorage.setItem("userId", this.state.userId)
              localStorage.setItem("merchantId", this.state.merchantId)
              
              // 跳转到 productList 页面
              this.props.history.push(`/productList`)
            }}>确定</button>
          </div>
        </div>
      </div>
    )
  }
}

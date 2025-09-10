import React, { Component } from 'react'
import axios from '../..//api/axios.js'
import { Modal, message, Spin } from 'antd';

export default class CreateProduct extends Component {

  state = {
    isFlashSale: false,
    name: "",
    stockAmount: -1,
    flashSaleStartTime: "",
    flashSaleEndTime: "",
    loading: false,
    loadingText: ""
  }

  render() {
    return (
      <div style={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
      }}>
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'flex-start',
        }}>
          <div style={{
            color: 'yellowgreen'
          }}>提示：不填写商品信息新增会自动随机生成商品信息</div>
          <div>商品名称：<input type="text" value={this.state.name} onChange={(e) => {
            this.setState({ name: e.target.value })
          }} /></div>
          <div>库存数量：<input type="text" value={this.state.stockAmount} onChange={(e) => {
            this.setState({ stockAmount: e.target.value })
          }} /></div>
          <div>秒杀商品？<input type="checkbox" value={this.state.isFlashSale}
            onChange={(e) => {
              this.setState({ isFlashSale: !this.state.isFlashSale })
            }} /></div>
          {
            this.state.isFlashSale && (
              <>
                <div>秒杀开始时间：<input type="text" value={this.state.flashSaleStartTime}
                  onChange={(e) => {
                    this.setState({ flashSaleStartTime: e.target.value })
                  }} /></div>
                <div>秒杀结束时间：<input type="text" value={this.state.flashSaleEndTime}
                  onChange={(e) => {
                    this.setState({ flashSaleEndTime: e.target.value })
                  }} /></div>
              </>
            )}
          <div style={{
            alignSelf: 'flex-end'
          }}><button onClick={(e) => {
            // alert(`name=${this.state.name},
            //   stockAmount=${this.state.stockAmount},
            //   flashSale=${this.state.isFlashSale},
            //   flashSaleStartTime=${this.state.flashSaleStartTime},
            //   flashSaleEndTime=${this.state.flashSaleEndTime}`)

            let url;
            let successMessage;
            if (this.state.isFlashSale) {
              // 新增秒杀商品
              url = "api/v1/order/addFlashSaleProduct"
              successMessage = "成功新增秒杀商品"
            } else {
              // 新增普通商品
              url = "api/v1/order/addOrdinaryProduct"
              successMessage = "成功新增普通商品"
            }

            this.setState({ loading: true, loadingText: "处理中..." })
            axios.get(url, {
              params: {
                name: this.state.name,
                stockAmount: this.state.stockAmount,
                flashSaleStartTime: this.state.flashSaleStartTime,
                flashSaleEndTime: this.state.flashSaleEndTime,
                merchantId: localStorage.getItem("merchantId")
              },
            }).then((data) => {
              var thisObj = this;
              Modal.confirm({
                title: '提示',
                content: `${successMessage}，是否跳转到商品详情功能并下单呢？`,
                okText: '确定',
                cancelText: '取消',
                onOk() {
                  thisObj.props.history.push(`/productInfo?id=${data}`)
                },
              })
            }).catch(function (error) {
              message.error(error.errorMessage)
            }).finally(() => {
              this.setState({ loading: false, loadingText: "" })
            })
          }}>新增</button></div>
        </div>

        {
          // 操作中等待框
          this.state.loading && (<div
            style={{
              position: 'fixed',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              backgroundColor: 'rgba(0, 0, 0, 0.5)',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              zIndex: 9999,
            }}
          >
            <Spin tip={this.state.loadingText}></Spin>
          </div>)
        }
      </div>
    )
  }
}

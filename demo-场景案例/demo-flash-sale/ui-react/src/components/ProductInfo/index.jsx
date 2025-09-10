import React, { Component } from 'react'
import styles from './index.module.css'
import qs from 'query-string'
import axios from '../../api/axios'
import { Spin, message } from 'antd'

export default class ProductInfo extends Component {

  state = {
    product: {
      // name: "x",
      // stock: 2,
      // flashSale: true,
      // toFlashSaleStartTimeRemainingSeconds: 0,
      // toFlashSaleEndTimeRemainingSeconds: 0
    },
    loading: false,
    loadingText: ""
  }

  componentDidMount() {
    const { id } = qs.parse(this.props.location.search)
    let productId = id
    this.setState({ loading: true, loadingText: "处理中..." })
    axios.get("/api/v1/order/getProductById", {
      params: {
        id: productId
      },
    }).then((data) => {
      this.setState({ product: data })
    }).catch((error) => {
      alert(error.errorMessage)
    }).finally(() => {
      this.setState({ loading: false, loadingText: "" })
    })
  }

  render() {
    return (
      <div className={styles.container}>
        <div className={styles['info-panel']}>
          <div className="image">
            <img width="100%" height="250" />
          </div>
          <div className="info-container">
            <div title={this.state.product.name} style={{ overflow: 'hidden' }}>商品名称：{this.state.product.name}</div>
            <div>库存：{this.state.product.stock}</div>
            {
              this.state.product.flashSale && (
                <div>
                  {
                    this.state.product.toFlashSaleStartTimeRemainingSeconds > 0 && (<div style={{ color: 'orange' }}>
                      距离秒杀开始时间还有 {this.state.product.toFlashSaleStartTimeRemainingSeconds} 秒
                    </div>)
                  }
                  {
                    this.state.product.toFlashSaleStartTimeRemainingSeconds <= 0 && this.state.product.toFlashSaleEndTimeRemainingSeconds > 0 && (
                      <div style={{ color: 'yellowgreen' }}>
                        距离秒杀结束还有 {this.state.product.toFlashSaleEndTimeRemainingSeconds} 秒
                      </div>
                    )
                  }
                  {
                    this.state.product.toFlashSaleStartTimeRemainingSeconds <= 0 && this.state.product.toFlashSaleEndTimeRemainingSeconds <= 0 && (
                      <div style={{ color: 'red' }}>
                        秒杀已结束
                      </div>
                    )
                  }
                </div>
              )
            }
          </div>
        </div>
        <div className={styles['operation-panel']}>
          <button onClick={(e) => {
            let url;
            if (this.state.product.flashSale) {
              // 秒杀下单
              url = "api/v1/order/createFlashSale"
            } else {
              // 普通下单
              url = "api/v1/order/create"
            }

            this.setState({ loading: true, loadingText: "下单中..." })
            axios.get(url, {
              params: {
                userId: localStorage.getItem("userId"),
                productId: this.state.product.id,
                randomCreateTime: false,
              },
            }).then((data) => {
              message.success(data)
            }).catch((error) => {
              message.error(error.errorMessage)
            }).finally(() => {
              this.setState({ loading: false, loadingText: "" })
            })
          }}>{this.state.product.flashSale ? "秒杀" : "购买"}</button>
        </div >

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
      </div >
    )
  }
}

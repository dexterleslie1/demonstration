import React, { Component } from 'react'
import styles from './index.module.css'
import axios from '../../api/axios'
import { message, Spin } from 'antd'

export default class ProductList extends Component {

  state = {
    productList: [],
    loading: false,
    loadingText: ""
  }

  refreshProductList = () => {
    this.setState({ loading: true, loadingText: "加载中..." })
    axios.get("api/v1/order/listProductByIds", {}).then((data) => {
      this.setState({ productList: data })
    }).catch((error) => {
      message.error(error.errorMessage)
    }).finally(() => {
      this.setState({ loading: false, loadingText: "" })
    })
  }

  componentDidMount() {
    this.refreshProductList()
  }

  render() {
    return (
      <div className={styles.container}>
        <div className={styles['operation-panel']}>
          <button onClick={(e) => {
            this.props.history.push(`/listByUserId`)
          }}>跳转到查询当前用户订单信息</button>
          &nbsp;&nbsp;<button onClick={(e) => {
            this.props.history.push(`/listByMerchantId`)
          }}>跳转到查询当前商家订单信息</button>
          &nbsp;&nbsp;<button onClick={(e) => {
            this.props.history.push(`/createProduct`)
          }}>新增商品</button>
          &nbsp;&nbsp;<button onClick={(e) => {
            this.refreshProductList()
          }}>刷新商品列表</button>
        </div>
        <div className={styles['product-container']}>
          {
            this.state.productList.map((item, index) => {
              return <div onClick={(e) => {
                this.props.history.push(`/productInfo?id=${item.id}`)
              }} className={styles['item']} key={item.id}>
                <div className={styles['image']}>
                  <img width="100%" height="250" />
                </div>
                <div className={styles['info-container']}>
                  <div title={item.name} style={{ overflow: 'hidden' }}>商品名称：{item.name}</div>
                  <div>库存：{item.stock}</div>
                  {
                    item.flashSale && (<div>
                      {
                        item.toFlashSaleStartTimeRemainingSeconds > 0 && (<div style={{ color: 'orange' }}>
                          距离秒杀开始时间还有 {item.toFlashSaleStartTimeRemainingSeconds} 秒
                        </div>)
                      }
                      {
                        item.toFlashSaleStartTimeRemainingSeconds <= 0 && item.toFlashSaleEndTimeRemainingSeconds > 0 && (<div style={{ color: 'yellowgreen' }}>
                          距离秒杀结束还有 {item.toFlashSaleEndTimeRemainingSeconds} 秒
                        </div>)
                      }
                      {
                        item.toFlashSaleStartTimeRemainingSeconds <= 0 && item.toFlashSaleEndTimeRemainingSeconds <= 0 && (<div style={{ color: 'red' }}>
                          秒杀已结束
                        </div>)
                      }
                    </div>)
                  }
                </div>
              </div>
            })
          }
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

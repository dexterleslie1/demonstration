import React, { Component } from 'react'
import styles from './index.module.css'
import axios from '../../api/axios'
import { message, Spin } from 'antd'

export default class ListByMerchantId extends Component {

  state = {
    orderList: [],
    queryStatus: '',
    loading: false,
    loadingText: "",
  }

  render() {
    return (
      <div className={styles.container}>
        <div className={styles.title}>商家查询订单列表</div>
        <div className={styles['operation-panel']}>
          <span>
            状态：
            <select value={this.state.queryStatus} onChange={(e) => {
              this.setState({ queryStatus: e.target.value })
            }}>
              <option value="">全部</option>
              <option value="Unpay">未支付</option>
              <option value="Undelivery">未发货</option>
              <option value="Unreceive">未收货</option>
              <option value="Received">已签收</option>
              <option value="Canceled">买家取消</option>
            </select>
          </span>
          <span>
            &nbsp;<button onClick={(e) => {
              let url;
              let status;
              if (this.state.queryStatus == '') {
                // 所有状态的订单
                url = "api/v1/order/listByMerchantIdAndWithoutStatus"
                status = null
              } else {
                url = "api/v1/order/listByMerchantIdAndStatus"
                status = this.state.queryStatus
              }

              this.setState({ loading: true, loadingText: "加载中..." })
              axios.get(url, {
                params: {
                  merchantId: localStorage.getItem("merchantId"),
                  latestMonth: true,
                  status: status
                }
              }).then((data) => {
                this.setState({ orderList: data })
              }).catch((error) => {
                message.error(error.errorMessage)
              }).finally(() => {
                this.setState({ loading: false, loadingText: "" })
              })
            }}>查询</button>
          </span>
        </div>
        <div className={styles['order-list-container']}>
          {
            this.state.orderList.length === 0 && (<div style={{ color: 'red' }}>没有订单信息</div>) ||
            this.state.orderList.map((item, index) => {
              return <div key={item.id}>
                <span>订单ID：{item.id}</span>
                ，<span>创建时间：{item.createTime}</span>
                ，<span>用户ID：{item.userId}</span>
                ，<span>
                  商品列表：
                  {
                    item.orderDetailList.map((detail, detailIndex) => {
                      return <React.Fragment key={detail.id}>
                        {detail.productName}，
                      </React.Fragment>
                    })
                  }
                </span>
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
      </div >
    )
  }
}

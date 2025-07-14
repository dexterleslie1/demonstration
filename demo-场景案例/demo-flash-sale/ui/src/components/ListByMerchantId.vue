<template>
    <div class="container">
        <div class="title">商家查询订单列表</div>
        <div class="operation-panel">
            <span>
                状态：
                <select v-model="queryStatus">
                    <option value="">全部</option>
                    <option value="Unpay">未支付</option>
                    <option value="Undelivery">未发货</option>
                    <option value="Unreceive">未收货</option>
                    <option value="Received">已签收</option>
                    <option value="Canceled">买家取消</option>
                </select>
            </span>
            <!-- <span>
                &nbsp;开始时间：<input type="text" size="10" />
            </span>
            <span>
                &nbsp;结束时间：<input type="text" size="10" />
            </span> -->
            <span>
                &nbsp;<button @click="handleClickQuery()">查询</button>
            </span>
        </div>
        <div class="order-list-container">
            <div v-for="item in this.orderList">
                <span>订单ID：{{ item.id }}</span>
                ，<span>创建时间：{{ item.createTime }}</span>
                ，<span>用户ID：{{ item.userId }}</span>
                ，<span>
                    商品列表：
                    <template v-for="detail in item.orderDetailList">
                        {{ detail.productName }}，
                    </template>
                </span>
            </div>
            <div v-if="this.orderList.length === 0" style="color:red;">没有订单信息</div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            queryStatus: '',
            orderList: [
                // {
                //     "id": 1944677143434874895,
                //     "userId": 1,
                //     "createTime": "2025-07-14 16:35:36",
                //     "status": {
                //         "name": "Undelivery",
                //         "description": "未发货"
                //     },
                //     "payTime": null,
                //     "deliveryTime": null,
                //     "receivedTime": null,
                //     "cancelTime": null,
                //     "deleteStatus": null,
                //     "merchantId": 2196861,
                //     "merchantName": null,
                //     "orderDetailList": [
                //         {
                //             "id": null,
                //             "orderId": 1944677143434874895,
                //             "productId": 19,
                //             "productName": null,
                //             "amount": 1
                //         }
                //     ]
                // }
            ]
        }
    },
    methods: {
        handleClickQuery() {
            let url;
            let status;
            if (this.queryStatus == '') {
                // 所有状态的订单
                url = "api/v1/order/listByMerchantIdAndWithoutStatus"
                status = null
            } else {
                url = "api/v1/order/listByMerchantIdAndStatus"
                status = this.queryStatus
            }

            const loading = this.$loading({
                lock: true,
                text: '加载中...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            this.$axios.get(url, {
                params: {
                    merchantId: localStorage.getItem("merchantId"),
                    latestMonth: true,
                    status: status
                }
            }).then((data) => {
                this.orderList = data
            }).catch((error) => {
                this.$message.error(error.errorMessage)
            }).finally(() => {
                loading.close()
            })
        }
    }
}
</script>

<style scoped>
.container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.title {
    margin-top: 10px;
}

.operation-panel {
    margin-top: 10px;
}

.order-list-container {
    margin-top: 10px;
}
</style>
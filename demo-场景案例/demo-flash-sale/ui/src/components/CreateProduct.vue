<template>
    <div class="container">
        <div class="form-container">
            <div>
                <div style="color:yellowgreen;">提示：不填写商品信息新增会自动随机生成商品信息</div>
                <div>
                    商品名称：<input type="text" v-model="name" />
                </div>
                <div>
                    库存数量：<input type="text" v-model="stockAmount" />
                </div>
                <div>
                    秒杀商品？<input type="checkbox" v-model="flashSale" />
                </div>
                <div v-if="flashSale">
                    秒杀开始时间：<input type="text" v-model="flashSaleStartTime" />
                </div>
                <div v-if="flashSale">
                    秒杀结束时间：<input type="text" v-model="flashSaleEndTime" />
                </div>
            </div>
            <div class="operation-panel">
                <button @click="handleClickAdd()">新增</button>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            name: null,
            stockAmount: -1,
            flashSale: false,
            flashSaleStartTime: null,
            flashSaleEndTime: null
        }
    },
    methods: {
        handleClickAdd() {
            let url;
            let successMessage;
            if (this.flashSale) {
                // 新增秒杀商品
                url = "api/v1/order/addFlashSaleProduct"
                successMessage = "成功新增秒杀商品"
            } else {
                // 新增普通商品
                url = "api/v1/order/addOrdinaryProduct"
                successMessage = "成功新增普通商品"
            }

            const loading = this.$loading({
                lock: true,
                text: '处理中...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            this.$axios.get(url, {
                params: {
                    name: this.name,
                    stockAmount: this.stockAmount,
                    flashSaleStartTime: this.flashSaleStartTime,
                    flashSaleEndTime: this.flashSaleEndTime,
                    merchantId: localStorage.getItem("merchantId")
                },
            }).then((data) => {
                this.$confirm(`${successMessage}，是否跳转到商品详情功能并下单呢？`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'success'
                }).then(() => {
                    this.$router.push({
                        path: "/productInfo",
                        query: { id: data }
                    })
                }).catch(() => {

                });
            }).catch((error) => {
                this.$message.error(error.errorMessage)
            }).finally(() => {
                loading.close()
                this.name = null
                this.stockAmount = -1
                this.flashSale = false
                this.flashSaleStartTime = null
                this.flashSaleEndTime = null
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

.operation-panel {
    margin-top: 10px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
}
</style>
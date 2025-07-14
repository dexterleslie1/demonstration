<template>
    <div class="container">
        <div class="info-panel" v-if="product">
            <div class="image">
                <img width="100%" height="250" />
            </div>
            <div class="info-container">
                <div :title="product.name" style="overflow: hidden;">商品名称：{{ product.name }}</div>
                <div>库存：{{ product.stock }}</div>
                <div v-if="product.flashSale">
                    <div v-if="product.toFlashSaleStartTimeRemainingSeconds > 0" style="color:orange">
                            距离秒杀开始时间还有 {{ product.toFlashSaleStartTimeRemainingSeconds }} 秒
                        </div>

                        <div v-if="product.toFlashSaleStartTimeRemainingSeconds<=0 && product.toFlashSaleEndTimeRemainingSeconds > 0" style="color:yellowgreen">
                            距离秒杀结束还有 {{ product.toFlashSaleEndTimeRemainingSeconds }} 秒
                        </div>

                        <div v-if="product.toFlashSaleStartTimeRemainingSeconds<=0 && product.toFlashSaleEndTimeRemainingSeconds <= 0" style="color:red">
                            秒杀已结束
                        </div>
                </div>
            </div>
        </div>
        <div class="operation-panel">
            <button @click="handleClickBuy()">{{ product.flashSale ? "秒杀" : "购买" }}</button>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            product: null
        }
    },
    mounted() {
        let productId = this.$route.query.id
        const loading = this.$loading({
            lock: true,
            text: '处理中...',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
        });
        this.$axios.get("/api/v1/order/getProductById", {
            params: {
                id: productId
            },
        }).then((data) => {
            this.product = data
        }).catch((error) => {
            this.$message.error(error.errorMessage)
        }).finally(() => {
            loading.close()
        })
    },
    methods: {
        handleClickBuy() {
            let url;
            if (this.product.flashSale) {
                // 秒杀下单
                url = "api/v1/order/createFlashSale"
            } else {
                // 普通下单
                url = "api/v1/order/create"
            }
            const loading = this.$loading({
                lock: true,
                text: '下单中...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            this.$axios.get(url, {
                params: {
                    userId: localStorage.getItem("userId"),
                    productId: this.product.id,
                    randomCreateTime: false,
                },
            }).then((data) => {
                this.$message({
                    message: data,
                    type: "success"
                })
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

.container .info-panel {
    margin-top: 10px;
    width: 250px;
    border-radius: 10px;
    border-style: solid;
    border-width: 1px;
    border-color: #7f7f7f;
    padding: 10px;
    transition: all 0.5s;
}

.container .info-panel:hover {
    border-color: #000;
    background-color: #f1f1f1;
}

.operation-panel {
    margin-top: 10px;
}
</style>
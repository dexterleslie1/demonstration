<template>
    <div class="container">
        <div class="operation-panel">
            <button @click="handleClickNavToListByUserId()">跳转到查询当前用户订单信息</button>
            &nbsp;
            <button @click="handleClickNavToListByMerchantId()">跳转到查询当前商家订单信息</button>
            &nbsp;
            <button @click="handleClickNavToCreateProduct()">新增商品</button>
            &nbsp;
            <button @click="handleClickRefresh()">刷新商品列表</button>
        </div>
        <div class="product-container">
            <div @click="handleClickItem(item)" class="item" v-for="(item, index) in productList">
                <div class="image">
                    <img width="100%" height="250" />
                </div>
                <div class="info-container">
                    <div :title="item.name" style="overflow: hidden;">商品名称：{{ item.name }}</div>
                    <div>库存：{{ item.stock }}</div>
                    <div v-if="item.flashSale">
                        <div v-if="item.toFlashSaleStartTimeRemainingSeconds > 0" style="color:orange">
                            距离秒杀开始时间还有 {{ item.toFlashSaleStartTimeRemainingSeconds }} 秒
                        </div>

                        <div v-if="item.toFlashSaleStartTimeRemainingSeconds <= 0 && item.toFlashSaleEndTimeRemainingSeconds > 0"
                            style="color:yellowgreen">
                            距离秒杀结束还有 {{ item.toFlashSaleEndTimeRemainingSeconds }} 秒
                        </div>

                        <div v-if="item.toFlashSaleStartTimeRemainingSeconds <= 0 && item.toFlashSaleEndTimeRemainingSeconds <= 0"
                            style="color:red">
                            秒杀已结束
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            // 商品列表
            productList: null
        }
    },
    mounted() {
        this.refreshProductList()
    },
    methods: {
        handleClickItem(product) {
            this.$router.push({
                name: "ProductInfo",
                query: { id: product.id }
            })
        }, handleClickNavToListByUserId() {
            this.$router.push("/listByUserId")
        }, handleClickNavToListByMerchantId() {
            this.$router.push("/listByMerchantId")
        }, handleClickNavToCreateProduct() {
            this.$router.push("/createProduct")
        }, handleClickRefresh() { this.refreshProductList() },
        refreshProductList() {
            const loading = this.$loading({
                lock: true, text: '加载中...',
                spinner: 'el-icon-loading', background: 'rgba(0, 0, 0, 0.7)'
            });
            this.$axios.get("api/v1/order/listProductByIds", {}).then((data) => {
                this.productList = data
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

.operation-panel {
    margin-top: 10px;
}

.container .product-container {
    margin-top: 10px;
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    width: 60%;
}

.container .product-container .item {
    width: 150px;
    border-radius: 10px;
    border-style: solid;
    border-width: 1px;
    border-color: #7f7f7f;
    padding: 10px;
    transition: all 0.5s;
}

.container .product-container .item:hover {
    border-color: #000;
    background-color: #f1f1f1;
    cursor: pointer;
}
</style>

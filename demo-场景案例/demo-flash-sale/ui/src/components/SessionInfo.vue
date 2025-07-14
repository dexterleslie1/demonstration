<template>
    <div class="container">
        <div class="form-container">
            <div class="form-item">
                <div>用户ID：</div>
                <div>
                    <input type="text" v-model="userId" />
                </div>
            </div>
            <div class="form-item">
                <div>商家ID：</div>
                <div>
                    <input type="text" v-model="merchantId" />
                </div>
            </div>
            <div class="form-op-item">
                <div>
                    <button @click="handleClickOk()">确定</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            userId: null,
            merchantId: null,
        }
    },
    methods: {
        handleClickOk() {
            if (this.userId)
                localStorage.setItem("userId", this.userId)
            if (this.merchantId)
                localStorage.setItem("merchantId", this.merchantId)

            this.$router.push('/productList');
        }
    },
    mounted() {
        // 读取上次是否已经设置过数据
        const userId = localStorage.getItem("userId")
        if (userId)
            this.userId = userId

        const merchantId = localStorage.getItem("merchantId")
        if (merchantId)
            this.merchantId = merchantId
    }
}
</script>

<style scoped>
.container {
    display: flex;
    justify-content: center;
    align-items: center;
}

.container .form-container {
    margin-top: 10px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.container .form-item {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
}

.container .form-op-item {
    margin-top: 10px;
    align-self: flex-end;
}
</style>
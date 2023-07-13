<template>
    <div>
        <div>商品列表</div>

        <!-- 二级路由: 商品详情，创建目录 pages/goods/_id.vue 目录用于表达路由结构 -->
        <nuxt-link to="/goods/1?p1=1&p2=11">商品1</nuxt-link>
        <nuxt-link to="/goods/2?p1=2&p2=22">商品2</nuxt-link>
        <nuxt-link :to="{ name: 'goods-id', params: { id: 3 }, query: { p1: 3, p2: 33 } }">商品3</nuxt-link>
        <a href="#" @click="handleClick">商品5</a>

        <!-- 用于显示nuxt-link跳转，相当于router-view -->
        <nuxt />
    </div>
</template>

<script>
export default {
    methods: {
        handleClick() {
            // https://stackoverflow.com/questions/64992189/how-to-achieve-programatic-routing-in-nuxt
            this.$router.push({ name: 'goods-id', params: { id: 5 }, query: { p1: 5, p2: 55 } })
        }
    }
}
</script>

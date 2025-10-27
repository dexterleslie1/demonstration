<template>
    <div>
        <RouterLink to="/">首页</RouterLink> |
        <RouterLink to="/about">关于</RouterLink> |
        <button @click="routeProgramatically">编程式路由</button> |
        <RouterLink to="/xxx">跳到404页面</RouterLink>
        <!-- router-view 是 Vue Router 的核心组件，用于根据当前路由动态渲染匹配的组件。它是实现单页面应用（SPA）“页面切换”的关键载体。 -->
        <router-view :key="$route.fullPath"></router-view>
    </div>
</template>

<script>
import { RouterLink, RouterView } from 'vue-router';

export default {
    methods: {
        routeProgramatically: function () {
            // this.$router.push({
            //   path: "/about",
            //   query: { id: 1 }
            // })
            this.$router.push({
                name: "About",
                query: { id: 1 }
            })
        }
    }
}
</script>

<style scoped></style>
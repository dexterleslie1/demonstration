<template>
    <div>
        <nuxt-link to="/">首页</nuxt-link>
        <nuxt-link to="/register">注册</nuxt-link>

        <nuxt/> 
    </div>
</template>

<script>
export default {
    middleware({req, res, route, redirect, params, query}) {
        console.log(`layouts页面全局前置守卫`)
    }
}
</script>
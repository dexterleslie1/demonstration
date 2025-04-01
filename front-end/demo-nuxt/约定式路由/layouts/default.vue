<template>
    <div>
        <!-- 一级路由 -->
        <nuxt-link to="/">首页</nuxt-link>
        <nuxt-link to="/register">注册</nuxt-link>
        <nuxt-link to="/goods">商品列表</nuxt-link>

        <!-- 用于显示nuxt-link跳转，相当于router-view -->
        <nuxt/>
    </div>
</template>
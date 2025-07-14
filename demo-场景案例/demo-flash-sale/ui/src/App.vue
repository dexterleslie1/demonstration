<template>
  <div id="app">
    <div v-if="this.$route.path !== '/'">
      当前用户ID：{{ getCurrentUserId }}，
      当前商家ID：{{ getCurrentMerchantId }}
      &nbsp;<a href="/">首页</a>
      &nbsp;<a href="#" @click.prevent="() => { $router.go(-1) }" v-if="this.$route.path !== '/productList'">返回</a>
    </div>
    <!-- router-view 是 ​​Vue Router 的核心组件​​，用于根据当前路由动态渲染匹配的组件。它是实现单页面应用（SPA）“页面切换”的关键载体。 -->
    <router-view :key="$route.fullPath"></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',
  components: {
  },
  computed: {
    getCurrentUserId() {
      return localStorage.getItem("userId")
    },
    getCurrentMerchantId() {
      return localStorage.getItem("merchantId")
    }
  }
}
</script>

<style>
html,
body {
  margin: 0;
  padding: 0;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>

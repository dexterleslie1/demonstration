<template>
  <div id="app">
    <RouterLink to="/">首页</RouterLink> |
    <RouterLink to="/about">关于</RouterLink> |
    <button @click="routeProgramatically">编程式路由</button>
    <!-- router-view 是 Vue Router 的核心组件，用于根据当前路由动态渲染匹配的组件。它是实现单页面应用（SPA）“页面切换”的关键载体。 -->
    <router-view></router-view>
  </div>
</template>

<script>
import { RouterLink, RouterView } from 'vue-router';
import HelloWorld from './components/HelloWorld.vue'
import router from './router';

export default {
  name: 'App',
  components: {
    HelloWorld
  },
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
  },
  mounted() {
    // 动态添加/about路由以模拟加载菜单场景
    router.addRoutes([
      {
        path: '/about',
        name: 'About',
        component: () => import(`@/components/About`)
      }]);
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>

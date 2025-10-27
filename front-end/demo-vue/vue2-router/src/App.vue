<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
import { RouterLink, RouterView } from 'vue-router';
import HelloWorld from './components/HelloWorld.vue'
import router from './router';
import Layout from './components/Layout.vue';

export default {
  name: 'App',
  components: {
    HelloWorld
  },
  mounted() {
    // 动态添加/about路由以模拟加载菜单场景
    router.addRoutes([
      {
        path: '/about',
        name: 'LayoutAbout',
        component: Layout,
        children: [
          {
            path: "",
            name: "About",
            component: () => import(`@/components/About`)
          }
        ]
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

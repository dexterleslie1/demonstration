<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <RouterLink to="/">组件1</RouterLink> |
    <RouterLink to="/component2">组件2</RouterLink> |
    <RouterLink to="/component3">组件3</RouterLink>
    <keep-alive :include="keepAliveComponents">
      <router-view></router-view>
    </keep-alive>
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    HelloWorld
  },
  data() {
    return {
      // keep-alive标签只缓存Component1和Component3组件
      keepAliveComponents: ["Component1", "Component3",]
    }
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

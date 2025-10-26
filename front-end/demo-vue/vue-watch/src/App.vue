<template>
  <div id="app">
    <div id="nav">
      <router-link to="/">演示watch用法</router-link> |
      <router-link to="/about">About</router-link>
    </div>
    <router-view />
  </div>
</template>


<script>
export default {
  name: 'App',
  components: {
  },
  watch: {
    // 监听路由变化
    "$route": {
      handler(to, from) {
        console.log(`路由变化为：${this.$route.fullPath}，to：${(to || {}).fullPath}，from：${(from || {}).fullPath}`)
      },
      // 组件创建时（立即执行）
      immediate: true
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
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>

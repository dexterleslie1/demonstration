<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <hr />
    <div>按钮Enable和Disable状态切换</div>
    <button :disabled="buttonEnable">Enable和Disable状态切换按钮</button><br/>
    <button @click="handleClick">点击我切换上面按钮状态</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      buttonEnable: true
    }
  },
  methods: {
    handleClick() {
      this.buttonEnable = !this.buttonEnable
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

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>

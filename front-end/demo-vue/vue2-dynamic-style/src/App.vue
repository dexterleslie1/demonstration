<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->
    <hr>
    <div>
      演示使用对象方式动态设置<br>
      https://blog.51cto.com/lenglingx/6579713
    </div>
    <div>
      <div :style="{ width: this.width + 'px', height: this.height + 'px', 'background-color': 'antiquewhite' }"></div>
    </div>
    <div>
      <button @click="handleClick">点击我动态增加div宽度和高度</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      width: 50,
      height: 50,
    }
  },
  methods: {
    handleClick() {
      this.width = this.width + 5
      this.height = this.height + 5
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

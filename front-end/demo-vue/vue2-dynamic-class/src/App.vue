<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <!-- https://www.jb51.net/article/254535.htm -->
    <hr />
    <div>使用对象语法动态添加类</div>
    <div>
      <div :class="{ 'my-font-color': applyMyFontColor}">内容</div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      applyMyFontColor: true
    }
  }
}
</script>

<style>
.my-font-color {
  color: #42b983;
}

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

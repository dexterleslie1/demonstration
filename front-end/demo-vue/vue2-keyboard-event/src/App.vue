<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <div>
      演示input聚焦后按下enter键触发事件
    </div>
    <div>
      <textarea @keydown.ctrl.enter="handleOnKeydownEnter">
        我是一个文本框。
      </textarea>
    </div>
    <hr>
  </div>
</template>

<script>
export default {
  methods: {
    handleOnKeydownEnter() {
      alert('你按下enter按钮')
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

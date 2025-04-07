<template>
  <div id="app">
    <ThemeToggle />
    <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App" />
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import ThemeToggle from './components/ThemeToggle.vue'

export default {
  name: 'App',
  components: {
    HelloWorld,
    ThemeToggle
  }
}
</script>

<style>
:root {
  --bg-color: #ffffff;
  --text-color: #2c3e50;
  --hover-bg-color: rgba(0, 0, 0, 0.1);
}

/*  表示当 HTML 元素的 data-theme 属性值为 "dark" 时，应用以下样式规则 */
[data-theme="dark"] {
  --bg-color: #1a1a1a;
  --text-color: #ffffff;
  --hover-bg-color: rgba(255, 255, 255, 0.1);
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: var(--text-color);
  background-color: var(--bg-color);
  min-height: 100vh;
  margin: 0;
  padding-top: 60px;
  transition: background-color 0.3s, color 0.3s;
}

html,
body {
  margin: 0;
  padding: 0;
}
</style>

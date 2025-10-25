<template>
  <!-- <div id="app">
    <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/>
  </div> -->
  <LayoutBasic />
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import LayoutBasic from './components/LayoutBasic.vue';

export default {
  name: 'App',
  components: {
    HelloWorld,
    LayoutBasic
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

<template>
  <div id="app">
    <MyComponent1></MyComponent1>
  </div>
</template>

<script>
import MyComponent1 from './components/MyComponent1.vue';

export default {
  name: 'App',
  components: {
    MyComponent1,
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

<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <div>选择省市区：{{ value }}</div>
    <el-cascader v-model="value" :options="options" :props="cascaderProps"></el-cascader>
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import areas from './areas.js'

export default {
  name: 'App',
  components: {
    HelloWorld
  },
  data() {
    return {
      value: [],
      options: areas,
      cascaderProps: {
        value: 'code',
        label: 'name',
        children: 'children',
      },
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

<template>
  <!-- <div id="app">
    <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/>
  </div> -->
  <TableTreeDatum />
  <!-- <TableBasic /> -->
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import TableTreeDatum from './components/TableTreeDatum.vue';
import TableBasic from './components/TableBasic.vue';

export default {
  name: 'App',
  components: {
    HelloWorld,
    TableTreeDatum,
    TableBasic
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

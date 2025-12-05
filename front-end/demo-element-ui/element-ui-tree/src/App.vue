<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <!-- <TreeBasic /> -->
    <TreeFiltering />
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import TreeBasic from './components/TreeBasic.vue';
import TreeFiltering from './components/TreeFiltering.vue';

export default {
  name: 'App',
  components: {
    HelloWorld,
    TreeBasic,
    TreeFiltering,
  }
}
</script>

<style></style>

<template>
  <div id="app">
    <MenuHorizontal />
    <!-- <MenuVertical /> -->
    <!-- <MenuCollapseAndExpand /> -->
  </div>
</template>

<script>
import MenuHorizontal from './components/MenuHorizontal.vue';
import MenuVertical from './components/MenuVertical.vue';
import MenuCollapseAndExpand from './components/MenuCollapseAndExpand.vue';

export default {
  name: 'App',
  components: {
    MenuHorizontal,
    MenuVertical,
    MenuCollapseAndExpand
  }
}
</script>

<style>
#app {}
</style>

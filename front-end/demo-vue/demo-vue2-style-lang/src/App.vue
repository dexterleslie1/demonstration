<template>
  <div id="app">
    <LessDemo />
    <ScssDemo />
  </div>
</template>

<script>
import ScssDemo from './components/ScssDemo.vue'
import LessDemo from './components/LessDemo.vue'

export default {
  name: 'App',
  components: {
    ScssDemo,
    LessDemo
  }
}
</script>

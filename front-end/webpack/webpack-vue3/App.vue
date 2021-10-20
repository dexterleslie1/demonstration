<template>
  <div>
    <div>{{message}}</div>
    <div><Helloworld></Helloworld></div>
  </div>
</template>

<script>
import Helloworld from './Helloworld.vue'

export default {
  name: "App",
  components: {
      Helloworld
  },
  data() {
    return {
      message: "This is a demo"
    }
  }
}
</script>

<style>
</style>
<template>
  <div id="app">
    <div>简单数组声明自定义属性</div>
    <CustomizePropertiesByUsingArray message="测试消息" count="8,234"></CustomizePropertiesByUsingArray>
    <hr />

    <div>对象形式声明自定义属性</div>
    <CustomizePropertiesByUsingObject :user="user" message="Hello" count="8,234" status="active"></CustomizePropertiesByUsingObject>
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import CustomizePropertiesByUsingArray from '@/components/CustomizePropertiesByUsingArray.vue'
import CustomizePropertiesByUsingObject from '@/components/CustomizePropertiesByUsingObject.vue'

export default {
  name: 'App',
  components: {
    HelloWorld,
    CustomizePropertiesByUsingArray,
    CustomizePropertiesByUsingObject
  },
  data() {
    return {
      user: {
        name: 'Dexter'
      }
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

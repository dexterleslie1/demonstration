<template>
  <div id="app">
    <MyComponent1></MyComponent1>
    <button @click="handleClick1()">使用方法：this.$message.error({ ... })调用</button>&nbsp;&nbsp;
    <button @click="handleClick2()">使用方法：this.$message({ ... })调用</button>
  </div>
</template>

<script>
export default {
  name: 'App',
  components: {
  },
  methods: {
    handleClick1() {
      // 在 Vue 中使用 this.$message 提示功能，本质是 ​​Element UI 插件向 Vue 原型链注入了 $message 方法​​。
      this.$message.error('错了哦，这是一条错误消息')
    },
    handleClick2() {
      // 在 Vue 中使用 this.$message 提示功能，本质是 ​​Element UI 插件向 Vue 原型链注入了 $message 方法​​。
      this.$message({
        message: '错了哦，这是一条错误消息',
        type: 'error'
      })
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

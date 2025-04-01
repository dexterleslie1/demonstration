<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->

    <!--
      NOTE: 使用手机访问并让el-autocomplete获取焦点时，网页自动zoomin，通过声明 meta viewport maximum-scale=1.0解决此问题。
    -->
    <el-autocomplete style="width:100%" ref="refSearchKeyword" placeholder="输入搜索内容"
    :fetch-suggestions="querySearch" clearable>
    </el-autocomplete>

    <!-- <el-input placeholder="Please input" v-model="input"></el-input> -->

    <!-- <el-input type="textarea" :rows="2" placeholder="Please input" v-model="textarea">
    </el-input> -->


  </div>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  // components: {
  //   HelloWorld
  // }
  methods: {
    querySearch() {

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

<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    使用v-model绑定变量：<input v-model="var1" /><button @click="var1++">+</button>
    <br />
    <button @click="handleClick">显示变量值</button>
    <hr />

    <div>v-model input radio 使用</div>
    <input type="radio" name="radioGroup" value="0" v-model="varRadio" />Radio1
    <input type="radio" name="radioGroup" value="1" v-model="varRadio" />Radio2
    <div>varRadio变量值：{{ varRadio }}</div>
    <hr />

    <div>v-model input checkbox 使用</div>
    <input type="checkbox" name="checkboxGroup" value="0" v-model="varCheckbox" />Checkbox1
    <input type="checkbox" name="checkboxGroup" value="1" v-model="varCheckbox" />Checkbox2
    <div>varCheckbox变量值：{{ varCheckbox }}</div>
    <hr />

    <div>v-model input select 使用</div>
    <select name="select" v-model="varSelect" multiple>
      <option value="0">0</option>
      <option value="1">1</option>
    </select>
    <div>varSelect变量值：{{ varSelect }}</div>
    <hr />

  </div>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'

export default {
  data() {
    return {
      var1: 1,
      // input radio 变量
      varRadio: 0,
      // input checkbox 变量
      varCheckbox: [1],
      // 支持多选时 select 变量
      varSelect: [1],
      // 单选时 select 变量
      // varSelect: 0,
    }
  },
  methods: {
    handleClick() {
      alert(this.var1)
    }
  },
  name: 'App',
  components: {
    // HelloWorld
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

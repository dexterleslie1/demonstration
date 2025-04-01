<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <!-- https://www.gxlsystem.com/qianduan-2450489.html -->
    <hr/>
    <div>使用v-if显示或者隐藏div</div>
    <div v-if="true" style="width:100px;height:100px;background-color: antiquewhite;"></div>

    <hr/>
    <div>使用v-show显示或者隐藏div</div>
    <div v-show="true" style="width:100px;height:100px;background-color: antiquewhite;"></div>

    <hr/>
    <div>通过点击按钮切换显示的 DIV</div>
    <div><button @click="showIndex=(showIndex+1)%3">点击我切换显示的 DIV</button></div>
    <div v-show="this.showIndex==0" style="width:100px;height:100px;background-color: antiquewhite;">1</div>
    <div v-show="this.showIndex==1" style="width:100px;height:100px;background-color: antiquewhite;">2</div>
    <div v-show="this.showIndex==2" style="width:100px;height:100px;background-color: antiquewhite;">3</div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showIndex: 0
    }
  },
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>

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
  </div>
</template>

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

<template>
  <div id="nav">
    <router-link to="/login">登录界面</router-link> |
    <router-link to="/drawer">Drawer</router-link> |
    <router-link to="/scrollbar">Scrollbar</router-link> |
    <router-link to="/tabs">Tabs</router-link> |
    <router-link to="/container" target="_blank">Container</router-link>
  </div>
  <hr/>
  <router-view/>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>

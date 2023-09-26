<template>
  <div id="app">
    <div id="nav">
      <router-link to="/">演示</router-link> |
    </div>
    <div id="main-container">
      <router-view/>
    </div>
  </div>
</template>

<style>
body, html {
  height: 100%;
  width: 100%;
  margin: 0px;
  padding: 0px;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  height: 100%;
  display: flex;
  flex-direction: column;
}

#nav {
  padding: 30px;
}

#main-container {
  flex: 1;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>

<template>
  <div id="nav">
    <router-link to="/">流式上传和下载演示</router-link> |
    <router-link to="/slice">分片上传和下载演示</router-link> |
    <router-link to="/download">流式下载、图片预览、范围下载演示</router-link> |
    <router-link to="/fileReaderApi">FileReader api演示</router-link>
  </div>
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

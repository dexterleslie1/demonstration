<template>
  <div id="nav">
    <router-link to="/">首页</router-link> |
    <router-link to="/help">帮助</router-link> |
    <router-link to="/about">关于我们</router-link>
  </div>
  <hr>
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

/* 
不是精确匹配样式，例如：/help和当前route.fullPath=/help/helpArticle?article=download不是精确匹配
但是此时会激活这个样式
*/
#nav a.router-link-active {
  color: #42b983;
}
</style>

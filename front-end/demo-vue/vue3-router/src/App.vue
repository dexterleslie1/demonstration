<template>
  <div id="nav">
    <!-- 声明式导航 -->
    <router-link to="/">首页</router-link> |
    <router-link to="/help">帮助</router-link> |
    <router-link to="/about">关于我们</router-link> |
    <button @click="(e)=>{this.$router.go(-1)}">返回</button> |
    <button @click="routeProgramatically">编程式路由</button>
  </div>
  <hr>
  <!-- router-view 是 Vue Router 的核心组件，用于根据当前路由动态渲染匹配的组件。它是实现单页面应用（SPA）“页面切换”的关键载体。 -->
  <router-view/>
</template>

<script>
export default {
  methods: {
    routeProgramatically: function() {
      // 编程式路由
      // this.$router.push("/help")
      this.$router.push({
        path: "/help/helpArticle",
        query: {
          article: 'login'
        }
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

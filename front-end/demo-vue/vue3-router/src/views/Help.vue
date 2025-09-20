<template>
  <div>
    <div class="navigator">
      <ul>
        <li>
            <!-- 
                嵌套路由，路由前缀需要和父级路由匹配，
                例如：嵌套路由 /help/helpArticle?article=download 和父级路由 /help 匹配
            -->
            <router-link 
              to="/help/helpArticle?article=download"
              v-bind:class="isActive('/help/helpArticle?article=download')">
              下载帮助
            </router-link>
        </li>
        <li>
            <router-link 
              to="/help/helpArticle?article=login"
              v-bind:class="isActive('/help/helpArticle?article=login')">
              登录帮助
            </router-link>
        </li>
      </ul>
    </div>
    <div class="content">
      <!--
        若同一组件被多次访问（如从 `/user/123` 跳转到 `/user/456`），由于 Vue 组件复用，`created` 等生命周期不会重新执行。
        在组件中监听 `$route.params` 或 `$route.query` 的变化。
        或者 在 `<router-view>` 中添加 `:key` 属性，强制重新渲染组件。例如：<router-view :key="$route.fullPath" />
      -->
      <router-view :key="$route.fullPath"></router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: "Help",
  methods: {
    // 计算当前激活的route
    isActive: function(path) {
      let fullPath = this.$route.fullPath
      return {active: path==fullPath}
    }
  }
}
</script>

<style scoped>
.navigator {
  width: 30%;
  height: 400px;
  background-color: #42b983;
  float: left;
}
.content {
  width: 70%;
  height: 400px;
  background-color: #cccccc;
  float: right;
}
ul {
  display: inline-block;
  margin: 0px;
  padding: 0px;
}

/*
自定义当前激活的route样式
*/
ul li a.active {
  font-size: 20px;
  font-weight: bold;
  color: yellow;
}
</style>
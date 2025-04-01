<template>
  <div>
    <div class="navigator">
      <ul>
        <li>
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
      <router-view></router-view>
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
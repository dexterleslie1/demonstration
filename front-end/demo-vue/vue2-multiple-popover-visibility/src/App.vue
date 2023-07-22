<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <div v-for="(_, index) in [1, 2, 3, 4, 5]">
      <el-popover placement="bottom" width="160" v-model="popoverVisibleArr[index]">
        <div>
          <el-input type="textarea" autosize placeholder="输入内容...">
          </el-input>
        </div>
        <div style="text-align: right; margin: 0">
          <el-button size="mini" type="text" @click="$set(popoverVisibleArr, index, false)">取消</el-button>
        </div>
        <el-button size="mini" icon="el-icon-circle-plus" type="primary" slot="reference">添加</el-button>
      </el-popover>

      <el-divider></el-divider>
    </div>

  </div>
</template>

<script>
export default {
  data() {
    return {
      popoverVisibleArr: []
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

<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <div>多个popover</div>
    <div v-for="(_, index) in [1, 2, 3, 4, 5]" style="margin: 5px 0px 5px 0px;">
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
    </div>
    <hr>

    <div>共享popover</div>
    <el-popover ref="popoverShared" placement="bottom" width="160" v-model="popoverVisible">
      <div>
        <el-input type="textarea" autosize placeholder="输入内容...">
        </el-input>
      </div>
      <div style="text-align: right; margin: 0">
        <el-button size="mini" type="text" @click="popoverVisible = false">取消</el-button>
      </div>
    </el-popover>
    <div v-for="(_, index) in [1, 2, 3, 4, 5]" style="margin: 5px 0px 5px 0px;">
      <!-- todo 失去焦点时自动关闭popover -->
      <el-button size="mini" icon="el-icon-circle-plus" type="primary" @click="handleClick($event)">添加</el-button>    
    </div>
    <hr>

  </div>
</template>

<script>
export default {
  data() {
    return {
      popoverVisibleArr: [],
      popoverVisible: false
    }
  },

  methods: {
    // https://www.nowcoder.com/discuss/381758405671702528
    handleClick(event) {
      this.popoverVisible = false
      this.$refs.popoverShared.doDestroy(true)
      this.$nextTick(() => {
        this.$refs.popoverShared.referenceElm = event.target
        this.popoverVisible = true
      })
    },
    handle1() {
      console.log('888888')
      this.popoverVisible=false
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

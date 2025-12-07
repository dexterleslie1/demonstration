<template>
  <div id="app">
    <div>确认消息</div>
    <el-button type="primary" @click="handleClickOk">点击我</el-button>
    <hr />

    <div>提交内容</div>
    <el-button type="primary" @click="handleClickPrompt">点击</el-button>
    <hr />
  </div>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    // HelloWorld
  },
  methods: {
    handleClickOk() {
      this.$confirm('确定删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message(
          {
            message: '点击确定',
            type: 'success'
          }
        )
      }).catch(() => {
        this.$message(
          {
            message: '点击取消',
            type: 'info'
          }
        )
      });
    },
    // 提交内容
    handleClickPrompt() {
      this.$prompt('请输入邮箱', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        this.$message({
          type: 'success',
          message: '你的邮箱是: ' + value
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        });
      })
    }
  }
}
</script>

<style>
#app {}
</style>

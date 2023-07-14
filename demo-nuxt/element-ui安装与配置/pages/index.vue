<template>
  <div>
    <el-button type="primary" @click="handleClick">点击我</el-button>
  </div>
</template>

<script>
export default {
  name: 'IndexPage',

  methods: {
    handleClick() {
      let thisObj = this
      thisObj.$confirm('确认退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        alert('确定')
      }).catch(() => {
        alert('取消')
      })
    }
  }
}
</script>

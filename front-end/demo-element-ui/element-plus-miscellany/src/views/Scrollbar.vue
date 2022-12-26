<template>
  <el-button @click="visible=true">演示el-drawer内容显示使用el-scrollbar</el-button>

  <el-drawer
      v-model="visible"
      title="抽屉"
      direction="rtl">
    <el-scrollbar
          ref="scrollbar"
          style="height: calc(100vh - 100px)">
      <p v-for="item in 20" :key="item" class="scrollbar-demo-item">{{ item }}</p>
    </el-scrollbar>
  </el-drawer>
</template>

<script>
export default {
  name: "Scrollbar",
  data() {
    return {
      visible: false
    }
  }
}
</script>

<style scoped>

</style>
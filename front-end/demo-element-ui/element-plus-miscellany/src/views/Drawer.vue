<template>
  <el-button @click="visible=true">Drawer右到左弹出显示</el-button>

  <el-drawer
      v-model="visible"
      title="抽屉"
      direction="rtl">
    <div>
      抽屉内容
    </div>
  </el-drawer>
</template>

<script>
export default {
  name: "Drawer",
  data() {
    return {
      visible: false
    }
  }
}
</script>

<style scoped>

</style>
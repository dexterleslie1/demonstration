<template>
  <el-button @click="visible=true">kkk</el-button>

  <el-drawer
      title="添加授权"
      v-model="visible"
      direction="rtl">
    <el-container>
      <el-header>
        添加授权
        <el-divider></el-divider>
      </el-header>
      <el-main>
        <el-scrollbar height="400px;">
          <p v-for="item in 20" :key="item" class="scrollbar-demo-item">{{ item }}</p>
        </el-scrollbar>
      </el-main>
      <el-footer>
        <el-divider></el-divider>
        <el-button>取消</el-button>
        <el-button>确定</el-button>
      </el-footer>
    </el-container>
  </el-drawer>
</template>

<script>
export default {
  name: "Container",
  data() {
    return {
      visible: false
    }
  }
}
</script>

<style scoped>
.el-drawer__body {
  height: 100%;
  box-sizing: border-box;
  overflow-y: auto;
}
</style>
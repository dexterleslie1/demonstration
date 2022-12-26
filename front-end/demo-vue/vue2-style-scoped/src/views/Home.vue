<template>
  <div class="home">
    <el-image>
      <div slot="error" class="image-slot">
        <i class="el-icon-picture-outline"></i>
      </div>
    </el-image>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from '@/components/HelloWorld.vue'

export default {
  name: 'Home',
  components: {
    HelloWorld
  },
  data() {
    return {
    }
  }
}
</script>

<style scoped lang="scss">
/* 
Vue中的scoped的实现原理以及scoped穿透的用法
https://blog.csdn.net/sinat_17775997/article/details/83416617
*/
// 方法1
// NOTE: 这样不会造成全局el-image样式污染问题，如果使用style标签不带scoped会造成全局el-image演示污染问题
.el-image {
  width: 160px;
  height: 160px;
  border-radius: 5px;
  border-style: solid;
  border-width: 1px;

  /deep/ .image-slot {
    font-size: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    color: #a3a3a3;
  }
}
</style>
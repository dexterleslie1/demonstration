<template>
  <div>
    帮助说明组件
    article query参数：{{article}}
  </div>
</template>

<script>
export default {
  name: "ComponentHelp",
  data() {
    return {
      article: ''
    }
  },
  created() {
    // NOTE：第一次加载页面获取article参数
    this.article = this.$route.query.article
  },
  watch:{
    // NOTE：需要监听路由变化才能够获取article参数
    $route(to, from){
      this.article = this.$route.query.article
    }
  }
}
</script>

<style scoped>

</style>
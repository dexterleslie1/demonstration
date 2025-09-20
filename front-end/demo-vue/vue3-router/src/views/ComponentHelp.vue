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
    // 注意：第一次加载页面获取article参数
    this.article = this.$route.query.article
  },
  watch:{
    // 注意：需要监听路由变化才能够获取article参数
    // 若同一组件被多次访问（如从 `/user/123` 跳转到 `/user/456`），由于 Vue 组件复用，`created` 等生命周期不会重新执行。
    // 在组件中监听 `$route.params` 或 `$route.query` 的变化。
    // 或者 在 `<router-view>` 中添加 `:key` 属性，强制重新渲染组件。例如：<router-view :key="$route.fullPath" />
    // $route(to, from){
    //   this.article = this.$route.query.article
    // }
  }
}
</script>

<style scoped>

</style>
<template>
  <div>首页</div>
</template>

<script>
export default {
  name: 'IndexPage',
  middleware({req, res, route, redirect, params, query}) {
    console.log(`页面级别的前置守卫`)
  }
}
</script>

<template>
    <div class="container">
        <div>统一错误处理页面</div>
        <h1 v-if="error.myErrorCode === 90000">错误代码: 90000</h1>
        <h1 v-else>意料之外错误</h1>
        <NuxtLink to="/">Home page</NuxtLink>
    </div>
</template>
  
<script>
export default {
    props: ['error'],
}
</script>
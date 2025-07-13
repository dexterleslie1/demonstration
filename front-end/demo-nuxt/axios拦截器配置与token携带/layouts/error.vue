<template>
    <div class="container">
        <div>统一错误处理页面</div>
        <h3 v-if="!errorCode || !errorMessage">错误代码：{{ errorCode }}，错误信息：{{ errorMessage }}</h3>
        <NuxtLink to="/">Home page</NuxtLink>
    </div>
</template>
  
<script>
export default {
    props: ['errorCode', 'errorMessage'],
}
</script>
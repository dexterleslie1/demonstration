<template>
    <div>
        <!-- 使用简单数组声明自定义属性 -->
        {{ message }} - {{ count }}
    </div>
</template>

<script>
export default {
    // 使用简单数组声明自定义属性
    props: ['message', 'count']
}
</script>

<style scoped></style>
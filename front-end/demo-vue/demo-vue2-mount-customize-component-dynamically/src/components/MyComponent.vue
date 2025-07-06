<template>
    <div>
        你好，{{ name }}！<button @click="$emit('close')">关闭</button>
    </div>
</template>

<script>
export default {
    props: ["name"]
}
</script>

<style scoped></style>
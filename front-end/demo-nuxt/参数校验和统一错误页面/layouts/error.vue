<template>
    <div>
        <h6>统一错误界面</h6>
        <div>{{ error.statusCode }} - {{ error.message }}</div>
    </div>
</template>

<script>
export default {
    props: ['error']
}
</script>
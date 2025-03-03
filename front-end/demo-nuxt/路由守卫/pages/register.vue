<template>
    <div>
        注册页面
    </div>
</template>

<script>
export default {
    // 后置守卫
    beforeRouteLeave(to, from, next) {
        let b = window.confirm("是否要离开？")
        next(b)
    }
}
</script>
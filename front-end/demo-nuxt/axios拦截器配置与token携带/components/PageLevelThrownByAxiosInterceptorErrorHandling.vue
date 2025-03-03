<template>
    <div>
        <div>页面级别的asyncData不需要编写错误处理逻辑，只是页面编写错误显示逻辑</div>
        <div v-if="myError">错误信息: {{ myError }}</div>
    </div>
</template>

<script>

export default {
    name: 'PageLevelThrownByAxiosInterceptorErrorHandling',
    data() {
        return {
            myError: null
        }
    },

    async asyncData({ app, $axios, error }) {
        // NOTE: 这是服务器端调用axios，element-ui不会弹出窗口
        // app.router.app.$message.error('88888')

        let data = await $axios.get('/api/v1/test1')
        return data.data
    }
}

</script>
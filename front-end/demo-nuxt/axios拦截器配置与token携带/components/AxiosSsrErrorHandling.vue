<template>
    <div>
        <div>页面级别的asyncData自定义try catch错误处理</div>
        <div v-if="errorMessage">调用接口 /api/v1/test1 失败，errorCode={{ errorCode }}，errorMessage={{ errorMessage }}</div>
        <div v-if="response">调用接口 /api/v1/test1 成功，服务器响应 {{ response }}</div>
    </div>
</template>

<script>

export default {
    name: 'AxiosSsrErrorHandling',
    data() {
        return {
            errorCode: null,
            errorMessage: null,
            response: null
        }
    },
    async asyncData({ app, $axios, error }) {
        try {
            let response = await $axios.get('/api/v1/test1?exceptionType=bizExceptionWithHttp200')

            // 返回的对象会合并到组件的 data 中
            return {
                response: response
            }
        } catch (err) {
            // 协助打印 err 对象
            // console.log(`err 对象 ${JSON.stringify(err)}`)

            // 返回 error 自定义错误对象和 vue 数据合并
            return {
                errorCode: err.errorCode,
                errorMessage: err.errorMessage
            }

            // 注意：这是服务器端调用axios，element-ui不会弹出窗口
            // app.router.app.$message.error('88888')

            // 跳转到 error.vue 页面
            // https://stackoverflow.com/questions/56283813/nuxt-js-error-not-defined-when-trying-to-throw-404-in-failed-await-call-with
            // return error({ errorCode: err.errorCode, errorMessage: err.errorMessage })
        }
    }
}

</script>

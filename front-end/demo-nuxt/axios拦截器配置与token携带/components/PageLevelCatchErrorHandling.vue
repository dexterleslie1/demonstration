<template>
    <div>
        <div>页面级别的asyncData自定义try catch错误处理</div>
        <div v-if="myError">错误信息: {{ myError }}</div>
    </div>
</template>

<script>

export default {
    name: 'PageLevelCatchErrorHandling',
    data() {
        return {
            myError: null
        }
    },
    async asyncData({ app, $axios, error }) {
        try {
            let response = await $axios.get('/api/v1/test1', { disableGlobalOnErrorHandling: true /* 禁用全局错误处理，否则无法catch到错误而是被回调then */ })
            console.log(`+++++++response=${response.data}`)
            return response.data
        } catch (err) {
            console.log(`+++++++PageLevelCatchErrorHandling出现错误`)

            // NOTE: 这是服务器端调用axios，element-ui不会弹出窗口
            // app.router.app.$message.error('88888')
            
            // 返回myError自定义错误对象和vue数据合并
            return {
                myError: err
            }

            // // 跳转到error.vue页面
            // // https://stackoverflow.com/questions/56283813/nuxt-js-error-not-defined-when-trying-to-throw-404-in-failed-await-call-with
            // return error({ myErrorCode: 90000, myErrorMessage: '888888' })
        }
    }
}

</script>

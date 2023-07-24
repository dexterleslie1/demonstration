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
    async asyncData({ $axios, error }) {
        try {
            let data = await $axios.get('/api/v1/test1', { disableGlobalOnErrorHandling: true/*此请求禁用全局onError处理逻辑，以便自己错误处理*/ })
            return data.data
        } catch (err) {
            console.log(`+++++++PageLevelCatchErrorHandling出现错误`)
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

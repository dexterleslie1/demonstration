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

    async asyncData({ $axios, error }) {
        let data = await $axios.get('/api/v1/test1')
        return data.data
    }
}

</script>
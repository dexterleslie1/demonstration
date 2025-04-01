<template>
    <div>
        <div>axios csr和ssr错误处理</div>
        <button @click="handleClick1">点击我1</button>
        <button @click="handleClick2">点击我2</button>
    </div>
</template>

<script>

export default {
    name: 'AxiosCsrAndSsrErrorHandling',

    methods: {
        async handleClick1() {
            try {
                let response = await this.$axios.get('/api/v1/test1')
                console.log(`+++++++response=${JSON.stringify(response.data)}`)
            } catch (err) {
                console.log(`+++++err=${err}`)
            }
        },
        async handleClick2() {
            try {
                let response = await this.$axios.get('/api/v1/test1', { disableGlobalOnErrorHandling: true })
                console.log(`+++++++response=${JSON.stringify(response.data)}`)
            } catch (err) {
                this.$message.error(`axios调用出错: ${err}`)
            }
        }
    }
}
</script>

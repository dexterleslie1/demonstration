<template>
    <div>
        <div>axios csr和ssr错误处理</div>
        <button @click="handleClick">点击我</button>
    </div>
</template>

<script>

export default {
    name: 'AxiosCsrAndSsrErrorHandling',

    methods: {
        async handleClick() {
            try {
                let response = await this.$axios.get('/api/v1/test1')
                console.log(`+++++++response=${JSON.stringify(response.data)}`)
            } catch(err) {
                console.log(`+++++err=${err}`)
            }
        }
    }
}
</script>

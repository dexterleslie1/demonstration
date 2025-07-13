<template>
    <div>
        <div>axios csr和ssr错误处理</div>
        <button @click="handleClick1">没有异常</button>
        <button @click="handleClick11">业务异常发生，http 200</button>
        <button @click="handleClick12">业务异常发生，http 400</button>
        <button @click="handleClick2">不使用客户端全局异常处理逻辑</button>
    </div>
</template>

<script>

export default {
    name: 'AxiosCsrErrorHandling',

    methods: {
        async handleClick1() {
            try {
                let response = await this.$axios.get('/api/v1/test1')
                // 控制台输出：没有异常，response={"errorCode":0,"errorMessage":null,"data":"Nuxt api返回数据, token=my token"}
                // console.log(`没有异常，response=${JSON.stringify(response)}`)
                this.$message({ message: `没有异常，response=${JSON.stringify(response)}`, type: 'success' })
            } catch (err) {
                console.log(`有异常，err=${JSON.stringify(err)}`)
            }
        },
        async handleClick11() {
            try {
                let response = await this.$axios.get('/api/v1/test1?exceptionType=bizExceptionWithHttp200')
                console.log(`没有异常，response=${JSON.stringify(response)}`)
            } catch (err) {
                // 控制台输出：有异常，err={"errorCode":90000,"errorMessage":"模拟业务异常","data":null}
                console.log(`有异常，err=${JSON.stringify(err)}`)
            }
        },
        async handleClick12() {
            try {
                let response = await this.$axios.get('/api/v1/test1?exceptionType=bizExceptionWithHttp400')
                console.log(`没有异常，response=${JSON.stringify(response)}`)
            } catch (err) {
                // 控制台输出：有异常，err={"errorCode":90000,"errorMessage":"模拟业务异常","data":null}
                console.log(`有异常，err=${JSON.stringify(err)}`)
            }
        },
        async handleClick2() {
            try {
                let response = await this.$axios.get('/api/v1/test1?exceptionType=bizExceptionWithHttp400', { disableGlobalOnErrorHandling: true })
                console.log(`没有异常，response=${JSON.stringify(response)}`)
            } catch (err) {
                this.$message.error(`axios调用出错: ${JSON.stringify(err)}`)
            }
        }
    }
}
</script>

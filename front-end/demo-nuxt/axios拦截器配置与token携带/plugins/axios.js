export default function ({ app, $axios, redirect, route, error }) {
    console.log(`加载axios自定义插件`)

    // 设置axios超时设置
    $axios.defaults.timeout = 30000

    // 请求时拦截器
    $axios.onRequest(function (config) {
        console.log(`axios请求时拦截，config=${JSON.stringify(config)}`)

        config.headers.Authorization = "my token"

        return config
    })

    // 响应时拦截器
    $axios.onResponse(function (response) {
        console.log(`axios响应时拦截，response=${JSON.stringify(response.data)},
        x-my-request-id=${JSON.stringify(response.headers['x-my-request-id'])}`)

        return response
    })

    // axios全局错误处理
    $axios.onError(function (err) {
        // 服务器端处理逻辑
        if(process.server) {
            if (!(err.config && err.config.disableGlobalOnErrorHandling === true)) {
                console.log(`++++++++服务端axios错误，准备return myError`)

                // NOTE: 如果有return数据，说明错误已经处理，调用者无法再catch到错误而是执行到then回调
                return {
                    data: {
                        myError: err
                    }
                }

                // 跳转到error.vue页面
                // return error({ myErrorCode: 90000, myErrorMessage: '888888' })
            }
        } else {
            if (!(err.config && err.config.disableGlobalOnErrorHandling === true)) {
                // 使用全局错误处理
                console.log(`++++++++客户端axios错误，准备throw err`)
                
                app.router.app.$message.error(`axios调用出错: ${err}`)
            }

            // NOTE: 暂时没有找到方案阻断调用者then或者catch执行，抛出err以便调用者catch错误
            throw err
        }
    })

}
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
                return {
                    data: {
                        myError: err
                    }
                }

                // 跳转到error.vue页面
                // return error({ myErrorCode: 90000, myErrorMessage: '888888' })
            }
        } else {
            // 客户端处理逻辑
            throw err
        }
    })

}
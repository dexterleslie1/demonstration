export default function({$axios, redirect, route}) {
    console.log(`加载axios自定义插件`)

    // 设置axios超时设置
    $axios.defaults.timeout = 30000

    // 请求时拦截器
    $axios.onRequest(function(config) {
        console.log(`axios请求时拦截，config=${JSON.stringify(config)}`)

        config.headers.Authorization = "my token"

        return config
    })

    // 响应时拦截器
    $axios.onResponse(function(response) {
        console.log(`axios响应时拦截，response=${JSON.stringify(response.data)}`)

        return response
    })

    // axios全局错误处理
    $axios.onError(function(error) {
        console.log(`axios错误发生时，error=${error}`)

        return error
    })

}
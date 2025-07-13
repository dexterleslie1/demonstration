export default function ({ app, $axios, redirect, route, error }) {
    console.log(`加载axios自定义插件`)

    class BusinessError extends Error {
        /**
         * 构造业务异常
         * @param {int} errorCode - 错误代号
         * @param {string} errorMessage - 错误信息（用于提示）
         * @param {object} data - 业务错误数据（如后端返回的 errorCode、errorMessage）
         */
        constructor(errorCode, errorMessage, data) {
            // 初始化原生 Error 的 message
            super(errorMessage);
            // 自定义属性：存储业务错误数据
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.data = data;
            // 修复原型链（可选，但推荐，避免某些环境下的原型问题）
            Object.setPrototypeOf(this, new.target.prototype);
        }
    }

    // 设置axios全局超时设置
    $axios.defaults.timeout = 30000

    // 请求时拦截器，csr和ssr都会回调此函数
    $axios.onRequest(function (config) {
        // 控制台输出：axios请求时拦截，config={"url":"/api/v1/test1","method":"get","headers":{"common":{"Accept":"application/json, text/plain, */*"},"delete":{},"get":{},"head":{},"post":{"Content-Type":"application/x-www-form-urlencoded"},"put":{"Content-Type":"application/x-www-form-urlencoded"},"patch":{"Content-Type":"application/x-www-form-urlencoded"}},"baseURL":"/","transformRequest":[null],"transformResponse":[null],"timeout":30000,"xsrfCookieName":"XSRF-TOKEN","xsrfHeaderName":"X-XSRF-TOKEN","maxContentLength":-1,"maxBodyLength":-1,"transitional":{"silentJSONParsing":true,"forcedJSONParsing":true,"clarifyTimeoutError":false}}
        console.log(`axios请求时拦截，config=${JSON.stringify(config)}`)

        // 请求发出前修改请求头
        config.headers.Authorization = "my token"

        return config
    })

    // 响应时拦截器，csr和ssr都会回调此函数
    $axios.onResponse(function (response) {
        if (process.server) {
            // SSR情况
            // http状态码为200，业务异常发生
            if (response?.data?.errorCode > 0) {
                // 这个抛出的异常会被下面的$axio.onError捕获并处理
                throw new BusinessError(response.data.errorCode, response.data.errorMessage, response.data.data)
            }

            // 返回业务接口响应的数据，response其他的信息丢弃后续逻辑不会再使用这些信息
            return response.data;
        } else {
            // CSR情况
            // 控制台输出：axios响应时拦截，response={"data":{"errorCode":0,"errorMessage":null,"data":"Nuxt api返回数据, token=my token"},"status":200,"statusText":"OK","headers":{"cache-control":"no-cache, no-store, max-age=0, must-revalidate","connection":"close","content-type":"application/json","date":"Thu, 10 Jul 2025 06:27:15 GMT","expires":"0","pragma":"no-cache","transfer-encoding":"chunked","x-content-type-options":"nosniff","x-frame-options":"DENY","x-my-request-id":"0431c50e-9150-488c-80d7-e913b8e37a37","x-xss-protection":"1; mode=block"},"config":{"url":"/api/v1/test1","method":"get","headers":{"Accept":"application/json, text/plain, */*","Authorization":"my token"},"baseURL":"/","transformRequest":[null],"transformResponse":[null],"timeout":30000,"xsrfCookieName":"XSRF-TOKEN","xsrfHeaderName":"X-XSRF-TOKEN","maxContentLength":-1,"maxBodyLength":-1,"transitional":{"silentJSONParsing":true,"forcedJSONParsing":true,"clarifyTimeoutError":false}},"request":{}},x-my-request-id="0431c50e-9150-488c-80d7-e913b8e37a37"
            console.log(`axios响应时拦截，response=${JSON.stringify(response)},x-my-request-id=${JSON.stringify(response.headers['x-my-request-id'])}`)

            // http状态码为200，业务异常发生
            if (response?.data?.errorCode > 0) {
                // 这个抛出的异常会被下面的$axio.onError捕获并处理
                throw new BusinessError(response.data.errorCode, response.data.errorMessage, response.data.data)
            }

            // 返回业务接口响应的数据，response其他的信息丢弃后续逻辑不会再使用这些信息
            return response.data;
        }
    })

    // axios全局错误处理，csr和ssr都会回调此函数
    $axios.onError(function (err) {
        // 协助打印 err
        // console.log(`err 对象 ${err.response}`)

        if (process.server) {
            // SSR 错误处理
            // 所有 SSR 错误都需要统一处理
            const responseData = err.response?.data
            const errorCode = responseData ? responseData.errorCode : 90000
            const errorMessage = responseData ? responseData.errorMessage : err.message
            const data = responseData ? responseData.data : null
            throw new BusinessError(errorCode, errorMessage, data)

            // 跳转到error.vue页面
            // return error({ errorCode: 90000, errorMessage: '888888' })
        } else {
            // CSR 错误处理
            const errOrigin = err
            if (!(err instanceof BusinessError)) {
                // err.response 的数据样例：{"data":{"errorCode":90000,"errorMessage":"模拟业务异常","data":null},"status":400,"statusText":"Bad Request","headers":{"cache-control":"no-cache, no-store, max-age=0, must-revalidate","connection":"close","content-type":"application/json","date":"Thu, 10 Jul 2025 07:22:38 GMT","expires":"0","pragma":"no-cache","transfer-encoding":"chunked","x-content-type-options":"nosniff","x-frame-options":"DENY","x-my-request-id":"bd3f9310-0488-4da5-a86c-65cc7abdbc38","x-xss-protection":"1; mode=block"},"config":{"url":"/api/v1/test1?exceptionType=bizExceptionWithHttp400","method":"get","headers":{"Accept":"application/json, text/plain, */*","Authorization":"my token"},"baseURL":"/","transformRequest":[null],"transformResponse":[null],"timeout":30000,"xsrfCookieName":"XSRF-TOKEN","xsrfHeaderName":"X-XSRF-TOKEN","maxContentLength":-1,"maxBodyLength":-1,"transitional":{"silentJSONParsing":true,"forcedJSONParsing":true,"clarifyTimeoutError":false}},"request":{}}

                // 转换为 BusinessError 类型异常
                if (err.response?.data?.errorCode)
                    // 后端接口返回的异常
                    err = new BusinessError(err.response?.data?.errorCode, err.response?.data?.errorMessage, err.response?.data?.data)
                else
                    // 非后端接口返回的异常，例如：504网关超时
                    err = new BusinessError(90000, err.response?.statusText, null)
            }

            if (!(errOrigin?.config?.disableGlobalOnErrorHandling === true)) {
                // 客户端全局异常处理逻辑
                // 控制台输出：axios全局异常处理逻辑捕获客户端异常，err={"errorCode":90000,"errorMessage":"模拟业务异常","data":null}
                console.log(`axios全局异常处理逻辑捕获客户端异常，err=${JSON.stringify(err)}`)
                app.router.app.$message.error(`axios调用出错: ${JSON.stringify(err)}`)
            }

            // 把异常往上层调用者抛出，以让调用者处理
            throw err
        }
    })

}
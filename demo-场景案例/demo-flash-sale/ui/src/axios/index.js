import axios from 'axios'

// 设置基础 URL 为 /
axios.defaults.baseURL = "/";
// 设置全局超时为 10000 毫秒
axios.defaults.timeout = 10000;

// 请求发出前拦截器
axios.interceptors.request.use((config) => {
    // 调试打印
    // console.log(`请求前拦截config=${JSON.stringify(config)}`);

    // 判断是否需要自动添加header1
    if (config?.needHeader) {
        if (!config.headers) {
            config.headers = {}
        }
        config.headers.header1 = 'header1 value'
    }

    return config;
}, (error) => {
    // 暂时不清楚什么情况下request会回调此函数
    console.log(error);
});

axios.interceptors.response.use((config) => {
    // 调试打印
    // console.log(`请求响应拦截config=${JSON.stringify(config)}`);

    if (config.data.errorCode > 0) {
        let errorCode = config.data.errorCode
        let errorMessage = config.data.errorMessage

        if (!errorCode || !errorMessage) {
            errorCode = 5000
            errorMessage = '服务器没有返回具体错误信息'
        }

        return Promise.reject({ errorCode, errorMessage })
    }

    // 忽略服务器返回config.data.errorCode、config.data.errorMessage直接返回data数据
    if (config.headers['content-type'] && config.headers['content-type'].startsWith('application/json')) {
        return config.data.data
    } else {
        return config.data
    }
}, (error) => {
    // 调试打印
    // console.log(`error ${JSON.stringify(error)}`)

    // 非 http 200 时错误处理回调
    if (error && !error.response) {
        // 网络错误
        return Promise.reject({ errorCode: 5000, errorMessage: error.message, httpStatus: -1 })
    } else {
        // 调试打印
        // console.log(`error ${JSON.stringify(error)}`)

        let response = error.response
        let httpStatus = response.status
        let errorCode = response?.data?.errorCode ? response.data.errorCode : 5000
        let errorMessage = response?.data?.errorMessage ? response.data.errorMessage : error.message
        return Promise.reject({ errorCode, errorMessage, httpStatus })
    }
});

export default axios;

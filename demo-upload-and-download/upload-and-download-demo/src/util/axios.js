import axios from 'axios'

// 全局axios配置
axios.defaults.baseURL = "/";
axios.defaults.timeout = 120000;
// return true、设置为null或者undefined，promise将resolved,否则将rejected
axios.defaults.validateStatus = function (status) {
    return status >= 200 && status <= 500 // 默认的
}

// http request拦截
axios.interceptors.request.use(config => {
    return config
}, error => {
    return Promise.reject(error)
})

// http response拦截
axios.interceptors.response.use((result) => {
    const status = Number(result.status) || 200

    if (status !== 200 || result.data.errorCode > 0) {
        const errorMessage = result.data.errorMessage
        return Promise.reject(errorMessage)
    }

    return result
}, error => {
    return Promise.reject(error)
})

export default axios
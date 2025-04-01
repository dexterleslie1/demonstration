import axios from "axios";

// 全局配置
// 设置基础 URL 为 /
axios.defaults.baseURL = "/";
// 设置全局超时为 5000 毫秒
axios.defaults.timeout = 5000;
// request、response 拦截器
axios.interceptors.request.use((config) => {
    let json = JSON.stringify(config);
    let message = `请求前拦截config=${json}`;
    console.log(message);

    // 判断是否需要自动添加header1
    if (config && config.needHeader) {
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
    let json = JSON.stringify(config);
    let message = `请求响应拦截config=${json}`;
    console.log(message);

    // 如果http响应状态码不为200
    let httpStatus = config.status
    if (httpStatus !== 200) {
        let errorCode = config.data.errorCode
        let errorMessage = config.data.errorMessage

        if (!errorCode || !errorMessage) {
            errorCode = 5000
            errorMessage = '服务器没有返回具体错误信息'
        }

        return Promise.reject({ errorCode, errorMessage, httpStatus })
    }

    if (httpStatus === 200 && config.data.errorCode > 0) {
        let errorCode = config.data.errorCode
        let errorMessage = config.data.errorMessage

        if (!errorCode || !errorMessage) {
            errorCode = 5000
            errorMessage = '服务器没有返回具体错误信息'
        }

        return Promise.reject({ errorCode, errorMessage, httpStatus })
    }

    // 忽略服务器返回config.data.errorCode、config.data.errorMessage直接返回data数据
    if (config.headers['content-type'] && config.headers['content-type'].startsWith('application/json')) {
        return config.data.data
    } else {
        return config.data
    }
}, (error) => {
    if (error && !error.response) {
        // 网络错误
        return Promise.reject({ errorCode: 5000, errorMessage: error.message, httpStatus: -1 })
    } else {
        let response = error.response
        let httpStatus = response.status
        let errorCode = response.data.errorCode
        let errorMessage = response.data.errorMessage
        if (!errorCode || !errorMessage) {
            errorCode = 5000
            errorMessage = '服务器没有返回具体错误信息'
        }
        return Promise.reject({ errorCode, errorMessage, httpStatus })
    }
});

let api1 = axios.create({
    baseURL: "/",
    timeout: 5000
});
// request、response 拦截器
api1.interceptors.request.use((config) => {
    let json = JSON.stringify(config);
    let message = `请求前拦截config=${json}`;
    console.log(message);

    // 判断是否需要自动添加header1
    if (config && config.needHeader) {
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
api1.interceptors.response.use((config) => {
    let json = JSON.stringify(config);
    let message = `请求响应拦截config=${json}`;
    console.log(message);

    // 如果http响应状态码不为200
    let httpStatus = config.status
    if (httpStatus !== 200) {
        let errorCode = config.data.errorCode
        let errorMessage = config.data.errorMessage

        if (!errorCode || !errorMessage) {
            errorCode = 5000
            errorMessage = '服务器没有返回具体错误信息'
        }

        return Promise.reject({ errorCode, errorMessage, httpStatus })
    }

    if (httpStatus === 200 && config.data.errorCode > 0) {
        let errorCode = config.data.errorCode
        let errorMessage = config.data.errorMessage

        if (!errorCode || !errorMessage) {
            errorCode = 5000
            errorMessage = '服务器没有返回具体错误信息'
        }

        return Promise.reject({ errorCode, errorMessage, httpStatus })
    }

    // 忽略服务器返回config.data.errorCode、config.data.errorMessage直接返回data数据
    if (config.headers['content-type'] && config.headers['content-type'].startsWith('application/json')) {
        return config.data.data
    } else {
        return config.data
    }
}, (error) => {
    if (error && !error.response) {
        // 网络错误
        return Promise.reject({ errorCode: 5000, errorMessage: error.message, httpStatus: -1 })
    } else {
        let response = error.response
        let httpStatus = response.status
        let errorCode = response.data.errorCode
        let errorMessage = response.data.errorMessage
        if (!errorCode || !errorMessage) {
            errorCode = 5000
            errorMessage = '服务器没有返回具体错误信息'
        }
        return Promise.reject({ errorCode, errorMessage, httpStatus })
    }
});

const $ = require('jquery')

$(document).ready(function () {
    $('#btnPutWithBody').click(function () {
        // 测试put方法提交body参数
        let url = '/api/v1/putWithBody'
        api1.put(url, {
            username: 'dexterleslie',
            password: '123456',
            verificationCode: '111111'
        }, {
            // 表示这个请求是否需要在request拦截器中自动添加header1参数
            needHeader: true,
            // 使用params传递参数
            params: { param1: '+' }
        }).then(function (response) {
            alert(`调用接口${url}成功，服务器返回：${response}`)
        }).catch(function (error) {
            let errorCode = error.errorCode
            let errorMessage = error.errorMessage
            let httpStatus = error.httpStatus
            alert(`调用接口${url}失败，错误代码：${errorCode}，错误原因：${errorMessage}，http状态：${httpStatus}`)
        }).finally(function () {

        })
    })

    $('#btnPostWithBody').click(function () {
        // 测试post方法提交body参数
        let url = '/api/v1/postWithBody'
        api1.post(url, {
            username: 'dexterleslie',
            password: '123456',
            verificationCode: '111111'
        }, {
            // 表示这个请求是否需要在request拦截器中自动添加header1参数
            needHeader: true,
            // 使用params传递参数
            params: { param1: '+' }
        }).then(function (response) {
            alert(`调用接口${url}成功，服务器返回：${response}`)
        }).catch(function (error) {
            let errorCode = error.errorCode
            let errorMessage = error.errorMessage
            let httpStatus = error.httpStatus
            alert(`调用接口${url}失败，错误代码：${errorCode}，错误原因：${errorMessage}，http状态：${httpStatus}`)
        }).finally(function () {

        })
    })

    $('#btnDelete').click(function () {
        // 测试单独配置实例
        api1.delete("api/v1/delete", {
            needHeader: true,
            params: { param1: "deleteObjectId#1111" }
        }).then((data) => {
            alert(data)
        }).catch(function (error) {
            alert(error.errorMessage)
        })
    })

    $('#btnGlobalAxios').click(function () {
        // get或者post携带http header参数
        // https://blog.csdn.net/qq_43225030/article/details/92810393

        // 测试全局配置
        axios.get("api/v1/get", {
            needHeader: true,
            params: { param1: "Dexterleslie0" },
            headers: { header2: 'my-header2' }
        }).then((data) => {
            alert(data)
        }).catch(function (error) {
            alert(error.errorMessage)
        })
    })

    $('#btnConcurrent').click(function () {
        // 测试并发请求
        axios.all([
            axios.get("api/v1/get", {
                needHeader: true,
                params: { param1: "Dexterleslie0" }
            }),
            axios.get("api/v1/get", {
                needHeader: true,
                params: { param1: "Dexterleslie1" }
            })
        ]).then((data) => {
            alert(JSON.stringify(data))
        }).catch((error) => {
            alert(JSON.stringify(error))
        }).finally(() => {
            console.log("finally回调")
        });
    })

    $('#btnGetTextPlain').click(function () {
        // 测试get text/plain返回
        api1.get('/api/v1/1.txt', {
            params: { param1: 'Dexterleslie123' },
            needHeader: true
        }).then(function (data) {
            alert('Axios获取text/plain返回：' + data)
        });
    })

    $("#btnFileUpload").click(function () {
        let fileUploadField = $("#fileUpload")
        let fileList = fileUploadField[0].files
        let formData = new FormData();
        for (let i = 0; i < fileList.length; i++) {
            formData.append('files', fileList[i])
        }
        axios.post("/api/v1/postWithFileUpload", formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }).then(function (response) {
            // alert(response)
            // 通过下面的代码可以打开一个新窗口并下载文件
            window.open("/api/v1/" + response[0], "_blank")
        }).catch(function (error) {
            alert(error.errorMessage)
        }).finally(function () {
        })
    })
})
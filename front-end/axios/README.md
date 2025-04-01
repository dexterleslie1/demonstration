# Axios



## Vue2 集成 axios

>[参考链接](https://juejin.cn/post/7080965261448183845)

参考 <a href="/vue/脚手架创建项目.html#创建-vue2" target="_blank">链接</a> 创建 Vue2 项目

安装 axios 依赖

```bash
npm install axios
```

src/main.js 集成 axios

```javascript
import Vue from 'vue'
import App from './App.vue'
import axios from 'axios'

Vue.config.productionTip = false
Vue.prototype.$axios = axios

new Vue({
  render: h => h(App),
}).$mount('#app')
```

src/App.vue 中使用 axios

```javascript
handleClick() {
    this.$axios.get("api/v1/get", {
        needHeader: true,
        params: { param1: "Dexterleslie0" },
        headers: { header1: "my-header1", header2: 'my-header2' }
    }).then((data) => {
        alert(data.data.data)
    }).catch(function (error) {
        alert(error.errorMessage)
    })
}
```



## 创建基于 webpack 的 Axios 项目

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/axios/axios-web)

安装 webpack、webpack-cli

```bash
npm install webpack@^5.36.2 webpack-cli@^4.6.0 --save-dev
```

安装 html-webpack-plugin

```bash
npm install html-webpack-plugin@^5.3.1 --save-dev
```

安装 webpack-dev-server

```bash
npm install webpack-dev-server@^3.11.2 --save-dev
```

安装 axios

```bash
npm install axios@^0.21.1 --save
```

安装 jquery

```bash
npm install jquery@1.12.4 --save
```

package.json 添加如下脚本：

```javascript
"scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "dev": "webpack serve",
    "build": "webpack"
},
```

创建 src/index.html 文件内容如下：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Axios用法</title>
</head>
<body>
    <input id="btnPutWithBody" type="button" value="测试putWithBody"/>
    <input id="btnPostWithBody" type="button" value="测试postWithBody"/>
    <input id="btnDelete" type="button" value="测试delete"/>
    <input id="btnGlobalAxios" type="button" value="测试全局Axios"/>
    <input id="btnConcurrent" type="button" value="测试并发请求"/>
    <input id="btnGetTextPlain" type="button" value="测试get text/plain返回"/>
</body>
</html>
```

创建 src/index.js 文件内容如下：

```javascript
import axios from "axios";

// 全局配置
axios.defaults.baseURL = "/";
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
        // 测试全局配置
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
        // // 测试get text/plain返回
        api1.get('/api/v1/1.txt', {
            params: { param1: 'Dexterleslie123' },
            needHeader: true
        }).then(function (data) {
            alert('Axios获取text/plain返回：' + data)
        });
    })
})
```

创建 webpack 配置文件 webpack.config.js，内容如下：

```bash
const {resolve} = require("path");

const HtmlWebpackPlugin = require("html-webpack-plugin");

module.exports = {
    entry: "./src/index.js",
    output: {
        filename: "built.js",
        path: resolve(__dirname, "build")
    },

    plugins:[
        new HtmlWebpackPlugin({
            template: "./src/index.html"
        })
    ],

    mode: "development",

    // webpack5 自动刷新浏览器
    target: "web",
    // webpack dev server配置
    devServer: {
        port: 3000,
        // 编译时使用gzip压缩
        compress: true,
        // 编译后自动打开浏览器
        open: true,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                ws: true,
                pathRewrite: {
                    '^/': '/'
                }
            }
        }
    }
}
```

编译发布

```bash
npm run build
```

运行开发者服务器

```bash
npm run dev
```



## 全局设置

### 基础设置

```javascript
// 设置基础 URL 为 /
axios.defaults.baseURL = "/";
// 设置全局超时为 5000 毫秒
axios.defaults.timeout = 5000;
```



### request 和 response 拦截器

```javascript
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
```



## 创建单独配置的 Axios 实例

```javascript
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
```



## Put 方法 Body 参数

```javascript
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
```



## Post 方法 Body 参数

```javascript
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
```



## Delete 方法

```javascript
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
```



## Get 方法

```javascript
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
```



## Header 参数

```javascript
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
```



## 并发请求

```javascript
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
```



## 返回 text/plain mine 类型

```javascript
$('#btnGetTextPlain').click(function () {
    // 测试get text/plain返回
    api1.get('/api/v1/1.txt', {
        params: { param1: 'Dexterleslie123' },
        needHeader: true
    }).then(function (data) {
        alert('Axios获取text/plain返回：' + data)
    });
})
```



## 上传和下载文件

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/axios)
>
>[参考链接](https://apifox.com/apiskills/axios-use-formdata-upload-flie/)

接口定义如下：

```java
@Slf4j
@RestController
@RequestMapping("/api/v1")
@Validated
public class ApiController {
    final static String TemporaryDirectoryPath = System.getProperty("java.io.tmpdir");
    
    /**
     * 测试使用 Axios 上传文件
     *
     * @param multipartFiles
     * @return
     * @throws IOException
     */
    @PostMapping("postWithFileUpload")
    ListResponse<String> postWithFileUpload(
            @NotNull(message = "请指定上传的文件")
            @Size(min = 1, message = "请指定上传的文件")
            @RequestParam(value = "files", required = false) MultipartFile[] multipartFiles) throws IOException {
        // 过滤空的 MultipartFile
        List<MultipartFile> temporaryMultipartList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                temporaryMultipartList.add(multipartFile);
            }
        }
        multipartFiles = temporaryMultipartList.toArray(new MultipartFile[0]);

        List<File> fileList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            // 上传时的文件名称
            String originalFilename = multipartFile.getOriginalFilename();
            // 获取文件的扩展名
            String filenameExtension = FilenameUtils.getExtension(originalFilename);
            // 在 /tmp 目录创建临时文件
            String filePath = TemporaryDirectoryPath + File.separator + UUID.randomUUID() + "." + filenameExtension;
            File fileTemporary = new File(filePath);
            boolean created = fileTemporary.createNewFile();
            if (!created) {
                if (log.isWarnEnabled()) {
                    log.warn("创建文件失败，路径 {}", fileTemporary.getAbsolutePath());
                }
                throw new BusinessException("文件转换失败！");
            }

            if (log.isDebugEnabled()) {
                log.debug("创建文件成功，原始文件 {}，新文件路径 {}", originalFilename, fileTemporary.getAbsolutePath());
            }

            multipartFile.transferTo(fileTemporary);
            fileList.add(fileTemporary);
            if (log.isDebugEnabled()) {
                log.debug("原始文件 {} 被成功转移到 {}", originalFilename, fileTemporary.getAbsolutePath());
            }
        }

        List<String> filenameList = fileList.stream().map(File::getName).collect(Collectors.toList());
        return ResponseUtils.successList(filenameList);
    }

    @GetMapping("{filename}")
    ResponseEntity downloadFile(
            @NotNull(message = "请指定下载的文件")
            @NotBlank(message = "请指定下载的文件")
            @PathVariable(value = "filename", required = false) String filename) {
        File file = new File(TemporaryDirectoryPath + File.separator + filename);
        long fileLength = file.length();

        ResponseEntity responseEntity = ResponseEntity.ok()
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(new FileSystemResource(file));
        return responseEntity;
    }
}
```

测试页面

```html
<div>使用 Axios 上传文件</div>
<div>
    <div>
        <input id="fileUpload" type="file" name="files" multiple/>&nbsp;&nbsp;
        <input id="btnFileUpload" type="button" value="上传"/>
    </div>
</div>
```

Axios 代码如下：

```javascript
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
```


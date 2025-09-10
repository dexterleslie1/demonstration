const { createProxyMiddleware } = require("http-proxy-middleware")

// https://create-react-app.dev/docs/proxying-api-requests-in-development/
module.exports = function (app) {
    app.use(
        createProxyMiddleware("/api", {
            target: "http://localhost:8080",
            // 需要修改请求头中的Host值为httpbin.org
            changeOrigin: true,
            //pathRewrite: { "^/api/v1": "" },
        })
    )
}
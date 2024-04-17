const { createProxyMiddleware } = require("http-proxy-middleware")

module.exports = function (app) {
    app.use(
        createProxyMiddleware("/api/v1", {
            target: "https://api.github.com",
            changeOrigin: true,
            pathRewrite: { "^/api/v1": "" },
        })
    )
}

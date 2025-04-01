module.exports = {
    // 配置转发代理
    devServer: {
        // 配置端口
        port: 8080,
        proxy: {
            '/chunk': {
                target: 'http://localhost:9080',
                ws: true,
                pathRewrite: {
                    '^/': '/'
                }
            }
        }
    }
}
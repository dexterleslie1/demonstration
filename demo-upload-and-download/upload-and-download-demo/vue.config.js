module.exports = {
    // 配置转发代理
    devServer: {
        // 配置端口
        port: 12346,
        proxy: {
            // upload-and-download-api
            '/api': {
                target: 'http://localhost:18080',
                ws: true,
                pathRewrite: {
                    '^/': '/'
                }
            }
        }
    }
}
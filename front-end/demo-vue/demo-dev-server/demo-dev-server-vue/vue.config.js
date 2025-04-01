const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    // 自定义 30000 端口
    // https://stackoverflow.com/questions/47219819/how-to-change-port-number-in-vue-cli-project
    port: 30000,
    proxy: {
      // /api 开头的请求转发到 http://localhost:8080 服务器
      '/api': {
        target: 'http://localhost:8080', // 后端服务器地址
        // changeOrigin: true, // 是否改变Origin头信息
        // pathRewrite: {
        //   '^/api': '' // 将/api前缀重写为空字符串
        // }
      }
    }
  }
})

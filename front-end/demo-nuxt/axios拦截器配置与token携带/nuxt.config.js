export default {
  // Global page headers: https://go.nuxtjs.dev/config-head
  head: {
    title: 'demo-test',
    htmlAttrs: {
      lang: 'en'
    },
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: '' },
      { name: 'format-detection', content: 'telephone=no' }
    ],
    link: [
      { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
    ]
  },

  // Global CSS: https://go.nuxtjs.dev/config-css
  css: [
    // 引入element-ui css
    'element-ui/lib/theme-chalk/index.css'
  ],

  // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
  plugins: [
    // 加载axios全局配置插件
    {
      src: '~/plugins/axios',
      ssr: true
    },
    // 声明element-ui自定义插件文件
    {
      src: '~/plugins/element-ui',
      ssr: true
    }
  ],

  // Auto import components: https://go.nuxtjs.dev/config-components
  components: true,

  // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
  buildModules: [
  ],

  // Modules: https://go.nuxtjs.dev/config-modules
  modules: [
    // 添加axios
    '@nuxtjs/axios'
  ],
  axios: {
    // 启用axios代理
    proxy: true
  },
  // 匹配 /api 前缀就转发到 localhost:8080
  proxy: {
    '/api/': {
      target: 'http://localhost:8080',
      // 当 changeOrigin 设置为 true 时，代理服务器会​​将请求头中的 Host 字段修改为目标服务器（target）的地址​​，使后端服务器认为请求是直接来自客户端，而非代理服务器
      // 假设你的前端项目运行在 http://localhost:8081，后端接口部署在 http://localhost:8080，前端发送请求 /api/user，代理默认转发到 http://localhost:8080/api/user，
      // 但请求头的 Host 仍为 localhost:8081（前端地址）。此时配置 changeOrigin: true 会把 Host 从 localhost:8081 修改为 localhost:8080。
      changeOrigin: true
    }
  },

  // Build Configuration: https://go.nuxtjs.dev/config-build
  build: {
  },

  router: {
    extendRoutes(routes, resolve) {
      routes.push({
        name: "axiosSsrErrorHandling",
        path: "/axiosSsrErrorHandling",
        component: resolve(__dirname, "components/AxiosSsrErrorHandling.vue")
      })

      routes.push({
        name: "axiosCsrErrorHandling",
        path: "/axiosCsrErrorHandling",
        component: resolve(__dirname, "components/AxiosCsrErrorHandling.vue")
      })
    }
  }
}

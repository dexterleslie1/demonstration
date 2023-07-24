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
      changeOrigin: true
    }
  },

  // Build Configuration: https://go.nuxtjs.dev/config-build
  build: {
  },

  router: {
    extendRoutes(routes, resolve) {
      routes.push({
        name: "pageLevelCatchErrorHandling",
        path: "/pageLevelCatchErrorHandling",
        component: resolve(__dirname, "components/PageLevelCatchErrorHandling.vue")
      })

      routes.push({
        name: "pageLevelThrownByAxiosInterceptorErrorHandling",
        path: "/pageLevelThrownByAxiosInterceptorErrorHandling",
        component: resolve(__dirname, "components/PageLevelThrownByAxiosInterceptorErrorHandling.vue")
      })

      routes.push({
        name: "axiosCsrAndSsrErrorHandling",
        path: "/axiosCsrAndSsrErrorHandling",
        component: resolve(__dirname, "components/AxiosCsrAndSsrErrorHandling.vue")
      })
    }
  }
}

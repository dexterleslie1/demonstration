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
  ],

  // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
  plugins: [
  ],

  // Auto import components: https://go.nuxtjs.dev/config-components
  components: [
    '~/components',
    { path: '~/components/sub', extensions: ['vue'] }
  ],

  // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
  buildModules: [
  ],

  // Modules: https://go.nuxtjs.dev/config-modules
  modules: [
  ],

  // Build Configuration: https://go.nuxtjs.dev/config-build
  build: {
  },

  router: {
    extendRoutes(routes, resolve) {
      routes.push({
        name: "component1",
        path: "/component1",
        component: resolve(__dirname, "components/sub/Component1.vue")
      })

      routes.push({
        name: "component2",
        path: "/component2",
        component: resolve(__dirname, "components/sub/Component2.vue")
      })

      routes.push({
        name: "component3",
        path: "/component3",
        component: resolve(__dirname, "components/Component3.vue")
      })
    }
  }
}

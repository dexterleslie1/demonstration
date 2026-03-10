import App from './App'
// 启用UniDevTools调试工具
import devTools from './devTools/index.js'
// 启用UniDevTools调试工具
import devToolsConfig from './devTools/config.js'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
// 启用UniDevTools调试工具
devTools.install(Vue, devToolsConfig)
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  // 启用UniDevTools调试工具
  devTools.install(app, devToolsConfig)
  return {
    app
  }
}
// #endif
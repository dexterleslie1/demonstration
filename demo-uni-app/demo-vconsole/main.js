import App from './App'
// 集成HF调试器
import { HFdebugging } from '@/uni_modules/HF-HF_debugging/common/next.js'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// 集成HF调试器
new HFdebugging()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  // 集成HF调试器
  new HFdebugging({ app })
  return {
    app
  }
}
// #endif
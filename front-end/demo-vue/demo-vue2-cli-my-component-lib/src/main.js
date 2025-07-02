import Vue from 'vue'
import App from './App.vue'
import MyComponentPlugin from "./index"

// 注册自定义组件库到 Vue 中
Vue.use(MyComponentPlugin)

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')

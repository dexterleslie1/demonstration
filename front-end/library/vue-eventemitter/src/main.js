import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

Vue.config.productionTip = false

// 初始化eventemitter3
import EventEmitter3 from 'eventemitter3'
Vue.prototype.$eventbus = new EventEmitter3

new Vue({
  router,
  store,
  render: function (h) { return h(App) }
}).$mount('#app')

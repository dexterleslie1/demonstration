import Vue from 'vue'
import App from './App.vue'

import axios from './axios'

Vue.prototype.$axios = axios

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.use(ElementUI)

Vue.config.productionTip = false

new Vue({
  render: function (h) { return h(App) },
}).$mount('#app')

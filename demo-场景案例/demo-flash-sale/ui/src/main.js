import Vue from 'vue'
import App from './App.vue'
import router from './router'; // 引入路由实例
import axios from './axios'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.config.productionTip = false
Vue.prototype.$axios = axios

Vue.use(ElementUI)

new Vue({
  router, // 挂载路由
  render: function (h) { return h(App) },
}).$mount('#app')

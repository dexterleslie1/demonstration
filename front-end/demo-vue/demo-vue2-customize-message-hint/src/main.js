import Vue from 'vue'
import App from './App.vue'
import MessagePlugin from './plugins/message'; // 引入消息插件

// 注册插件（全局生效）
Vue.use(MessagePlugin, {
  duration: 3000 // 可选：设置默认持续时间
});

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')

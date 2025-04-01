import Vue from 'vue'
import App from './App.vue'
import uploader from 'vue-simple-uploader'
import ElementUI from 'element-ui'

Vue.config.productionTip = false
Vue.use(uploader)
Vue.use(ElementUI)

new Vue({
  render: function (h) { return h(App) },
}).$mount('#app')

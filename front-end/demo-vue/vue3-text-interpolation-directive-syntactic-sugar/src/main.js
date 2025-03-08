import { createApp } from 'vue';
import App from './App.vue';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 导入App.vue组件，挂载到index.html的id=app dom中
createApp(App).use(ElementPlus).mount('#app')

import { createApp } from 'vue';
import App from './App.vue';

let elementDiv = document.createElement('div')
elementDiv.id = 'app'
document.body.appendChild(elementDiv)

createApp(App).mount('#app');
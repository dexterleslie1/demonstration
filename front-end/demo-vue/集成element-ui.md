# Element-UI



## Vue2 集成 Element-UI

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/vue2-using-element-ui)

参考本站 <a href="/vue/脚手架创建项目.html#创建-vue2" target="_blank">链接</a> 初始化 Vue2 项目

添加 Element-UI 依赖

```bash
npm install --save element-ui
```

配置 Vue2 集成 Element-UI，在 main.js 添加以下内容：

```javascript
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.use(ElementUI)
```

启动应用

```bash
npm run serve
```



## Vue3 集成 Element-Plus

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/demo-vue3-element-plus-integration)

参考本站 <a href="/vue/脚手架创建项目.html#创建-vue3" target="_blank">链接</a> 初始化 Vue3 项目

添加 Element-Plus 依赖

```bash
npm install -save element-plus --registry=https://registry.npmmirror.com
```

配置 Vue3 集成 Element-Plus，在 main.js 添加以下内容：

```javascript
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

createApp(App).use(ElementPlus).mount('#app')
```

启动应用

```bash
npm run serve
```


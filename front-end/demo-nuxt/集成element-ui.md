# 集成 Element-UI

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-nuxt/element-ui%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AE)

安装 Element-UI 依赖

```bash
npm install element-ui --save
```

nuxt.config.js 中配置如下：

```javascript
css: [
  // 引入element-ui css
  'element-ui/lib/theme-chalk/index.css'
],

plugins: [
    // 声明element-ui自定义插件文件
    {
      src: '~/plugins/element-ui',
      ssr: true
    }
  ],
```

plugins 目录中创建文件 element-ui.js 内容如下：

```javascript
import Vue from 'vue'
import ElementUI from 'element-ui'

Vue.use(ElementUI)
```

在页面中加入 el-button 测试

```html
<el-button type="primary">Primary</el-button>
```


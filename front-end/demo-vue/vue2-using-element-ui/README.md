# vue2-using-element-ui



## 配置集成element-ui步骤

```
# 使用vue-cli初始化vue2项目

# 添加element-ui依赖
yarn add element-ui

# 配置vue集成element-ui，在main.js添加如下内容:
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.use(ElementUI)

# 启动应用
yarn serve
```



## Project setup
```
yarn install
```

### Compiles and hot-reloads for development
```
yarn serve
```

### Compiles and minifies for production
```
yarn build
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

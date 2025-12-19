## 概念

>官网地址：https://vant-ui.github.io/vant/v2/#/zh-CN/home

Vant 是一个**轻量、可靠的移动端组件库**，于 2017 年开源。

目前 Vant 官方提供了 [Vue 2 版本](https://vant-contrib.gitee.io/vant/v2)、[Vue 3 版本](https://vant-contrib.gitee.io/vant)和[微信小程序版本](http://vant-contrib.gitee.io/vant-weapp)，并由社区团队维护 [React 版本](https://github.com/3lang3/react-vant)和[支付宝小程序版本](https://github.com/ant-move/Vant-Aliapp)。

## 和Vue2项目集成

>说明：Vant2只能和Vue2集成。
>
>详细指引请参考官方链接：https://vant-ui.github.io/vant/v2/#/zh-CN/quickstart
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-vant/demo-getting-started

创建Vue2项目

安装Vant2依赖

```sh
# Vue 2 项目，安装 Vant 2：
npm i vant@latest-v2 -S
```

Vant 支持一次性导入所有组件，引入所有组件会增加代码包体积，因此不推荐这种做法。

```javascript
import Vue from 'vue';
import Vant from 'vant';
import 'vant/lib/index.css';

Vue.use(Vant);
```

App.vue如下：

```vue
<template>
  <div id="app">
    <van-button type="primary">主要按钮</van-button>
    <van-button type="info">信息按钮</van-button>
    <van-button type="default">默认按钮</van-button>
    <van-button type="warning">警告按钮</van-button>
    <van-button type="danger">危险按钮</van-button>
  </div>
</template>

<script>

export default {
  name: 'App',
  components: {
  }
}
</script>

<style>
#app {
}
</style>

```

启动项目测试

```sh
npm run serve
```


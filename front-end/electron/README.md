## 创建第一个 electron 应用

参考

https://juejin.cn/post/7058637067718246431
demo-electron-vue 演示

安装 vue-cli

```sh
npm install -g @vue/cli
```

创建一个 vue 基础项目(取消 babel 和 eslint 选择)

```sh
vue create demo-electron-vue
```

安装 vue-cli-plugin-electron-builder 插件，选择 electron 13.0.0 版本

```sh
vue add electron-builder
```

启动 electron+vue 项目，NOTE：启动过程中会拉取**vue-devtools**的浏览器调试插件，这个时候你如果没有使用科学的方式上网将会出现，这时候如果你可以使用科学的方式来下载那更好，毕竟做开发还是要会的，如果暂时不方便就`src/background.js`中的`await installExtension(VUEJS_DEVTOOLS)`暂时注释掉并将项目重新启动一次。

```sh
npm run electron:serve
```


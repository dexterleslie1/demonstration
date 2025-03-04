# Vite

>[Vite5 官方网站](https://v5.vite.dev/)
>
>[Vite2 官方网站](https://v2.vite.dev/)



## 介绍

Vite（法语意为 "快速的"，发音 `/vit/`，发音同 "veet"）是一种新型前端构建工具，能够显著提升前端开发体验。它主要由两部分组成：

- 一个开发服务器，它基于 [原生 ES 模块](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Modules) 提供了 [丰富的内建功能](https://cn.vitejs.dev/guide/features.html)，如速度快到惊人的 [模块热替换（HMR）](https://cn.vitejs.dev/guide/features.html#hot-module-replacement)。
- 一套构建指令，它使用 [Rollup](https://rollupjs.org) 打包你的代码，并且它是预配置的，可输出用于生产环境的高度优化过的静态资源。

Vite 是一种具有明确建议的工具，具备合理的默认设置。您可以在 [功能指南](https://cn.vitejs.dev/guide/features.html) 中了解 Vite 的各种可能性。通过 [插件](https://cn.vitejs.dev/guide/using-plugins.html)，Vite 支持与其他框架或工具的集成。如有需要，您可以通过 [配置部分](https://cn.vitejs.dev/config/) 自定义适应你的项目。

Vite 还提供了强大的扩展性，可通过其 [插件 API](https://cn.vitejs.dev/guide/api-plugin.html) 和 [JavaScript API](https://cn.vitejs.dev/guide/api-javascript.html) 进行扩展，并提供完整的类型支持。

你可以在 [为什么选 Vite](https://cn.vitejs.dev/guide/why.html) 部分深入了解该项目的设计理念。



## 创建项目

在目录 test1 创建一个新的 Vite 项目

```bash
npm create vite@latest test1
```

```bash
# 上面 npm create 命令输出
~/workspace-git/temp » npm create vite@latest test1
Need to install the following packages:
create-vite@6.3.1
Ok to proceed? (y) 
│
◇  Select a framework:
│  Vue
│
◇  Select a variant:
│  JavaScript
│
◇  Scaffolding project in /home/dexterleslie/workspace-git/temp/test1...
│
└  Done. Now run:

  cd test1
  npm install
  npm run dev

```

初始化项目依赖

```bash
npm install --registry=https://registry.npmmirror.com
```

运行项目

```bash
npm run dev
```



## 为现有项目添加 Vite 支持

进入项目目录后，执行以下命令为项目添加 Vite 支持

```bash
npm install -D vite --registry=https://registry.npmmirror.com
```

并创建一个像这样的 `index.html` 文件：

```html
<p>Hello Vite!</p>
```

然后在终端上运行相应的命令：

```bash
npx vite
```

之后就可以在 `http://localhost:5173` 上访问 `index.html`。

`package.json` 中配置 `vite` 命令行：

```json
{
  "scripts": {
    "dev": "vite", // 启动开发服务器，别名：`vite dev`，`vite serve`
    "build": "vite build", // 为生产环境构建产物
    "preview": "vite preview" // 本地预览生产构建产物
  }
}
```

配置 `vite` 命令行后即可使用以下命令启动 `vite` 支持的项目

```bash
npm run dev
```


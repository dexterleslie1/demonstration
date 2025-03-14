# Nuxt



## 介绍

Nuxt.js 是一个基于 Vue.js 的高级框架，用于构建现代且富交互性的 Web 应用。它扩展了 Vue.js 的功能，提供了服务器渲染（Server-Side Rendering, SSR）、静态站点生成（Static Site Generation, SSG）等功能，使得开发者能够更容易地创建快速、性能优异的 Web 应用。下面是一些关于 Nuxt.js 的关键点：

1. **基于 Vue.js**：Nuxt.js 是建立在 Vue.js 之上的，因此它完全兼容 Vue.js 的生态系统和组件库。如果你已经熟悉 Vue.js，那么上手 Nuxt.js 会非常容易。
2. **服务器渲染（SSR）**：Nuxt.js 支持服务器渲染，这意味着在服务器端预先渲染页面内容，然后将已经渲染好的 HTML 发送到客户端。这样做的好处是可以提高 SEO（搜索引擎优化）性能，因为搜索引擎爬虫可以直接抓取到页面的内容，同时也减少了客户端的渲染时间，提高了用户体验。
3. **静态站点生成（SSG）**：除了 SSR，Nuxt.js 还支持静态站点生成。这意味着在构建阶段，Nuxt.js 会预先生成整个网站的静态 HTML 文件。这种方式非常适合内容变化不频繁的网站，如博客、文档网站等。生成的静态文件可以被部署到任何静态文件服务器上，如 Netlify、Vercel 等，从而实现极高的性能和安全性。
4. **模块化开发**：Nuxt.js 提供了模块化的开发方式，允许开发者通过添加或创建模块来扩展应用的功能。Nuxt.js 社区和官方都提供了丰富的模块，如用于身份验证的 `@nuxtjs/auth-next` 模块，用于 PWA（渐进式 Web 应用）支持的 `@nuxtjs/pwa` 模块等。
5. **自动代码分割**：Nuxt.js 利用了 Webpack 的代码分割功能，自动将代码分割成小块，按需加载。这有助于减少初始加载时间，提高应用的性能。
6. **路由管理**：Nuxt.js 基于文件系统的路由管理使得路由配置变得非常简单。你只需在 `pages` 目录下创建对应的 `.vue` 文件，Nuxt.js 就会自动生成相应的路由配置。
7. **状态管理**：虽然 Nuxt.js 不强制使用任何特定的状态管理库，但它与 Vuex（Vue.js 的官方状态管理库）完美集成。你可以轻松地在 Nuxt.js 应用中使用 Vuex 来管理应用的状态。
8. **国际化支持**：Nuxt.js 提供了内置的国际化（i18n）支持，使得创建多语言网站变得更加容易。

总的来说，Nuxt.js 是一个功能强大且灵活的框架，适用于构建各种类型的 Web 应用。无论是需要高性能的电商网站，还是需要 SEO 优化的博客平台，Nuxt.js 都能提供出色的解决方案。



## Node 和 Nuxt CLI 版本兼容性

- Nuxt CLI 3 兼容 Node 14、16、18、19，但是推荐使用 Node 18.x。
- Nuxt CLI 2 兼容 Node 16.x、v20.12.2。



## 安装指定版本的 Nuxt CLI

>`https://www.npmjs.com/package/create-nuxt-app?activeTab=versions`

安装 2.15.0 版本

```bash
sudo npm install -g create-nuxt-app@2.15.0 --registry=https://registry.npmmirror.com
```



安装最新版本

```bash
sudo npm install -g create-nuxt-app --registry=https://registry.npmmirror.com
```



## 创建、运行、编译、发布项目

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-nuxt/demo-init-nuxt)



### 创建 Nuxt 2 项目

安装 nuxt CLI

```bash
sudo npm install -g create-nuxt-app@2.15.0 --registry=https://registry.npmmirror.com
```

查看 nuxt CLI 版本

```bash
create-nuxt-app --version
```

在当前目录创建项目

```bash
create-nuxt-app .
```

启动项目

```bash
npm run dev
```



### 创建 Nuxt 3 项目

安装 nuxt CLI

```bash
sudo npm install -g create-nuxt-app@3.7.1 --registry=https://registry.npmmirror.com
```

查看 nuxt CLI 版本

```bash
create-nuxt-app --version
```

在当前目录创建项目

```bash
create-nuxt-app .
```

项目选项如下：

```bash
create-nuxt-app v3.7.1
✨  Generating Nuxt.js project in .
? Project name: test1
? Programming language: JavaScript
? Package manager: Npm
? UI framework: None
? Nuxt.js modules: (Press <space> to select, <a> to toggle all, <i> to invert selection)
? Linting tools: (Press <space> to select, <a> to toggle all, <i> to invert selection)
? Testing framework: None
? Rendering mode: Universal (SSR / SSG)
? Deployment target: Server (Node.js hosting)
? Development tools: (Press <space> to select, <a> to toggle all, <i> to invert selection)
? What is your GitHub username? dexterleslie@gmail.com
? Version control system: None

🎉  Successfully created project test1

  To get started:

        npm run dev

  To build & start for production:

        npm run build
        npm run start

```

启动项目

```bash
npm run dev
```



### 编译和发布项目

在生产环境编译和发布项目

```bash
npm run build && npm run start
```



### 开发环境运行已有的项目

下载相关依赖包括 nuxt CLI

```sh
npm install
```

开发模式运行项目

```sh
npm run dev
```



## Vite+Nuxt

>提醒：尝试根据 [链接](https://vite.nuxtjs.org/getting-started/installation) 在 Nuxt 项目中配置 Vite，但失败。



## npm run generate 和 npm run build、npm run start 区别

在 Nuxt.js 中，`npm run generate`、`npm run build` 和 `npm run start` 是三个常用的命令，它们各自用于不同的场景和目的。以下是这三个命令的区别：

1. npm run generate

- **用途**：用于构建静态生成的应用（Static Site Generation, SSG）。
- **执行结果**：此命令会预渲染所有页面，并生成静态 HTML 文件。执行后，会生成 `dist/` 目录，其中包含静态生成的应用的所有文件，如 HTML、JavaScript 和 CSS 文件。
- **服务器依赖**：生成的静态站点不需要 Node.js 服务器来运行，可以部署在任何能够托管静态文件的服务上，如 Netlify、Vercel 或 GitHub Pages。
- **常用场景**：适用于内容不经常更改的网站，或希望应用部署简单且没有服务器端渲染需求的情况。

2. npm run build

- **用途**：用于构建服务器端渲染（Server-Side Rendering, SSR）应用或单页面应用（Single-Page Application, SPA）。
- **执行结果**：此命令主要用于准备应用在服务器上运行。执行后，会生成 `.nuxt` 目录，其中包含用于服务器端渲染的所有资源。
- **服务器依赖**：构建的结果需要一个 Node.js 服务器来运行，因为服务器端渲染的特性使得应用部分或全部页面在服务器上生成。
- **常用场景**：适用于希望应用具有更好的 SEO 和首屏渲染性能的情况。SSR 应用需要后端服务器来处理渲染。

3. npm run start

- **用途**：用于启动已经构建好的 Nuxt.js 应用。
- **执行结果**：此命令会启动一个 Node.js 服务器，并加载 `.nuxt` 目录中的资源来运行应用。
- **服务器依赖**：需要 Node.js 和 npm（或 yarn）环境来运行。
- **常用场景**：在开发过程中，可以使用 `npm run dev` 命令来启动开发服务器，它会自动监听文件变化并重新构建应用。而在生产环境中，通常先使用 `npm run build` 命令构建应用，然后再使用 `npm run start` 命令启动服务器来运行应用。

总结

- `npm run generate` 适用于静态站点生成，生成的文件可以部署在任何能够托管静态文件的服务上。
- `npm run build` 适用于需要服务器端渲染的应用（SSR 或 SPA），生成的文件需部署在 Node.js 环境。
- `npm run start` 用于启动已经构建好的 Nuxt.js 应用，需要 Node.js 服务器来运行。

根据你的具体需求和部署环境，可以选择适合的命令来构建和启动 Nuxt.js 应用。



## 项目目录结构

Nuxt.js 是一个基于 Vue.js 的高级框架，用于构建服务器端渲染（SSR）或静态站点生成（SSG）的应用。Nuxt.js 提供了一个清晰且组织良好的目录结构，以帮助开发者更好地组织和管理项目代码。以下是 Nuxt.js 项目目录结构的详细解析：

### 1. 根目录

- **`my-nuxt-project/`**：这是你的 Nuxt.js 项目的根目录，通常包含项目的所有文件和文件夹。

### 2. 核心目录

- **`assets/`**：用于存放未编译的静态资源，如图片（.jpg, .png 等）、字体（.woff, .ttf 等）和样式文件（如 .scss, .sass 文件）。这些文件可以通过路径引用或在 Vue 文件中直接引用。
- **`components/`**：用于存放 Vue 组件。这些组件可以在整个项目中复用。Nuxt.js 会自动导入这个目录中的任何组件（以及你可能正在使用的任何模块注册的组件）。
- **`composables/`**（Nuxt 3 特有）：用于存放 Vue 3 的组合式 API 函数（即 hooks）。这些顶级文件会被 Nuxt.js 自动导入，并在整个项目中使用。
- **`content/`**（可选）：用于 Nuxt Content 模块存储内容。如果启用了 Nuxt Content 模块，你可以在这个目录中放置 .md、.yml、.csv 和 .json 文件，Nuxt Content 模块会解析这些文件并为你的应用程序创建一个基于文件的 CMS。
- **`layouts/`**：用于存放页面布局组件。布局组件定义了页面的整体结构，如头部、主体和底部。你可以为不同的页面指定不同的布局。
- **`middleware/`**：用于存放中间件。中间件是在导航到特定路由之前运行的代码，适用于权限验证、重定向等操作。
- **`pages/`**：用于存放 Vue 页面组件。Nuxt.js 会自动读取这个目录中的文件，并根据文件夹和文件名生成路由，无需手动配置。
- **`plugins/`**：用于存放插件。插件用于扩展 Nuxt.js 的功能，如注册全局组件、全局指令或全局方法。
- **`server/`**（Nuxt 3 特有）：用于存放服务器端代码，如 API 路由、服务端渲染等。
- **`store/`**：用于存放状态管理文件。你可以使用 Pinia 或 Vuex 来管理应用的状态。

### 3. 配置和脚本文件

- **`nuxt.config.ts`**：Nuxt.js 的配置文件。你可以在这个文件中配置 Nuxt.js 的各种特性和模块，如路由、插件、服务器设置等。
- **`package.json`**：项目的依赖和脚本文件。在这个文件中，你可以定义项目的依赖（如 vue, nuxt 等），以及构建、开发时使用的脚本。
- **`tsconfig.json`**（如果使用 TypeScript）：TypeScript 的配置文件。

### 4. 其他文件

- **`.gitignore`**：Git 忽略文件配置。这个文件指定了哪些文件或目录应该被 Git 忽略，不包含在版本控制中。
- **`public/`**：用于存放静态文件，这些文件会直接暴露在服务器上。例如，你可以在这个目录中放置 favicon.ico 或 robots.txt 文件。

### 5. 运行时生成的文件

- **`.nuxt/`**：这是 Nuxt.js 在运行时生成的文件和目录的存放位置。通常，你不需要手动修改这个目录中的内容。
- **`.output/`**（生产构建时生成）：当使用 `npm run build` 命令为生产构建应用程序时，Nuxt.js 会创建这个目录来存放构建后的文件。

### 总结

Nuxt.js 的目录结构清晰且易于理解，它帮助开发者更好地组织和管理项目代码。通过遵循这个目录结构，你可以确保你的项目具有良好的可维护性和可扩展性。


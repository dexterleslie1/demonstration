# vuepress 使用

## 环境配置

vuepress 需要 nodejs v18.16.0+，nodejs v15.14.0 不能启动 vuepress。



## 第一个 vuepress 入门例子

### 参考

vuepress 官方文档 [Gettting Started](https://v2.vuepress.vuejs.org/guide/getting-started.html#getting-started)

### 创建 vuepress 入门例子

创建并切换到新目录

```sh
mkdir demo-getting-started
cd demo-getting-started
```

初始化项目

```sh
npm init
```

安装 vuepress

```sh
# 安装 vuepress
npm install -D vuepress@next
# 安装 bundler and theme
npm install -D @vuepress/bundler-vite@next @vuepress/theme-default@next
```

创建 docs 和 docs/.vuepress 目录

```sh
mkdir docs
mkdir docs/.vuepress
```

创建 vuepress 配置文件 docs/.vuepress/config.js

```typescript
import { viteBundler } from '@vuepress/bundler-vite'
import { defaultTheme } from '@vuepress/theme-default'
import { defineUserConfig } from 'vuepress'

export default defineUserConfig({
  bundler: viteBundler(),
  theme: defaultTheme(),
})
```

创建第一个 markdown 文件 docs/README.md

```markdown
# Hello VuePress
```

在设置完成后，项目结构如下：

```
.
├── docs
│   ├── README.md
│   └── .vuepress
│       └── config.js
├── package.json
└── package-lock.json
```

添加一下 scripts 到 package.json 中

```json
{
  "scripts": {
    "docs:dev": "vuepress dev docs",
    "docs:build": "vuepress build docs"
  }
}
```

开发模式启动项目

```sh
npm run docs:dev
```

打开浏览器访问 http://localhost:8080/ 即可查看效果

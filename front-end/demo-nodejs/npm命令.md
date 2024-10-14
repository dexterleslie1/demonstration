# `npm`命令



## `npm create`

`create`命令是`init`命令的别名，`npm init`等同于`npm create`。



## `npm init`



### 使用`npm init`命令初始化项目

初始化空`nodejs`项目（生成`package.json`文件）

```bash
npm init
```

添加`index.mjs`文件内容如下：

```javascript
// NOTE: 文件只有命名为index.mjs才能够使用 yarn test 运行，
// 否则报告 "Cannot use import statement outside a module" 错误

import moment from "moment";

console.log(`当前时间: ${moment().format("YYYY-MM-DD HH:mm:ss")}`)
```

添加`monent`开发环境依赖

```bash
npm install moment --save-dev
```

编辑`package.json`文件最终内容如下：

```json
{
  "name": "demo-nodejs-project-init-by-using-npm",
  "version": "1.0.0",
  "description": "``` # 初始化空nodejs项目(生成package.json文件) ```",
  "main": "index.js",
  "scripts": {
    "test": "node ./index.mjs"
  },
  "author": "",
  "license": "ISC",
  "devDependencies": {
    "moment": "^2.29.4"
  }
}

```

运行项目

```bash
node index.mjs
```



### `npm init <initializer>`

> [链接](https://docs.npmjs.com/cli/v6/commands/npm-init)

`npm init <initializer>`可用于设置新的或现有的`npm`包。

在这种情况下，`initializer`是一个名为`create-<initializer>`的`npm`包，它将由`npx`安装，然后执行其主`bin`——大概是创建或更新`package.json`并运行任何其他与初始化相关的操作。

`init`命令转换为相应的`npx`操作，如下所示：

- `npm init foo` -> `npx create-foo`
- `npm init @usr/foo` -> `npx @usr/create-foo`
- `npm init @usr` -> `npx @usr/create`

任何其他选项都将直接传递给命令，因此`npm init foo --hello`将映射到`npx create-foo --hello`。

如果省略初始化程序（只需调用`npm init`），`init`将恢复为旧的`init`行为。它会问你一堆问题，然后为你写一个`package.json`。它将尝试根据现有字段、依赖项和所选选项做出合理的猜测。它是严格附加的，因此它将保留已设置的任何字段和值。您还可以使用`-y/--yes`完全跳过问卷调查。如果您传递`--scope`，它将创建一个范围包。

使用`create-react-app`创建一个基于`React`的新项目：

```bash
npm init react-app ./my-react-app
```

使用`create-esm`创建一个与`esm`兼容的新包：

```bash
mkdir my-esm-lib && cd my-esm-lib
npm init esm --yes
```


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



## `npm`和`npx`区别

> [npm vs npx — What’s the Difference?](https://www.freecodecamp.org/news/npm-vs-npx-whats-the-difference/)

`npm`（`node`包管理器）是安装`Node.js`时随附的依赖项/包管理器。它为开发人员提供了一种全局和本地安装包的方法。

有时您可能想查看特定包并尝试一些命令。但如果不在本地`node_modules`文件夹中安装依赖项，则无法执行此操作。

这就是`npx`发挥作用的地方。

`npm`本身不运行任何包。如果要使用`npm`运行包，则必须在`package.json`文件中指定该包。

当可执行文件通过`npm`软件包安装时，`npm`会创建指向它们的链接：

- 本地安装在`./node_modules/.bin/`目录中创建链接
- 全局安装在全局`bin/`目录中创建链接（例如：`Linux`上的`/usr/local/bin`或`Windows`上的`%AppData%/npm`）

要使用`npm`执行包，您必须输入本地路径，如下所示：

```bash
./node_modules/.bin/your-package
```

或者您可以通过将其添加到`package.json`文件中的脚本部分来运行本地安装的包，如下所示：

```json
{
  "name": "your-application",
  "version": "1.0.0",
  "scripts": {
    "your-package": "your-package"
  }
}

```

然后您可以使用`npm run`运行脚本：

```bash
npm run your-package
```

您可以看到，使用普通的`npm`运行包需要相当多的手续。

幸运的是，这正是`npx`派上用场的地方。

您可以运行以下命令来查看`npx`是否已安装在您当前的`npm`版本中：

```bash
which npx
```

如果没有，你可以像这样安装：

```bash
npm install -g npx
```

如果您希望执行本地安装的包，您只需输入：

```bash
npx your-package
```

`npx`将检查`<command>`或`<package>`是否存在于`$PATH`或本地项目二进制文件中，如果存在，则执行它。

另一个主要优点是能够执行以前未安装的包。

让我们通过运行来测试一下：

```bash
npx cowsay wow
```

这很棒，因为有时您只想使用一些`CLI`工具，但不想为了测试而全局安装它们。

这意味着您可以节省一些磁盘空间，只在需要时运行它们。这也意味着您的全局变量将受到更少的污染。

让我们使用`npx`尝试`create-react-app`的下一个`dist`标签，它将在沙盒目录中创建应用程序。

```bash
npx create-react-app@next sandbox
```

`npx`将临时安装`create-react-app`的下一个版本，然后执行它来搭建应用程序并安装其依赖项。

安装完成后，我们可以像这样导航到该应用程序：

```bash
cd sandbox
```

然后使用以下命令启动它：

```bash
npm start
```

它将自动在您的默认浏览器窗口中打开`React`应用程序。 现在我们有一个在`create-react-app`包的下一个版本上运行的应用程序！



## `npm install`

- 调试`npm install`

  >https://dev.to/matheusabr/enhance-your-npm-installations-with-loglevel-26cc

  ```bash
  npm install --verbose
  ```

- npm install 时指定 registry

  >`https://stackoverflow.com/questions/35622933/how-to-specify-registry-while-doing-npm-install-with-git-remote-url`

  ```bash
  sudo npm install -g create-nuxt-app@2.15.0 --registry=https://registry.npmmirror.com
  ```

  

## 概念

>说明：
>
>- **Grunt**：诞生于 2012 年，曾是前端构建的主流工具，但随着 Gulp（2013 年）和 Webpack（2014 年）的崛起，逐渐被替代。目前社区活跃度下降，仅维护旧项目。
>- **Gulp**：曾是最流行的前端构建工具（2015-2018 年），但现代前端工程化转向 Webpack、Vite 等“模块化打包工具”（更适合 SPA）。不过，Gulp 仍在传统多页应用（MPA）或需要高度自定义流程的场景中使用。

Grunt 是一个 **基于 Node.js 的 JavaScript 任务运行器（Task Runner）**，核心作用是**自动化前端/Node.js 项目中的重复性工作流程**，帮开发者从“手动重复操作”中解放出来，提升效率。

### 1. 本质：解决“重复劳动”的工具

在前端开发中，我们常做很多机械性重复的事：

- 压缩 JS/CSS/图片（减小文件体积）；

- 将 Sass/Less 编译成 CSS；

- 合并多个 JS 文件为一个（减少 HTTP 请求）；

- 检查代码语法错误（如 ESLint）；

- 运行单元测试/端到端测试；

- 复制文件、清空目录（如打包前清理旧文件）……

这些事如果手动做，不仅费时间，还容易出错。**Grunt 的作用就是把这些操作写成“自动化任务”，一键执行或监听文件变化自动触发**。

### 2. 核心机制：“配置式”定义任务

Grunt 的使用依赖两个关键部分：

- **`package.json`**：管理项目依赖（包括 Grunt 本身和插件）；

- **`Gruntfile.js`**：Grunt 的“配置文件”，在这里**定义任务和配置插件**（核心）。

它的逻辑是“**插件 + 配置**”：Grunt 本身只提供任务运行的框架，具体功能靠**社区插件**实现（比如压缩 JS 用 `grunt-contrib-uglify`，编译 Sass 用 `grunt-sass`）。你只需在 `Gruntfile.js`里告诉 Grunt：“我要用什么插件，处理哪些文件，输出到哪里”。

### 3. 举个例子：用 Grunt 压缩 JS

假设你想把 `src/js`下的所有 JS 文件压缩到 `dist/js`：

1. 安装 Grunt 和压缩插件：

   ```
   npm install grunt grunt-contrib-uglify --save-dev
   ```
   
2. 在 `Gruntfile.js`中配置任务：

   ```
   module.exports = function(grunt) {
     // 初始化配置
     grunt.initConfig({
       // 配置 uglify 插件（压缩 JS）
       uglify: {
         target: {
           files: {
             'dist/js/app.min.js': ['src/js/*.js'] // 输入：src/js 下所有 JS → 输出：dist/js/app.min.js
           }
         }
       }
     });
   
     // 加载插件
     grunt.loadNpmTasks('grunt-contrib-uglify');
   
     // 注册默认任务（执行 grunt 命令时默认跑这个）
     grunt.registerTask('default', ['uglify']);
   };
   ```
   
3. 运行 `grunt`命令，Grunt 会自动把 `src/js`的 JS 压缩成 `dist/js/app.min.js`。

### 4. 常见使用场景

- **文件处理**：压缩、合并、拷贝、删除文件；

- **预编译**：Sass/Less → CSS、TypeScript → JavaScript、Vue 单文件组件编译；

- **代码质量**：ESLint 检查 JS 语法、Stylelint 检查 CSS 规范；

- **自动化测试**：配合 Mocha、Jasmine 运行测试用例；

- **开发辅助**：监听文件变化（`grunt-contrib-watch`），修改代码后自动重新编译/刷新浏览器。

### 5. 现状：依然可用，但不再是“主流”

Grunt 是**早期前端工程化的代表工具**（2010s 流行），现在虽然还有很多老项目在用，但随着 Webpack、Vite 等“构建工具”（不仅能跑任务，还能做模块打包、热更新等更复杂的事）崛起，Grunt 的地位逐渐被替代。不过，它的“配置简单、上手快”的特点，仍适合小型项目或需要轻量自动化的场景。

总结：Grunt 是一个**“用配置代替手动”的自动化任务工具**，核心价值是简化前端重复工作，让开发者更专注于写业务代码。

## 总结

Grunt通过读取Gruntfile.js中的配置执行相应任务，包括：JavaScript压缩和合并、Less文件编译为CSS、CSS文件压缩、HTML压缩、HTML资源加载路径修改等。通过命令grunt（执行default任务）、grunt less:development（执行less:development任务，less为任务，development为目标）、grunt dev（执行dev任务）、grunt production（执行production任务）执行相关任务并自动调用相应的插件。

详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-grunt/demo-getting-started

示例的使用方法：

- 开发环境一次性编译

  ```sh
  npm run build
  ```

- 启动开发环境，修改HTML、LESS、CSS、JavaScript都会触发grunt自动执行任务

  ```sh
  npm run dev
  ```

- 编译发布生产环境

  ```sh
  npm run build:prod
  ```

  
## 概念

**Gulp** 是一个基于 **Node.js** 的**前端自动化构建工具**，主要用于优化前端工作流。它通过编写简单的 JavaScript 代码（称为 Gulp 任务），自动完成重复性的开发任务，如文件压缩、编译、合并、测试等，从而提升开发效率。

------

### 核心特点

1. **代码优于配置**

   Gulp 使用纯 JavaScript 编写任务逻辑，比传统配置型工具（如 Grunt）更灵活，适合需要复杂逻辑的自动化流程。

2. **流式处理（Streams）**

   通过管道（`pipe()`）将文件流依次传递给不同的插件处理，减少磁盘 I/O 操作，执行速度更快。

3. **轻量且高效**

   仅提供基础 API，具体功能依赖丰富的第三方插件（如 `gulp-uglify`压缩 JS，`gulp-sass`编译 Sass）。

4. **易于学习和调试**

   语法简洁直观，支持实时监听文件变化并自动触发任务（热更新）。

------

### 典型应用场景

- **编译预处理语言**：Sass/Less → CSS、TypeScript/JSX → JavaScript。
- **代码压缩**：CSS、JS、HTML 压缩混淆。
- **资源优化**：图片压缩、雪碧图生成。
- **文件合并**：合并多个 JS/CSS 文件。
- **本地服务器**：搭建开发环境并自动刷新浏览器（LiveReload）。
- **代码检查**：ESLint、Stylelint 静态分析。

------

### 简单示例

以下是一个典型的 `gulpfile.js`配置：

```
const gulp = require('gulp');
const sass = require('gulp-sass')(require('sass'));
const uglify = require('gulp-uglify');
const concat = require('gulp-concat');

// 编译 Sass + 压缩 CSS
gulp.task('styles', function() {
  return gulp.src('src/scss/*.scss')
    .pipe(sass().on('error', sass.logError))
    .pipe(gulp.dest('dist/css'));
});

// 压缩 JS
gulp.task('scripts', function() {
  return gulp.src('src/js/*.js')
    .pipe(concat('bundle.min.js'))
    .pipe(uglify())
    .pipe(gulp.dest('dist/js'));
});

// 监听文件变化
gulp.task('watch', function() {
  gulp.watch('src/scss/*.scss', gulp.series('styles'));
  gulp.watch('src/js/*.js', gulp.series('scripts'));
});

// 默认任务
gulp.task('default', gulp.parallel('styles', 'scripts', 'watch'));
```

------

### Gulp vs Webpack

- **Gulp**：专注于**任务自动化**（Task Runner），适合流程控制类场景（如编译、压缩、部署）。
- **Webpack**：专注于**模块打包**（Module Bundler），擅长处理模块化依赖关系（如前端工程化中的 JS/CSS 打包）。

两者可互补使用：例如用 Webpack 打包 JS，再用 Gulp 处理 Sass 编译和图片压缩。

------

### 总结

Gulp 是前端开发中不可或缺的自动化工具，尤其适合需要定制化工作流的场景。虽然现代工具（如 Vite、Snowpack）逐渐兴起，但 Gulp 因其灵活性和易用性，仍在许多项目中广泛使用。

## gulp和grunt的区别

Gulp 和 Grunt 都是前端领域经典的**自动化构建工具**（诞生于 2010 年代初），核心目标是解决前端开发中的重复工作（如文件编译、压缩、合并、监听变化等）。但它们的设计理念和实现方式有显著差异，以下从多个维度对比两者的区别：

### 一、核心理念：“代码优于配置” vs “配置优于代码”

这是两者最根本的差异：

| **维度**     | **Grunt**                                                    | **Gulp**                                                     |
| ------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **设计理念** | **配置优于代码**：通过 JSON-like 的配置文件定义任务流程，强调“声明式”描述。 | **代码优于配置**：通过 JavaScript 代码直接编写任务逻辑，强调“命令式”编程。 |

### 二、任务执行机制：基于临时文件 vs 基于内存流

任务执行的中间处理方式直接影响性能和复杂度：

#### 1. Grunt：基于临时文件的“磁盘读写”

Grunt 的每个任务（如编译、压缩）会先将处理结果写入**临时文件**，再传递给下一个任务。例如：

```
// Grunt 配置示例（Gruntfile.js）
module.exports = function(grunt) {
  grunt.initConfig({
    sass: {
      dev: {
        files: [{
          expand: true,
          cwd: 'src/scss',
          src: ['*.scss'],
          dest: 'tmp/css', // 先输出到临时目录 tmp/css
          ext: '.css'
        }]
      }
    },
    cssmin: {
      dev: {
        files: [{
          expand: true,
          cwd: 'tmp/css', // 从临时目录读取
          src: ['*.css'],
          dest: 'dist/css' // 最终输出到 dist/css
        }]
      }
    }
  });
  grunt.loadNpmTasks('grunt-contrib-sass');
  grunt.loadNpmTasks('grunt-contrib-cssmin');
  grunt.registerTask('default', ['sass', 'cssmin']); // 先执行 sass，再执行 cssmin
};
```

**缺点**：频繁的磁盘读写导致性能较低，大型项目构建速度慢。

#### 2. Gulp：基于内存流的“流式处理”

Gulp 利用 Node.js 的**流（Stream）机制**，文件在内存中通过管道（`pipe`）依次处理，无需写入临时文件。例如：

```
// Gulp 配置示例（gulpfile.js）
const gulp = require('gulp');
const sass = require('gulp-sass')(require('sass'));
const cssmin = require('gulp-cssmin');

gulp.task('sass', function () {
  return gulp.src('src/scss/*.scss') // 读取源文件
    .pipe(sass()) // 编译 Sass（内存中处理）
    .pipe(cssmin()) // 压缩 CSS（内存中处理）
    .pipe(gulp.dest('dist/css')); // 一次性输出到目标目录
});
```

**优点**：无磁盘 I/O 瓶颈，构建速度更快，尤其适合大型项目。

### 三、配置方式：JSON 式配置 vs JavaScript 代码

#### 1. Grunt：JSON 风格的配置对象

Grunt 通过 `grunt.initConfig()`传入一个**配置对象**，定义任务的输入输出路径、参数等，逻辑隐藏在插件内部。例如：

```
grunt.initConfig({
  uglify: {
    target: {
      files: { 'dist/app.min.js': ['src/*.js'] } // 输入 src/*.js，输出 dist/app.min.js
    }
  }
});
```

**缺点**：配置复杂，难以调试；任务间依赖关系隐含在顺序中，灵活性差。

#### 2. Gulp：JavaScript 代码直接编写逻辑

Gulp 的 `gulpfile.js`是纯 JavaScript 文件，可直接使用变量、函数、条件判断等编程特性，逻辑透明可控。例如：

```
gulp.task('js', function () {
  const isProd = process.env.NODE_ENV === 'production';
  let stream = gulp.src('src/*.js');
  if (isProd) {
    stream = stream.pipe(uglify()); // 生产环境才压缩
  }
  return stream.pipe(gulp.dest('dist/js'));
});
```

**优点**：灵活性高，可自定义复杂逻辑；调试方便（直接用 `console.log`输出中间结果）。

### 四、插件生态与设计哲学

#### 1. Grunt：插件功能单一，组合复杂

Grunt 插件通常**只做一件事**（如 `grunt-contrib-sass`仅编译 Sass，`grunt-contrib-cssmin`仅压缩 CSS），需通过多个插件组合完成任务。例如，编译并压缩 CSS 需要 `sass`+ `cssmin`两个插件，且中间需临时文件衔接。

#### 2. Gulp：插件功能集成，链式调用

Gulp 插件设计为**“转换流”**（Transform Stream），每个插件专注处理一种转换（如编译、压缩），但通过管道（`pipe`）可轻松串联多个插件，形成流畅的处理链。例如：

```
gulp.src('src/scss/*.scss')
  .pipe(sass())       // 编译
  .pipe(autoprefixer()) // 自动加前缀
  .pipe(cssmin())     // 压缩
  .pipe(gulp.dest('dist/css'));
```

### 五、性能对比

由于 Gulp 基于内存流处理，避免了 Grunt 的磁盘 I/O 开销，**Gulp 的构建速度通常比 Grunt 快 2~10 倍**（尤其在大型项目中）。例如，处理 1000 个 JS 文件时，Grunt 可能需要数秒，而 Gulp 仅需几百毫秒。

### 六、社区与现状

- **Grunt**：诞生于 2012 年，曾是前端构建的主流工具，但随着 Gulp（2013 年）和 Webpack（2014 年）的崛起，逐渐被替代。目前社区活跃度下降，仅维护旧项目。
- **Gulp**：曾是最流行的前端构建工具（2015-2018 年），但现代前端工程化转向 Webpack、Vite 等“模块化打包工具”（更适合 SPA）。不过，Gulp 仍在传统多页应用（MPA）或需要高度自定义流程的场景中使用。

### 总结：如何选择？

- **选 Grunt**：仅当维护旧项目（依赖 Grunt 生态），或团队对 JSON 配置更熟悉（但实际很少见）。
- **选 Gulp**：需要高性能、灵活逻辑的自动化任务（如多页应用构建、静态站点生成），且希望避免复杂的配置。
- **现代替代方案**：若开发 SPA（如 React、Vue），优先选择 Webpack 或 Vite；若仅需简单任务（如压缩图片），可使用轻量工具（如 `imagemin-cli`）。

**核心差异总结**：Grunt 是“配置驱动的临时文件处理器”，Gulp 是“代码驱动的内存流处理器”——Gulp 凭借更高效的执行方式和灵活的编程模型，成为更现代的选择。

## 示例

具体用法参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-gulp/demo-gettting-started

安装gulp和插件依赖

```json
{
  ...
  "devDependencies": {
    "gulp": "^5.0.1",
    "gulp-clean-css": "^4.3.0",
    "gulp-htmlmin": "^5.0.1",
    "gulp-imagemin": "^9.2.0",
    "gulp-rename": "^2.1.0",
    "gulp-sourcemaps": "^3.0.0",
    "gulp-terser": "^2.1.0"
  }
}
```

编写gulpfile.js脚本文件，文件里编写gulp编译任务

```javascript
import gulp from 'gulp';
import htmlmin from 'gulp-htmlmin';
import cleanCSS from 'gulp-clean-css';
import terser from 'gulp-terser';
import imagemin from 'gulp-imagemin';
import rename from 'gulp-rename';
import sourcemaps from 'gulp-sourcemaps';

// HTML 构建任务
gulp.task('html', function() {
  return gulp.src('src/html/*.html')
    .pipe(htmlmin({
      collapseWhitespace: true,
      removeComments: true,
      minifyCSS: true,
      minifyJS: true
    }))
    .pipe(gulp.dest('dist'));
});

// CSS 构建任务
gulp.task('css', function() {
  return gulp.src('src/css/*.css')
    .pipe(sourcemaps.init())
    .pipe(cleanCSS({
      compatibility: 'ie8'
    }))
    .pipe(rename({ suffix: '.min' }))
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest('dist/css'));
});

// JS 构建任务
gulp.task('js', function() {
  return gulp.src('src/js/*.js')
    .pipe(sourcemaps.init())
    .pipe(terser({
      compress: {
        drop_console: true
      }
    }))
    .pipe(rename({ suffix: '.min' }))
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest('dist/js'));
});

// 图片构建任务
gulp.task('images', function() {
  return gulp.src('src/images/*')
    .pipe(imagemin({
      optimizationLevel: 5,
      progressive: true,
      interlaced: true
    }))
    .pipe(gulp.dest('dist/images'));
});

// 监视任务
gulp.task('watch', function() {
  gulp.watch('src/html/*.html', gulp.series('html'));
  gulp.watch('src/css/*.css', gulp.series('css'));
  gulp.watch('src/js/*.js', gulp.series('js'));
  gulp.watch('src/images/*', gulp.series('images'));
});

// 构建任务
gulp.task('build', gulp.parallel('html', 'css', 'js', 'images'));

// 默认任务
gulp.task('default', gulp.series('build', 'watch'));
```

开发环境运行

```sh
npm run dev
```

编译发布运行

```sh
npm run build
```


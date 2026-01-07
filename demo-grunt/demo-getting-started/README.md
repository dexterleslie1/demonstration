# Grunt 开发流程演示项目

这个项目演示了如何使用 Grunt 来处理 LESS、JavaScript 和 HTML 文件，展示了从开发到生产环境的完整构建流程。

## 项目功能

### 1. LESS 处理
- 将 LESS 文件编译为 CSS
- 开发环境保留源码映射，便于调试
- 生产环境压缩 CSS 文件，减小文件大小

### 2. JavaScript 处理
- 合并多个 JS 文件
- 压缩 JS 文件（移除空格、注释，变量名混淆）
- 开发环境保留完整代码，生产环境优化

### 3. HTML 处理
- 开发环境保持代码可读性
- 生产环境压缩 HTML（移除注释、空格，优化属性）

### 4. 自动监控
- 监控文件变化
- 自动重新编译相关文件

## 项目结构

```
demo-getting-started/
├── src/
│   ├── less/
│   │   └── style.less      # LESS 源文件
│   ├── js/
│   │   ├── utils.js        # 工具函数
│   │   └── main.js         # 主应用逻辑
│   └── html/
│       └── index.html      # HTML 源文件
├── dist/
│   ├── css/
│   │   ├── style.css       # 编译后的 CSS
│   │   ├── style.css.map   # 源码映射
│   │   └── style.min.css   # 压缩后的 CSS
│   ├── js/
│   │   ├── app.js          # 合并后的 JS
│   │   └── app.min.js      # 压缩后的 JS
│   ├── index.html          # 编译后的 HTML
│   └── index.min.html      # 压缩后的 HTML
├── Gruntfile.js            # Grunt 配置文件
├── package.json            # 项目配置和依赖
└── README.md               # 项目说明
```

## 安装依赖

```bash
npm install
```

## 使用命令

### 开发环境构建
```bash
grunt
```
- 编译 LESS 到 CSS，保留源码映射
- 合并 JS 文件
- 压缩 JS 文件
- 处理 HTML 文件，保持可读性

### 生产环境构建
```bash
grunt production
```
- 编译并压缩 LESS 到 CSS
- 合并并压缩 JS 文件
- 深度压缩 HTML 文件

### 自动监控
```bash
grunt dev
```
- 监控所有源文件变化
- 自动重新执行相关构建任务

## 演示功能

1. **LESS 功能**：
   - 使用变量定义颜色和字体大小
   - 嵌套选择器
   - 混合（Mixins）
   - 颜色函数

2. **JavaScript 功能**：
   - 模块化代码组织
   - 工具函数封装
   - 事件处理
   - 动态 DOM 操作

3. **HTML 功能**：
   - 响应式设计
   - 语义化标签
   - 资源引用

## Grunt 插件说明

- `grunt-contrib-less`：处理 LESS 文件
- `grunt-contrib-concat`：合并 JavaScript 文件
- `grunt-contrib-uglify`：压缩 JavaScript 文件
- `grunt-contrib-htmlmin`：压缩 HTML 文件
- `grunt-contrib-watch`：监控文件变化

## 开发流程建议

1. 在 `src/` 目录下编写源代码
2. 使用 `grunt dev` 命令启动自动监控
3. 开发过程中实时查看 `dist/` 目录下的输出
4. 发布前使用 `grunt production` 生成优化的生产文件

## 浏览器访问

构建完成后，可以直接在浏览器中打开：
- 开发版本：`dist/index.html`
- 生产版本：`dist/index.min.html`

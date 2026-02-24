## 是什么呢？

vConsole 是一个由腾讯前端团队 AlloyTeam 开发的轻量级、可扩展的前端开发者调试面板，专为移动端网页设计。它类似于浏览器开发者工具（DevTools），但可以在手机浏览器或 WebView 中直接使用，方便开发者在真机环境下调试页面、查看日志、网络请求、DOM 结构等。

### 主要功能：

- **Console 面板**：查看 `console.log`等输出的日志信息，支持多级分类（log、info、warn、error）。
- **Network 面板**：监控网络请求（XHR、Fetch 等），查看请求和响应详情。
- **Element 面板**：查看和修改 DOM 结构及样式（部分功能受限）。
- **Storage 面板**：管理本地存储（LocalStorage、SessionStorage、Cookie 等）。
- **System 面板**：查看页面 URL、User Agent 等系统信息。
- **自定义插件**：支持扩展自定义面板，满足特定调试需求。

### 使用场景：

- 移动端网页开发调试，尤其是真机测试时。
- 嵌入 WebView 的 Hybrid 应用开发。
- 快速定位线上问题（可通过 URL 参数控制是否显示）。

### 基本用法：

引入 vConsole 库后，初始化即可：

```
// 引入 vConsole
import VConsole from 'vconsole';

// 初始化
const vConsole = new VConsole();

// 可选：通过环境变量控制是否显示
if (process.env.NODE_ENV === 'development') {
  new VConsole();
}
```

vConsole 默认在页面右下角显示一个小绿色按钮，点击即可展开面板。它也支持配置选项，如自定义位置、禁用某些面板等。

## 本目录演示

### 文件说明

- `index.html`：演示页面（移动端友好 UI）
- `app.js`：按钮行为与 vConsole 开关逻辑
- `style.css`：页面样式

### 如何打开

建议用本地 HTTP 服务打开（避免 `file://` 场景下部分网络请求行为不一致）。

**方式 A：Node（有 npm / pnpm / yarn 任一种即可）**

在 `demo-vconsole/` 目录执行：

```bash
npx http-server . -p 8080
```

然后访问：`http://localhost:8080/index.html?vconsole=1`

**方式 B：Python**

在 `demo-vconsole/` 目录执行：

```bash
python -m http.server 8080
```

然后访问：`http://localhost:8080/index.html?vconsole=1`

### 如何使用（演示点）

1. **启用/关闭 vConsole**：URL 带 `?vconsole=1` 或点击页面“启用 vConsole（刷新）”（会写入 localStorage 并刷新）。启用后会在 console 输出初始化日志。
2. **Console 面板**：点 “console.log / info / warn / error / table / time” 按钮观察输出。
3. **Network 面板**：点 “fetch 请求 / XHR 请求 / 失败请求”，观察成功请求（如 httpbin.org/get、uuid）与失败请求（如 status/503）。
4. **Storage 面板**：点 “写入 Storage / 读取 Storage / 写入 Cookie”，在 vConsole 的 Storage 面板查看 localStorage、sessionStorage、cookie。
5. **Error 观察**：点 “同步抛错 / 异步抛错” 观察 vConsole 对错误的捕获与展示。
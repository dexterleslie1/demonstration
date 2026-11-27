## Canvas

### 是什么？

**Canvas**（`<canvas>`）是 HTML5 提供的位图画布：页面上的一块矩形绘图区域，通过 JavaScript 的 2D / WebGL 等 API，用命令式代码逐像素（或逐帧）绘制图形、图像、动画和交互内容。

技术要点：

- 标签本身几乎没有内容，真正干活的是 `getContext('2d')` 或 `getContext('webgl')` 拿到的绘图上下文
- 绘制结果是**位图**（像素栅格），放大后会模糊；缩放、擦除、重绘都由脚本控制
- 适合高频更新：游戏帧循环、粒子、实时图表、图像处理、签名板等
- 与 DOM/CSS 解耦：画布内的形状不是 DOM 节点，不能直接用 CSS 选中或用事件委托到「某个圆」

官方参考：https://developer.mozilla.org/zh-CN/docs/Web/API/Canvas_API

### 解决了什么问题？

在 Canvas 出现前，网页里动态、复杂的图形往往靠 Flash、大量 DOM 拼图，或静态图片，难做、难维护，性能也差。Canvas 把「在浏览器里用脚本自由画图」变成一等能力。

| 痛点 | Canvas 怎么解决 |
|------|----------------|
| 纯 HTML/CSS 难画任意形状、曲线、像素级效果 | 用 2D API 画线、弧、路径、渐变、图像合成等 |
| 用大量 DOM 节点做动画/游戏，回流重排成本高 | 单块画布按帧重绘，适合大量精灵与粒子 |
| 需要实时图表、波形、摄像头/图像处理 | 直接读写像素或持续重绘，延迟可控 |
| 需要手写签名、涂鸦、简单编辑器 | 监听指针事件，在画布上描点、擦除、导出为图片 |
| Flash 等插件逐渐被淘汰 | 用原生 Web API 替代插件式绘图 |

**和 SVG 的大致分工**：SVG 是矢量、每个图形是 DOM 节点，适合图标、可缩放插图、需要单独交互/样式的图形；Canvas 是位图、命令式绘制，适合像素处理、高帧率动画和大量对象的场景。二者常配合使用，不是互斥替换。

### 最小示例

```html
<canvas id="c" width="300" height="150"></canvas>
<script>
  const canvas = document.getElementById('c');
  const ctx = canvas.getContext('2d');
  ctx.fillStyle = '#3b82f6';
  ctx.fillRect(20, 20, 100, 80);
</script>
```

### 本目录演示：用 Canvas 2D 封装 UI 框架 + 登录页

演示思路：在 Canvas 上自研一套极简组件树（布局坐标、脏标记重绘、命中测试、焦点与键盘），再拼出登录界面——说明「没有 DOM 控件时，UI 框架要自己解决什么问题」。

#### 文件说明

| 文件 | 作用 |
|------|------|
| `index.html` | 页面入口，只有一块 `#ui-canvas` |
| `style.css` | 页面外壳样式（标题区）；登录 UI 本身不在 CSS 里 |
| `js/canvas-ui.js` | 框架：`CanvasApp` / `Widget`，组件：`Panel` `Label` `TextInput` `Button` |
| `js/login.js` | 用上述组件组装登录卡片与校验提示 |

框架要点：

- **组件树**：父节点相对坐标，`absoluteX/Y` 用于绘制与命中
- **重绘**：`markDirty` + `requestAnimationFrame`，有变化才 `clearRect` 再画
- **事件**：在 canvas 上算指针坐标 → `hitTest` 找到最上层组件 → 分发 mousedown/up/click
- **输入**：`TextInput` 可聚焦；用页面外的隐藏 `<input>` 承接真实键盘与 IME，再同步到画布文字（纯 Canvas 无法直接弹系统输入法）

#### 如何打开

在 `demo-canvas/` 目录启动本地静态服务后访问 `index.html`：

```bash
npx http-server . -p 8080
# 或
python -m http.server 8080
```

浏览器打开：`http://localhost:8080/index.html`

#### 试用

1. 点击「用户名 / 密码」输入框输入（密码显示为圆点）
2. **Label / Button / TextInput**（以及带 `text`/`title` 的 Panel）均支持按住拖拽选字；`Ctrl/Cmd+A` 全选；选区同步到隐藏 DOM input，便于系统复制。按钮上若拖出选区则不触发点击
3. 点「登录」或按 Enter：空字段会红字提示；都填了会绿字「登录成功」
4. 可对照 `js/login.js` 看如何声明式拼界面，对照 `js/canvas-ui.js` 看绘制与事件如何封装
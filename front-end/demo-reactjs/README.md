## vscode安装react插件

1、ES7 React/Redux/GraphQL/React-Native snippets

## 创建不基于脚手架的 reactjs 项目

> reactjs CDN引用
> https://stackoverflow.com/questions/68350370/get-rid-of-babelstandalone-warning-message-from-cdn-package/68350422#68350422
>
> 参考 不基于脚手架的reactjs项目 demo

## jsx 语法规则

### 什么是JSX

https://en.wikipedia.org/wiki/JSX_(JavaScript)

JSX（JavaScript XML，正式名称为 JavaScript Syntax eXtension）是一种 JavaScript 扩展，允许使用类似 XML 的语法创建文档对象模型 (DOM) 树。 [1] JSX 最初由 Facebook 创建，用于与 React 一起使用，现已被多个 Web 框架采用。[2]: 5 [3]: 11  作为一种语法糖，JSX 通常被转换为嵌套的 JavaScript 函数调用，其结构与原始 JSX 类似。

### 创建虚拟dom语法

> 参考 jsx语法规则/创建虚拟dom语法 demo

### 插入js表达式

> 参考 jsx语法规则/插入js表达式 demo

### 设置虚拟dom的class属性

> 参考 jsx语法规则/设置虚拟dom的class属性 demo

### 设置虚拟dom的style属性

> 参考 jsx语法规则/设置虚拟dom的style属性 demo

## 组件

### 基于非脚手架函数式自定义组件

> 参考 组件的函数式定义 demo

### 基于非脚手架类式自定义组件

> 参考 组件的类式定义 demo

### 受控和非受控组件

参考 受控和非受控 demo

### 组件的生命周期

#### 旧的生命周期

参考 组件的生命周期/旧的生命周期

#### 新的生命周期

参考 组件的生命周期/新的生命周期

### 自定义组件(基于脚手架)

参考 react-component

## 组件的核心属性用法

### state属性用法

> 参考 组件的核心属性state demo

### props属性用法

> 参考 组件的核心属性props demo

### refs属性用法

> 参考 组件的核心属性refs demo

## 事件处理

参考 事件处理 demo、react-handling-event

## react脚手架

### todo 自动动手还原react脚手架

react脚手架架构：react+webpack+es6+eslint

### 使用react脚手架创建项目

安装react脚手架

```sh
sudo npm install -g create-react-app
```

使用react脚手架创建hello-react项目

```sh
create-react-app hello-react
```

进入hello-react项目

```sh
cd hello-react
```

启动hello-react项目

```sh
npm start
```

## 样式模块化

参考 react-modular-css

## checkbox的defaultChecked和checked

参考 react-checkbox-defaultchecked-and-checked

## 综合案例TodoList

参考 demo-todo-list

## react配置http proxy

配置setupProxy.js文件，不需要npm install http-proxy-middleware，因为react脚手架已经包含此组件。

参考 react-config-http-proxy

## 综合案例 demo-github-search

参考 demo-github-search

## 路由

参考 demo-react-router

> 添加 react-router-dom 依赖
>
> ```bash
> npm install react-router-dom
> ```
>
> NavLink 和 Route 包含在 BrowserRouter 中

## 获取自定义组件标签体内容

参考 demo-get-custom-component-body

以下是一个简单的示例，展示了如何在自定义组件中访问和使用其标签体内容：

```jsx
function App() {
  return (
    // 自定义 MyComponent 组件并传递一些标签体内容
    <MyComponent>
      <a href="https://www.baidu.com" target="_blank">跳转到百度</a>
    </MyComponent>
  );
}

export default class MyComponent extends Component {
  render() {
    return (
      <div>
        {/* 使用 this.props.children 访问自定义组件传递的标签体内容 */}
        {this.props.children}
      </div>
    )
  }
}
```


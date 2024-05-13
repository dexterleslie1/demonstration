# react脚手架

## 动手还原react脚手架

todo react脚手架架构：react+webpack+es6+eslint

## 使用react脚手架创建项目

> 实验环境使用的`node`版本为`v20.12.2`。
>
> 基于`create-react-app`脚手架创建的项目是基于最新版本的`react`。
>
> 通过`react`脚手架创建的项目源码[链接](https://github.com/dexterleslie1/demonstration/tree/master/front-end/demo-reactjs/hello-react)

安装react脚手架

```sh
sudo npm install -g create-react-app
```

使用react脚手架创建hello-react项目

```sh
create-react-app hello-react
```

进入hello-react目录

```sh
cd hello-react
```

启动hello-react项目

```sh
npm start
```

打开浏览器访问 http://localhost:3000 项目首页

## 使用react脚手架创建react16项目

> `react16`项目源码[链接](https://github.com/dexterleslie1/demonstration/tree/master/front-end/demo-reactjs/hello-react16)

安装`react`脚手架

```bash
sudo npm install -g create-react-app
```

使用`react`脚手架创建`hello-react16`项目

```bash
create-react-app hello-react16
```

进入`hello-react16`项目

```bash
cd hello-react16
```

此时创建的新项目是基于最新版本的`react`，需要手动降低`react`版本

```sh
# 把`package.json`中react、react-dom、@testing-library/react降低版本
npm install react@16.x react-dom@16.x @testing-library/react@9
```

修改`index.js`内容如下：

```jsx
// 修改 import ReactDOM from 'react-dom/client';
// createRoot 替换为 render
import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';

// const root = ReactDOM.createRoot(document.getElementById('root'));
// root.render(
//   <React.StrictMode>
//     <App />
//   </React.StrictMode>
// );
ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>, document.getElementById('root'));

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
```

启动项目

```sh
npm start
```

打开浏览器访问 http://localhost:3000 项目首页
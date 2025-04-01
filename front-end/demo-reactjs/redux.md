# `redux`状态管理

## `redux`库使用

> `redux`用于集中式管理`react`状态并实现组件间共享这些状态数据。
>
> 具体例子源码参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/front-end/demo-reactjs/demo-redux)

使用`create-react-app cli`创建最新版本`react`的项目

安装`redux`依赖

```bash
npm install redux
```

安装`redux-thunk`依赖用于设置`redux action`支持异步`action`

```bash
npm install redux-thunk
```

通过参考`/src/redux`相关文件配置`redux`，其中`/src/redux/store.js`是配置`redux store`的关键文件。

通过参考`/src/components/Count/index.jsx`文件学习`redux`相关`API`的调用。

## `react-redux`库使用

> `react-redux`提供了`connect`函数（或高阶组件），用于将`Redux store`中的状态（state）和派发（dispatch）函数映射到`React`组件的`props`上。这样，`React`组件就可以访问`Redux store`中的数据，并触发数据更新。
>
> `react-redux`使用容器组件包含UI组件，其中容器组件负责和`redux`通讯修改数据，`UI`组件负责`redux`中数据的展示。
>
> `react-redux`容器组件能够自动监控`redux`数据变化并刷新UI
>
> 具体例子源码参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/front-end/demo-reactjs/demo-react-redux)

使用`create-react-app cli`创建最新版本`react`的项目

安装`react-redux`依赖

```bash
npm install react-redux
```

安装`redux-thunk`依赖用于设置`redux action`支持异步`action`

```bash
npm install redux-thunk
```

具体配置参考例子源代码 [链接](https://github.com/dexterleslie1/demonstration/tree/master/front-end/demo-reactjs/demo-react-redux)

## `redux`开发者工具使用

> 具体配置参考例子源代码 [链接](https://github.com/dexterleslie1/demonstration/tree/master/front-end/demo-reactjs/demo-react-redux)

使用`firefox`访问`redux devtools`浏览器插件在线安装 [地址](https://addons.mozilla.org/en-US/firefox/addon/reduxdevtools/)，点击在线安装。

`react`项目中安装`@redux-devtools/extension`用于在项目中激活`redux devtools`浏览器插件

> `@redux-devtools/extension`具体用法 [参考](https://www.npmjs.com/package/@redux-devtools/extension)

```bash
npm install @redux-devtools/extension
```

修改项目中的`store.js`文件内容如下：

```jsx
import { createStore, applyMiddleware, combineReducers } from 'redux'
import countReducer from "./reducers/count"
import personReducer from "./reducers/person"
// redux支持异步action
import { thunk } from "redux-thunk"
import { composeWithDevTools } from '@redux-devtools/extension';

// 定义redux状态对象的数据结构{myCount:...,myPerson:...}
// 使用redux状态对象中数据state.myCount
const allInOneReducers = combineReducers({
    myCount: countReducer,
    myPerson: personReducer
})

// 暴露redux store，整个应用只有一个store
export default createStore(allInOneReducers, composeWithDevTools(applyMiddleware(thunk)/* redux支持异步action */)/* 激活浏览器redux-devtools */)

```

上面代码中`composeWithDevTools(...`是激活`react devtools`浏览器插件的关键，否则`devtools`一直处于禁用状态。
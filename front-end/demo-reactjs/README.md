## `vscode` 安装 `react` 插件

名称：`ES7 React/Redux/GraphQL/React-Native snippets`，作者：`dsznajder`



快速代码片段：

- 新建 `index.jsx` 文件，输入 `rfc` 弹出智能提示快速生成基于函数的组件。
- 新建 `index.jsx` 文件，输入 `rcc` 弹出智能提示快速生成基于类的组件。



## 使用脚手架创建项目

### 动手还原脚手架

`TODO`：脚手架架构 `react+webpack+es6+eslint`



### 创建项目

> 实验环境使用的 `node` 版本为 `v20.12.2`。
>
> 基于 `create-react-app` 脚手架创建的项目是基于最新版本的 `react`。
>
> 通过 `react` 脚手架创建的项目源码 [链接](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/hello-react)

安装脚手架

```sh
sudo npm install -g create-react-app

# 如果在执行 create-react-app hello-react 命令时报告 Permission Denided 错误，则使用下面命令安装脚手架
npm install -g create-react-app
```

使用脚手架创建 `hello-react` 项目

```sh
create-react-app hello-react
```

进入 `hello-react` 目录

```sh
cd hello-react
```

启动 `hello-react` 项目

```sh
npm start
```

打开浏览器访问 `http://localhost:3000` 项目首页



### 创建 `react16` 项目

> `react16` 项目源码 [链接](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/hello-react16)

安装脚手架

```bash
sudo npm install -g create-react-app
```

使用脚手架创建 `hello-react16` 项目

```bash
create-react-app hello-react16
```

进入 `hello-react16` 项目

```bash
cd hello-react16
```

此时创建的新项目是基于最新版本的 `react`，需要手动降低 `react` 版本

```sh
# 把`package.json`中react、react-dom、@testing-library/react降低版本
npm install react@16.x react-dom@16.x @testing-library/react@9
```

修改 `index.js` 内容如下：

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

打开浏览器访问 `http://localhost:3000` 项目首页



## 创建不基于脚手架的 `reactjs` 项目

> [`reactjs CDN` 引用](https://stackoverflow.com/questions/68350370/get-rid-of-babelstandalone-warning-message-from-cdn-package/68350422#68350422)
> 
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E4%B8%8D%E5%9F%BA%E4%BA%8E%E8%84%9A%E6%89%8B%E6%9E%B6%E7%9A%84reactjs%E9%A1%B9%E7%9B%AE)



## `jsx` 语法规则

### 什么是 `JSX`

>[参考维基百科](https://en.wikipedia.org/wiki/JSX_(JavaScript))

`JSX`（`JavaScript XML`，正式名称为 `JavaScript Syntax eXtension`）是一种 `JavaScript` 扩展，允许使用类似 `XML` 的语法创建文档对象模型 (`DOM`) 树。 [1] `JSX` 最初由 `Facebook` 创建，用于与 `React` 一起使用，现已被多个 `Web` 框架采用。[2]: 5 [3]: 11  作为一种语法糖，`JSX` 通常被转换为嵌套的 `JavaScript` 函数调用，其结构与原始 `JSX` 类似。

### 创建虚拟 `dom` 语法

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/jsx%E8%AF%AD%E6%B3%95%E8%A7%84%E5%88%99/%E5%88%9B%E5%BB%BA%E8%99%9A%E6%8B%9Fdom%E8%AF%AD%E6%B3%95)

### 插入 `js` 表达式

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/jsx%E8%AF%AD%E6%B3%95%E8%A7%84%E5%88%99/%E6%8F%92%E5%85%A5js%E8%A1%A8%E8%BE%BE%E5%BC%8F)

### 设置虚拟 `dom` 的 `class` 属性

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/jsx%E8%AF%AD%E6%B3%95%E8%A7%84%E5%88%99/%E8%AE%BE%E7%BD%AE%E8%99%9A%E6%8B%9Fdom%E7%9A%84class%E5%B1%9E%E6%80%A7)

### 设置虚拟 `dom` 的 `style` 属性

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/jsx%E8%AF%AD%E6%B3%95%E8%A7%84%E5%88%99/%E8%AE%BE%E7%BD%AE%E8%99%9A%E6%8B%9Fdom%E7%9A%84style%E5%B1%9E%E6%80%A7)



## 组件

### 基于非脚手架函数式自定义组件

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E7%BB%84%E4%BB%B6%E7%9A%84%E5%87%BD%E6%95%B0%E5%BC%8F%E5%AE%9A%E4%B9%89)

`index.html`：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../js/react.development.js" ></script>
    <script type="text/javascript" src="../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../js/babel.min.js" ></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        function MyCompoent() {
            return <h1>Hello, React</h1>
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<MyCompoent/>, document.getElementById("test"))
    </script>
</body>
</html>
```



### 基于非脚手架类式自定义组件

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E7%BB%84%E4%BB%B6%E7%9A%84%E7%B1%BB%E5%BC%8F%E5%AE%9A%E4%B9%89)

`index.html`：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../js/react.development.js" ></script>
    <script type="text/javascript" src="../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../js/babel.min.js" ></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        class MyComponent extends React.Component {
            render() {
                return <h1>Hello, React</h1>
            }
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<MyComponent/>, document.getElementById("test"))
    </script>
</body>
</html>
```



### 受控和非受控组件

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E5%8F%97%E6%8E%A7%E5%92%8C%E9%9D%9E%E5%8F%97%E6%8E%A7%E7%BB%84%E4%BB%B6)

在 React 中，**受控组件（Controlled Components）** 和 **非受控组件（Uncontrolled Components）** 是两种处理表单输入（如 `<input>`、`<textarea>`、`<select>` 等）的不同模式，核心区别在于 **表单数据的状态管理权归属**。


#### **核心定义**
- **受控组件**：表单元素的状态（如 `value`、`checked`）由 React 的 `state` 完全控制，输入内容的变化会触发 React 状态更新，最终渲染到 DOM。  
- **非受控组件**：表单元素的状态由浏览器原生的 DOM 自身管理（类似传统 jQuery 表单处理），React 仅在需要时通过 `ref` 访问 DOM 获取当前值。  


#### **受控组件：React 状态驱动**
受控组件的核心逻辑是：**“一切数据由 React 管理”**。表单元素的 `value`（或 `checked` 对于单选/复选框）始终绑定 React 的 `state`，输入变化时通过 `onChange` 事件更新 `state`，从而触发重新渲染，保证 UI 与状态同步。

##### **关键特点**
- 必须通过 `value`（或 `checked`）属性绑定 React 的 `state`；  
- 输入变化时必须通过 `onChange`（或其他事件）更新 `state`；  
- 输入内容完全由 React 状态决定，用户输入无法绕过 React 直接修改 DOM。

##### **示例：受控 Input 组件**
```jsx
import { useState } from 'react';

function ControlledInput() {
  // 1. 用 state 管理输入值
  const [inputValue, setInputValue] = useState('');

  // 2. onChange 事件更新 state
  const handleChange = (e) => {
    setInputValue(e.target.value);
  };

  return (
    <input
      type="text"
      value={inputValue} // 绑定 state
      onChange={handleChange} // 输入变化时更新 state
    />
  );
}
```

##### **适用场景**
- 需要实时校验输入（如限制输入长度、格式）；  
- 需要根据输入动态调整其他 UI（如自动补全、级联选择）；  
- 需要表单提交前统一处理数据（避免直接操作 DOM）。  


#### **非受控组件：DOM 状态驱动**
非受控组件的核心逻辑是：**“数据由 DOM 自身管理，React 仅在需要时读取”**。表单元素的 `value` 不绑定 React 的 `state`，而是由浏览器原生 DOM 维护。React 通过 `ref`（通常是 `useRef`）获取 DOM 节点，在需要时（如提交表单时）读取其值。

##### **关键特点**
- 不强制绑定 `value`（或 `checked`），输入内容由用户直接修改 DOM；  
- 通过 `ref` 访问 DOM 节点的 `value`（或 `checked`）属性获取当前值；  
- 适合简单表单或需要直接操作原生 DOM 的场景。

##### **示例：非受控 Input 组件**
```jsx
import { useRef } from 'react';

function UncontrolledInput() {
  // 1. 用 ref 引用 DOM 节点
  const inputRef = useRef(null);

  // 2. 提交时通过 ref 获取值
  const handleSubmit = () => {
    console.log('输入内容：', inputRef.current.value);
  };

  return (
    <div>
      <input type="text" ref={inputRef} /> {/* 不绑定 value */}
      <button onClick={handleSubmit}>提交</button>
    </div>
  );
}
```

##### **适用场景**
- 简单表单（如仅需提交时获取一次值）；  
- 需要访问原生 DOM 属性（如文件上传 `<input type="file">`，必须通过 `ref` 获取 `files`）；  
- 性能敏感场景（减少 `state` 更新带来的重新渲染）。  


#### **核心区别对比**
| **特性**     | **受控组件**                                         | **非受控组件**                      |
| ------------ | ---------------------------------------------------- | ----------------------------------- |
| 数据管理方   | React 的 `state`                                     | 浏览器原生 DOM                      |
| 输入同步方式 | 输入变化 → 触发 `onChange` → 更新 `state` → 重新渲染 | 输入直接修改 DOM，React 按需读取    |
| 状态一致性   | 强制同步（UI 与 `state` 始终一致）                   | 可能不同步（需手动通过 `ref` 同步） |
| 控制粒度     | 细粒度（可实时干预输入）                             | 粗粒度（仅在需要时读取）            |
| 代码复杂度   | 较高（需维护 `state` 和事件处理）                    | 较低（无需 `state`，直接用 `ref`）  |


#### **选择建议**
- **优先使用受控组件**：符合 React “数据驱动 UI” 的设计哲学，便于状态管理和表单校验，适合大多数业务场景（尤其是需要实时交互的表单）。  
- **谨慎使用非受控组件**：仅在需要简化代码（如简单表单）或必须访问原生 DOM（如文件上传）时使用。  


#### **补充：受控组件的特殊处理**
对于某些需要自定义行为的输入（如富文本编辑器、日期选择器），React 仍可通过受控模式管理，只需将 `value` 绑定到 `state`，并通过自定义事件（如 `onChange`）更新 `state`。例如：
```jsx
// 自定义日期选择器（受控）
function DatePicker({ value, onChange }) {
  // 内部通过原生日期选择器修改值时，调用 onChange 更新 React state
  return <input type="date" value={value} onChange={(e) => onChange(e.target.value)} />;
}
```

总之，受控与非受控组件的选择取决于具体需求，但 React 推荐优先使用受控组件以保证数据流的可预测性。



### 组件的生命周期



#### 旧的生命周期

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E7%BB%84%E4%BB%B6%E7%9A%84%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F)

`index.html`：

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>

<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../js/react.development.js"></script>
    <script type="text/javascript" src="../js/react-dom.development.js"></script>
    <script type="text/javascript" src="../js/babel.min.js"></script>
    <script type="text/javascript" src="../js/prop-types.js"></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        /*
            1、初始化阶段：由ReactDOM.render()触发，初次渲染。
                1.1、constructor
                1.2、componentWillMount
                1.3、render
                1.4、componentDidMount
            2、更新阶段：由组件内部this.setState或者父组件render触发。
                2.1、shouldComponentUpdate
                2.2、componentWillUpdate
                2.3、render
                2.4、componentDidUpdate
            3、卸载组件：由ReactDOM.unmountComponentAtNode触发。
                3.1、componentWillUnmount
        */
        class ParentComponent extends React.Component {
            constructor(props) {
                super(props)
                console.log(`parent constructor`)
                this.state = { name: "Dexter" }
            }

            changeName = () => {
                this.setState({ name: "Dexterleslie" })
            }

            componentWillMount() {
                console.log(`parent componentWillMount`)
            }
            componentDidMount() {
                console.log(`parent componentDidMount`)
            }
            componentWillReceiveProps() {
                console.log(`parent componentWillReceiveProps`)
            }
            // 是否调用render更新组件
            shouldComponentUpdate() {
                console.log(`parent shouldComponentUpdate`)
                return true
            }
            componentWillUpdate() {
                console.log(`parent componentWillUpdate`)
            }
            componentDidUpdate() {
                console.log(`parent componentDidUpdate`)
            }
            componentWillUnmount() {
                console.log(`parent componentWillUnmount`)
            }

            render() {
                console.log(`parent render`)
                return (
                    <div>
                        <div>Parent Component</div>
                        <ChildComponent myName={this.state.name} />
                        <button onClick={this.changeName}>修改名称</button>
                        <button onClick={() => { this.changeName(); this.forceUpdate()/* forceUpdate()越过shouldComponentUpdate()强制执行render */ }}>forceUpdate</button>
                        <button onClick={()=>{ReactDOM.unmountComponentAtNode(document.getElementById("test"))}}>unmount组件</button>
                    </div>
                )
            }
        }

        class ChildComponent extends React.Component {
            constructor(props) {
                super(props)
                console.log(`child constructor`)
            }

            componentWillMount() {
                console.log(`child componentWillMount`)
            }
            componentDidMount() {
                console.log(`child componentDidMount`)
            }
            // 父组件render时会触发此函数回调。NOTE：父组件第一次render时不会触发此函数回调。
            componentWillReceiveProps() {
                console.log(`child componentWillReceiveProps`)
            }
            shouldComponentUpdate() {
                console.log(`child shouldComponentUpdate`)
                return true
            }
            componentWillUpdate() {
                console.log(`child componentWillUpdate`)
            }
            componentDidUpdate() {
                console.log(`child componentDidUpdate`)
            }
            componentWillUnmount() {
                console.log(`child componentWillUnmount`)
            }

            render() {
                console.log(`child render`)
                return (
                    <div>
                        <div>Child Component with name {this.props.myName}</div>
                    </div>
                )
            }
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<ParentComponent />, document.getElementById("test"))
    </script>
</body>

</html>
```



#### 新的生命周期

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E7%BB%84%E4%BB%B6%E7%9A%84%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F)

`index.html`：

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>

<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../js/react.development.js"></script>
    <script type="text/javascript" src="../js/react-dom.development.js"></script>
    <script type="text/javascript" src="../js/babel.min.js"></script>
    <script type="text/javascript" src="../js/prop-types.js"></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        /*
            NOTE：新版本react将会弃用componentWillMount、componentWillUpdate、componentWillReceiveProps
            1、初始化阶段：由ReactDOM.render()触发，初次渲染。
                1.1、constructor
                // 1.2、componentWillMount
                1.3、render
                1.4、componentDidMount
            2、更新阶段：由组件内部this.setState或者父组件render触发。
                2.1、shouldComponentUpdate
                // 2.2、componentWillUpdate
                2.3、render
                2.4、componentDidUpdate
            3、卸载组件：由ReactDOM.unmountComponentAtNode触发。
                3.1、componentWillUnmount
        */
        class ParentComponent extends React.Component {
            constructor(props) {
                super(props)
                console.log(`parent constructor`)
                this.state = { name: "Dexter" }
            }

            changeName = () => {
                this.setState({ name: "Dexterleslie" })
            }

            // componentWillMount() {
            //     console.log(`parent componentWillMount`)
            // }
            componentDidMount() {
                console.log(`parent componentDidMount`)
            }
            // componentWillReceiveProps() {
            //     console.log(`parent componentWillReceiveProps`)
            // }
            // 是否调用render更新组件
            shouldComponentUpdate() {
                console.log(`parent shouldComponentUpdate`)
                return true
            }
            // componentWillUpdate() {
            //     console.log(`parent componentWillUpdate`)
            // }
            componentDidUpdate() {
                console.log(`parent componentDidUpdate`)
            }
            componentWillUnmount() {
                console.log(`parent componentWillUnmount`)
            }

            render() {
                console.log(`parent render`)
                return (
                    <div>
                        <div>Parent Component</div>
                        <ChildComponent myName={this.state.name} />
                        <button onClick={this.changeName}>修改名称</button>
                        <button onClick={() => { this.changeName(); this.forceUpdate()/* forceUpdate()越过shouldComponentUpdate()强制执行render */ }}>forceUpdate</button>
                        <button onClick={()=>{ReactDOM.unmountComponentAtNode(document.getElementById("test"))}}>unmount组件</button>
                    </div>
                )
            }
        }

        class ChildComponent extends React.Component {
            constructor(props) {
                super(props)
                console.log(`child constructor`)
            }

            // componentWillMount() {
            //     console.log(`child componentWillMount`)
            // }
            componentDidMount() {
                console.log(`child componentDidMount`)
            }
            // // 父组件render时会触发此函数回调。NOTE：父组件第一次render时不会触发此函数回调。
            // componentWillReceiveProps() {
            //     console.log(`child componentWillReceiveProps`)
            // }
            shouldComponentUpdate() {
                console.log(`child shouldComponentUpdate`)
                return true
            }
            // componentWillUpdate() {
            //     console.log(`child componentWillUpdate`)
            // }
            componentDidUpdate() {
                console.log(`child componentDidUpdate`)
            }
            componentWillUnmount() {
                console.log(`child componentWillUnmount`)
            }

            render() {
                console.log(`child render`)
                return (
                    <div>
                        <div>Child Component with name {this.props.myName}</div>
                    </div>
                )
            }
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<ParentComponent />, document.getElementById("test"))
    </script>
</body>

</html>
```



### 自定义组件（基于脚手架）

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/react-component)

自定义组件 `Component1`、`Component2` 目录结构：

```
src
├── components
│   ├── Component1
│   │   ├── index.css
│   │   └── index.jsx
│   └── Component2
│       ├── index.css
│       └── index.jsx
```

自定义组件 `Component1`

- `src/components/Component1/index.css`：

  ```css
  .my-style {
      background-color: green;
      margin: 10px;
  }
  ```

- `src/components/Component1/index.jsx`：

  ```jsx
  import React from 'react'
  import "./index.css"
  
  export default class Component1 extends React.Component {
      render() {
          return (
              <div className='my-style'>
                  Component1
              </div>
          )
      }
  }
  ```

自定义组件 `Component2`：

- `src/components/Component2/index.css`：

  ```css
  .my-style1 {
      background-color: yellowgreen;
  }
  ```

- `src/components/Component2/index.jsx`：

  ```jsx
  import React from "react";
  import "./index.css"
  
  export default class Component2 extends React.Component {
      render() {
          return (
              <div className="my-style1">
                  Component2
              </div>
          )
      }
  }
  ```

`App.js` 引用自定义组件：

```jsx
import logo from './logo.svg';
import './App.css';
import Component1 from './components/Component1';
import Component2 from './components/Component2';

function App() {
  return (
    <div className="App">
      <Component1/>
      <Component2/>
    </div>
  );
}

export default App;

```



### 获取自定义组件标签体内容

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/demo-get-custom-component-body)

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



## 组件的核心属性用法

### `state` 属性用法

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E7%BB%84%E4%BB%B6%E7%9A%84%E6%A0%B8%E5%BF%83%E5%B1%9E%E6%80%A7state)

#### 标准用法

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../js/react.development.js" ></script>
    <script type="text/javascript" src="../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../js/babel.min.js" ></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        class Weatcher extends React.Component {
            constructor(props) {
                super(props)
                this.state = {isHot: false}
                // 使 changeWeather 函数中的 this 指向当前 Weather 实例，否则 this 为 undefined
                this.changeWeather = this.changeWeather.bind(this)
            }

            render() {
                return <h1 onClick={this.changeWeather}>今天天气很{this.state.isHot?"炎热":"凉爽"}</h1>
            }

            changeWeather() {
                // 修改并合并新的state
                this.setState({isHot: !this.state.isHot})
            }
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<Weatcher/>, document.getElementById("test"))
    </script>
</body>
</html>
```



#### 精简用法

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../js/react.development.js" ></script>
    <script type="text/javascript" src="../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../js/babel.min.js" ></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        class Weatcher extends React.Component {

            // 使用类赋值语句初始化 state 成员变量
            state = {isHot: false}
            
            render() {
                return <h1 onClick={this.changeWeather}>今天天气很{this.state.isHot?"炎热":"凉爽"}</h1>
            }

            // 使用箭头函数是因为使 this 指向当前 Weatcher 实例，否则 this 为 undefined
            changeWeather = ()=>{
                // 修改并合并新的state
                this.setState({isHot: !this.state.isHot})
            }
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<Weatcher/>, document.getElementById("test"))
    </script>
</body>
</html>
```



### `props` 属性用法

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E7%BB%84%E4%BB%B6%E7%9A%84%E6%A0%B8%E5%BF%83%E5%B1%9E%E6%80%A7props)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>
    <div id="test1"></div>

    <script type="text/javascript" src="../js/react.development.js" ></script>
    <script type="text/javascript" src="../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../js/babel.min.js" ></script>
    <script type="text/javascript" src="../js/prop-types.js"></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        class Person extends React.Component {
            // https://legacy.reactjs.org/docs/typechecking-with-proptypes.html
            static propTypes = {
                // 姓名必须为string类型并且必须提供
                name: PropTypes.string.isRequired,
                // 性别必须为string类型
                sex: PropTypes.string,
                // 年龄必须为数值类型
                age: PropTypes.number
            }

            static defaultProps = {
                // 性别默认值为 男
                sex: "男",
                // 年龄默认值为 18
                age: 18
            }

            render() {
                let {name, sex, age} = this.props
                return (
                    <ul>
                        <li>姓名：{name}</li>
                        <li>性别：{sex}</li>
                        <li>年龄：{age+1}</li>
                    </ul>
                )
            }
        }

        // 渲染自定义组件到页面容器中
        let data = {name: "张三", sex: "女", age: 19}
        ReactDOM.render(<Person {...data}/>, document.getElementById("test"))
        ReactDOM.render(<Person name="李四" sex="男" age={20}/>, document.getElementById("test1"))
    </script>
</body>
</html>
```



### `refs` 属性用法

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E7%BB%84%E4%BB%B6%E7%9A%84%E6%A0%B8%E5%BF%83%E5%B1%9E%E6%80%A7refs)



#### `createRef` 形式的 `ref`

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../js/react.development.js" ></script>
    <script type="text/javascript" src="../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../js/babel.min.js" ></script>
    <script type="text/javascript" src="../js/prop-types.js"></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        class MyComponent extends React.Component {
            input1 = React.createRef()
            input2 = React.createRef()

            showDataOnClick = ()=> {
                alert(`左侧input的值为： ${this.input1.current.value}`)
            }

            showDataOnBlur = ()=>{
                alert(`右侧input的值为： ${this.input2.current.value}`)
            }

            render() {
                return (
                    <div>
                        <input ref={this.input1} type="text" placeholder="点击按钮提示数据"/>&nbsp;
                        <button onClick={this.showDataOnClick}>点击我提示左侧的数据</button>&nbsp;
                        <input ref={this.input2} type="text" placeholder="失去焦点提示数据" onBlur={this.showDataOnBlur}/>
                    </div>
                )
            }
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<MyComponent/>, document.getElementById("test"))
    </script>
</body>
</html>
```



#### 回调形式的 `ref`

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../js/react.development.js" ></script>
    <script type="text/javascript" src="../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../js/babel.min.js" ></script>
    <script type="text/javascript" src="../js/prop-types.js"></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        class MyComponent extends React.Component {
            showDataOnClick = ()=> {
                alert(`左侧input的值为： ${this.input1.value}`)
            }

            showDataOnBlur = ()=>{
                alert(`右侧input的值为： ${this.input2.value}`)
            }

            render() {
                return (
                    <div>
                        <input ref={(c/*当前组件*/)=>{this.input1=c}} type="text" placeholder="点击按钮提示数据"/>&nbsp;
                        <button onClick={this.showDataOnClick}>点击我提示左侧的数据</button>&nbsp;
                        <input ref={(c)=>{this.input2=c}} type="text" placeholder="失去焦点提示数据" onBlur={this.showDataOnBlur}/>
                    </div>
                )
            }
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<MyComponent/>, document.getElementById("test"))
    </script>
</body>
</html>
```



#### 字符串形式的 `ref`

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../js/react.development.js" ></script>
    <script type="text/javascript" src="../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../js/babel.min.js" ></script>
    <script type="text/javascript" src="../js/prop-types.js"></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        class MyComponent extends React.Component {
            showDataOnClick = ()=> {
                alert(`左侧input的值为： ${this.refs.input1.value}`)
            }

            showDataOnBlur = ()=>{
                alert(`右侧input的值为： ${this.refs.input2.value}`)
            }

            render() {
                return (
                    <div>
                        <input ref="input1" type="text" placeholder="点击按钮提示数据"/>&nbsp;
                        <button onClick={this.showDataOnClick}>点击我提示左侧的数据</button>&nbsp;
                        <input ref="input2" type="text" placeholder="失去焦点提示数据" onBlur={this.showDataOnBlur}/>
                    </div>
                )
            }
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<MyComponent/>, document.getElementById("test"))
    </script>
</body>
</html>
```



## 事件处理

>详细用法请参考本站 [示例1](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E4%BA%8B%E4%BB%B6%E5%A4%84%E7%90%86)、[示例2](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/react-handling-event)

`index.html`：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../js/react.development.js" ></script>
    <script type="text/javascript" src="../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../js/babel.min.js" ></script>
    <script type="text/javascript" src="../js/prop-types.js"></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        class MyComponent extends React.Component {
            input1 = React.createRef()

            showDataOnClick = ()=> {
                alert(`左侧input的值为： ${this.input1.current.value}`)
            }

            showDataOnBlur = (event/* 事件对象 */)=>{
                alert(`右侧input的值为： ${event.target.value/* event.target 触发事件的事件源对象 */}`)
            }

            render() {
                return (
                    <div>
                        <input ref={this.input1} type="text" placeholder="点击按钮提示数据"/>&nbsp;
                        <button onClick={this.showDataOnClick/* 使用onClick绑定按钮点击事件处理函数 */}>点击我提示左侧的数据</button>&nbsp;
                        <input type="text" placeholder="失去焦点提示数据" onBlur={this.showDataOnBlur}/>
                    </div>
                )
            }
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<MyComponent/>, document.getElementById("test"))
    </script>
</body>
</html>
```

`App.jsx`：

```jsx
import logo from './logo.svg';
import './App.css';

function App() {
  return (
    <div className="App">
      {
        /*
          https://www.geeksforgeeks.org/how-to-get-the-enter-key-in-reactjs/ 
           */
      }
      <div>演示onKeyDown事件处理</div>
      <div className="demo1">
        <input type="text" onKeyDown={(e) => {
          console.log(e.key)
        }} />
      </div>
      <hr />

      {
        /*
        https://codingbeautydev.com/blog/react-get-input-value-on-enter/
         */
      }
      <div>演示input text输入Enter后获取当前input值</div>
      <div className="demo2">
        <input type="text" onKeyDown={(e) => {
          if (e.key === "Enter") {
            alert("input当前值：" + e.target.value)
          }
        }} />
      </div>
      <hr />
    </div>
  );
}

export default App;

```



## 样式

`react` 组件中通过 `import 'xxx.css'` 样式文件 `+ className` 的方式引用样式，`react` 会保留原来的样式类名，导致样式可能存在冲突和覆盖的可能性，如下面例子所示方式引用样式：

`XxxComponent/index.css` 内容如下：

```css
.mystyle {
    background-color: yellowgreen;
}
```

`XxxComponent/index.jsx` 内容如下：

```jsx
import React from "react";
import "./index.css"

export default class Component2 extends React.Component {
    render() {
        return (
            <div className="mystyle">
                Component2
            </div>
        )
    }
}
```

`react` 在编译后依旧保留一个名为 `.mystyle` 的全局样式，为了避免组件之间样式的冲突和覆盖，参考样式的模块化解决此问题。



### 全局样式

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/react-css)

在 `index.css` 中定义全局样式

```css
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

code {
  font-family: source-code-pro, Menlo, Monaco, Consolas, 'Courier New',
    monospace;
}

```

在 `index.js` 中引入全局样式文件 `index.css`

```jsx
import './index.css';
```



### 局部样式（样式的模块化）

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/react-css)

样式的模块化，在样式被编译后会随机生成一个独一无二的类名避免样式的冲突和覆盖。如下面例子所示：

`XxxComponent/index.module.css` 内容如下：

```css
.mystyle {
    background-color: green;
    margin: 10px;
}
```

`XxxComponent/index.jsx` 内容如下：

```jsx
import React from 'react'
import component1css from "./index.module.css"

export default class Component1 extends React.Component {
    render() {
        return (
            <div className={component1css.mystyle}>
                Component1
            </div>
        )
    }
}
```



### 内联样式

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/demo-reactjs/react-css)

内联样式示例

```jsx
function App() {
  return (
    <div className="App">
      {/* 演示内联样式的用法 */}
      <div style={{
        fontSize: '24px',
        fontWeight: 'bold',
        color: 'green',
        backgroundColor: 'yellow',
      }}>演示内联样式的用法</div>
    </div>
  );
}
```



### 使用`scss`样式

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/react-css)

安装 `sass` 依赖用于编译 `scss` 样式文件

```bash
npm install sass --save-dev
```

`/src/components/ComponentScss/index.scss` 内容如下：

```scss
.my-component-scss {

    .nested1 {

        .nested2 {
            font-size: 24px;
            font-weight: bold;
            color: yellowgreen;
        }
    }
}
```

`/src/components/ComponentScss/index.jsx` 内容如下：

```jsx
import React from 'react'
import "./index.scss"

export default class ComponentScss extends React.Component {
    render() {
        return (
            <div className="my-component-scss">
                <div className='nested1'>
                    <div className='nested2'>
                        ComponentScss!
                    </div>
                </div>
            </div>
        )
    }
}
```

上面例子中，使用 `import './index.scss'` 导入 `scss` 文件后 `react` 项目会自动调用 `sass` 依赖编译 `scss` 样式文件。    



### 独立样式文件

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/react-css)

`index.css`：

```css
.mystyle {
    background-color: yellowgreen;
}
```

`index.jsx` 引用样式文件：

```jsx
import React from "react";
import "./index.css"

export default class Component2 extends React.Component {
    render() {
        return (
            <div className="mystyle">
                Component2
            </div>
        )
    }
}
```



## `checkbox` 的 `defaultChecked` 和 `checked`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/react-checkbox-defaultchecked-and-checked)

`App.jsx`：

```jsx
import React from 'react'
import logo from './logo.svg';
import './App.css';

export default class App extends React.Component {
  state = {
    checked1:true,
    checked2:true,
  }

  render() {
    return (
      <div className="App">
        {
          /*
          1、checkbox的手动选择和不选中都不能够改变defaultChecked对应state中的变量，因为defaultChecked只在组件初始化渲染一次。
          2、修改state中的checked变量值不会改变defaultChecked对应checkbox的选中和不选中状态。
          3、checked+onChange能够自由控制state中的checked变量。
          4、defaultChecked、defaultValue只在初始渲染时由状态控制，之后更新不再跟状态有关系，而checked、value在全过程中都受状态控制。
          https://blog.51cto.com/u_15295346/3022088
           */
        }
        <div>defaultChecked用法</div>
        <div className="demo1">
          <input type="checkbox" defaultChecked={this.state.checked1}/>
          <input type="button" value="修改state.checked1=false" onClick={()=>{this.setState({checked1:false})}}/>
          <input type="button" value="显示checked1的当前值" onClick={()=>{alert(`state.checked1=${this.state.checked1}`)}}/>
        </div>
        <hr />

        <div>checked用法</div>
        <div className="demo2">
          <input type="checkbox" checked={this.state.checked2} onChange={()=>{this.setState({checked2:!this.state.checked2})}}/>
          <input type="button" value="修改state.checked2=false" onClick={()=>{this.setState({checked2:false})}}/>
          <input type="button" value="显示checked2的当前值" onClick={()=>{alert(`state.checked2=${this.state.checked2}`)}}/>
        </div>
        <hr />
      </div>
    );
  }

}

```



## 综合案例 `TodoList`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/demo-todo-list)



## 配置 `http proxy`

> 提示：配置 `setupProxy.js` 文件，不需要 `npm install http-proxy-middleware`，因为脚手架已经包含此组件。
>
> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/react-config-http-proxy)



## 路由

> 下面的实验都是基于`react-router-dom v5`的。

### 路由的基本用法

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/demo-react-router)

添加 `react-router-dom` 依赖

```sh
npm install react-router-dom@5
```

`index.js`需要使用`<BrowserRouter/>`标签包含`<App/>`标签

```jsx
ReactDOM.render(
  <BrowserRouter>
    <App />
  </BrowserRouter>, document.getElementById('root'));
```

`App.js`内容如下：

```jsx
import logo from './logo.svg';
import './App.css';

import { NavLink, Route, Switch } from 'react-router-dom';
import Home from './components/Home';
import About from './components/About';

function App() {
  // 添加 react-router-dom 依赖

  return (
    ...
          <NavLink exact activeClassName='my-active' to="/">Home</NavLink>
          <NavLink exact activeClassName='my-active' to="/about">About</NavLink>
    ...
          <Switch>
            <Route exact path='/' component={Home} />
            <Route exact path='/about' component={About} />
          </Switch>
    ...
  );
}

export default App;

```



### `redirect` 用法

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/demo-react-router-redirect)

`Switch` 组件用于只渲染与 URL 匹配的第一个 `<Route>` 或 `<Redirect>`。你可以使用 `<Redirect>` 作为默认路由，当用户访问的 URL 与任何 `<Route>` 都不匹配时，将其重定向到某个默认页面。

```jsx
import { BrowserRouter as Router, Switch, Route, Redirect } from 'react-router-dom';  
  
function App() {  
  return (  
    <Router>  
      <Switch>  
        <Route path="/about" component={About} />  
        <Route path="/contact" component={Contact} />  
        <Redirect from="*" to="/about" />  
      </Switch>  
    </Router>  
  );  
}
```

在上述示例中，如果用户访问的 URL 不是 `/about` 或 `/contact`，则他们将被重定向到 `/about`。



### 嵌套路由

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/demo-react-router-nested-routing)

子路由路径前缀需要与父路由匹配，例如：`/home/news`、`/home/message`子路有和`/home`父路由匹配。

`App.js`内容如下：

```jsx
import logo from './logo.svg';
import './App.css';

import { NavLink, Route, Switch, Redirect } from 'react-router-dom';
import Home from './components/Home';
import About from './components/About';

function App() {
  // 添加 react-router-dom 依赖

  return (
    ...
          <NavLink activeClassName='my-active' to="/home">Home</NavLink>
          <NavLink activeClassName='my-active' to="/about">About</NavLink>
    ...
          <Switch>
            <Route path='/home' component={Home} />
            <Route path='/about' component={About} />
            <Redirect from="*" to="/home" />  
          </Switch>
     ...  
  );
}

export default App;

```

Home组件的`/Home/index.jsx`内容如下：

```jsx
import React, { Component } from 'react'
import { NavLink, Switch, Route, Redirect } from 'react-router-dom'
import News from './News'
import Message from './Message'

export default class Home extends Component {
  render() {
    return (
      <div>
        <div>
          Home页面
        </div>
        <div>
          <NavLink activeClassName='my-active' to="/home/news">News</NavLink>&nbsp;
          <NavLink activeClassName='my-active' to="/home/message">Message</NavLink>
        </div>
        <div>
          <Switch>
            <Route path='/home/news' component={News} />
            <Route path='/home/message' component={Message} />
            <Redirect from="*" to="/home/news" />
          </Switch>
        </div>
      </div>
    )
  }
}

```

上面的`/Home/index.jsx`中路由`/home/news`、`/home/message`是`/home`路由的嵌套路由。



### 路由参数

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/demo-react-router-parameters)

#### `URL`路由参数

`/Home/Message/index.jsx`内容如下：

```jsx
import React, { Component } from 'react'
import { NavLink, Route } from 'react-router-dom'
import Detail from './Detail'

export default class Message extends Component {
  state = {
    messageList: [{
      id: 1,
      content: "你好"
    }, {
      id: 2,
      content: "大家好"
    }]
  }
  render() {
    return (
      <div>
        <div>消息列表如下：</div>
        <hr />
        <div style={{
          display: 'flex',
          flexDirection: 'column',
        }}>
          {
            this.state.messageList.map((el) => {
              return (
                <NavLink to={`/home/message/detail/${el.id}/${el.content}`}>{el.content}</NavLink>
              )
            })
          }
        </div>
        <hr />
        <div>
          <Route path="/home/message/detail/:id/:content" component={Detail} />
        </div>
      </div>
    )
  }
}

```

`/Home/Message/Detail/index.jsx`内容如下：

```jsx
import React, { Component } from 'react'

export default class Detail extends Component {
  render() {
    const {id, content} = this.props.match.params
    return (
      <div>
        <div>ID: {id}</div>
        <div>Content: {content}</div>
      </div>
    )
  }
}

```

从上面例子可以看出`URL`路由参数主要是通过`<NavLink to={``/home/message/detail/${el.id}/${el.content}``}>{el.content}</NavLink>`、`<Route path="/home/message/detail/:id/:content" component={Detail} />`方式传递路由参数，`const {id, content} = this.props.match.params`方式获取路由参数。



#### `query`路由参数

安装`query-string`组件用于解析`query`参数

```bash
npm install query-string
```

`/Home/Message/index.jsx`内容如下：

```jsx
import React, { Component } from 'react'
import { NavLink, Route } from 'react-router-dom'
import Detail from './Detail'

export default class Message extends Component {
  state = {
    messageList: [{
      id: 1,
      content: "你好"
    }, {
      id: 2,
      content: "大家好"
    }]
  }
  render() {
    return (
      <div>
        <div>消息列表如下：</div>
        <hr />
        <div style={{
          display: 'flex',
          flexDirection: 'column',
        }}>
          {
            this.state.messageList.map((el) => {
              return (
                // URL路由参数
                // <NavLink to={`/home/message/detail/${el.id}/${el.content}`}>{el.content}</NavLink>

                // query路由参数
                <NavLink to={`/home/message/detail?id=${el.id}&content=${el.content}`}>{el.content}</NavLink>
              )
            })
          }
        </div>
        <hr />
        <div>
          {/* URL路由参数 */}
          {/* <Route path="/home/message/detail/:id/:content" component={Detail} /> */}

          {/* query路由参数 */}
          <Route path="/home/message/detail" component={Detail} />
        </div>
      </div>
    )
  }
}

```

`/Home/Message/Detail/inex.jsx`内容如下：

```jsx
import React, { Component } from 'react'
import qs from "query-string"

export default class Detail extends Component {
  render() {
    // URL路由参数
    // const {id, content} = this.props.match.params

    // query路由参数
    const {id, content} = qs.parse(this.props.location.search)
    return (
      <div>
        <div>ID: {id}</div>
        <div>Content: {content}</div>
      </div>
    )
  }
}

```

从上面例子可以看出`query`路由参数主要是通过`<NavLink to={``/home/message/detail?id=${el.id}&content=${el.content}``}>{el.content}</NavLink>`、`<Route path="/home/message/detail" component={Detail} />`方式传递路由参数，`const {id, content} = qs.parse(this.props.location.search)`方式获取路由参数。



#### `state`路由参数

`/Home/Message/index.jsx`内容如下：

```jsx
import React, { Component } from 'react'
import { NavLink, Route } from 'react-router-dom'
import Detail from './Detail'

export default class Message extends Component {
  state = {
    messageList: [{
      id: 1,
      content: "你好"
    }, {
      id: 2,
      content: "大家好"
    }]
  }
  render() {
    return (
      <div>
        <div>消息列表如下：</div>
        <hr />
        <div style={{
          display: 'flex',
          flexDirection: 'column',
        }}>
          {
            this.state.messageList.map((el) => {
              return (
                // URL路由参数
                // <NavLink to={`/home/message/detail/${el.id}/${el.content}`}>{el.content}</NavLink>

                // query路由参数
                // <NavLink to={`/home/message/detail?id=${el.id}&content=${el.content}`}>{el.content}</NavLink>

                // state路由参数
                <NavLink to={{ pathname: '/home/message/detail', state: { id: el.id, content: el.content } }}>{el.content}</NavLink>
              )
            })
          }
        </div>
        <hr />
        <div>
          {/* URL路由参数 */}
          {/* <Route path="/home/message/detail/:id/:content" component={Detail} /> */}

          {/* query路由参数 */}
          {/* <Route path="/home/message/detail" component={Detail} /> */}

          {/* state路由参数 */}
          <Route path="/home/message/detail" component={Detail} />
        </div>
      </div>
    )
  }
}

```

`/Home/Message/Detail/index.jsx`内容如下：

```jsx
import React, { Component } from 'react'
import qs from "query-string"

export default class Detail extends Component {
  render() {
    // URL路由参数
    // const {id, content} = this.props.match.params

    // query路由参数
    // const {id, content} = qs.parse(this.props.location.search)

    // state路由参数
    const {id, content} = this.props.location.state || {}
    return (
      <div>
        <div>ID: {id}</div>
        <div>Content: {content}</div>
      </div>
    )
  }
}

```

从上面例子可以看出`state`路由参数主要是通过`<NavLink to={ { pathname: '/home/message/detail', state: { id: el.id, content: el.content } }}>{el.content}</NavLink>`、`<Route path="/home/message/detail" component={Detail} />`方式传递路由参数，`const {id, content} = this.props.location.state || {}`方式获取路由参数。



### 编程式路由

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/demo-react-router-programmatic-routing)

`/Home/Message/index.jsx`内容如下：

```jsx
import React, { Component } from 'react'
import { NavLink, Route } from 'react-router-dom'
import Detail from './Detail'

export default class Message extends Component {
  state = {
    messageList: [{
      id: 1,
      content: "你好"
    }, {
      id: 2,
      content: "大家好"
    }]
  }

  handlePush = (id, content) => {
    this.props.history.push(`/home/message/detail/${id}/${content}`)
  }
  handleReplace = (id, content) => {
    this.props.history.replace(`/home/message/detail/${id}/${content}`)
  }

  render() {
    return (
      <div>
        <div>消息列表如下：</div>
        <hr />
        <ul>
          {
            this.state.messageList.map((el) => {
              return (
                <li>
                  <NavLink to={`/home/message/detail/${el.id}/${el.content}`}>{el.content}</NavLink>
                  &nbsp;&nbsp;<button onClick={() => this.handlePush(el.id, el.content)}>push查看</button>
                  &nbsp;&nbsp;<button onClick={() => this.handleReplace(el.id, el.content)}>replace查看</button>
                </li>
              )
            })
          }
        </ul>
        <hr />
        <div>
          <Route path="/home/message/detail/:id/:content" component={Detail} />
        </div>
      </div>
    )
  }
}

```

`/Home/Message/Detail/index.jsx`内容如下：

```jsx
import React, { Component } from 'react'
import qs from "query-string"

export default class Detail extends Component {
  render() {
    const {id, content} = this.props.match.params
    return (
      <div>
        <div>ID: {id}</div>
        <div>Content: {content}</div>
      </div>
    )
  }
}

```

编程式路由主要通过调用`this.props.history.push(`/home/message/detail/${id}/${content}`)`、`this.props.history.replace(`/home/message/detail/${id}/${content}`)`接口实现。



### 获取当前路由信息

在 React 中实现类似 Vue2 的 `v-if="this.$route.path !== '/'"` 条件渲染（根据当前路由路径决定是否显示元素），核心是通过 **React Router 提供的路由信息获取 API** 结合 React 的条件渲染语法（如三元运算符、`if` 语句）实现。以下是具体实现方案，覆盖函数组件和类组件的不同场景，并兼容 React Router v5/v6 版本。


#### 一、核心思路
1. **获取当前路由路径**：通过 React Router 提供的 API（如 `useLocation` 或 `withRouter`）获取当前路径 `pathname`。
2. **条件渲染**：使用 JavaScript 条件语法（三元运算符、`if` 语句等）根据路径决定是否渲染目标元素。


#### 二、具体实现（函数组件）
React Router v6 推荐使用 `useLocation` 钩子获取路由信息（v5 也可用），以下是函数组件的实现示例：

##### 1. 安装依赖（若未安装）
确保已安装 `react-router-dom`（v6+）：
```bash
npm install react-router-dom@latest
```

##### 2. 函数组件示例（React Router v6）
```jsx
import { useLocation } from 'react-router-dom';

function MyComponent() {
  // 获取当前路由的 location 对象（包含 pathname）
  const location = useLocation();
  // 当前路径（例如：'/', '/about'）
  const currentPath = location.pathname;

  return (
    <div>
      {/* 当路径不等于 '/' 时显示该段落 */}
      {currentPath !== '/' && (
        <p>当前不是首页，路径是：{currentPath}</p>
      )}

      {/* 或用三元运算符（适合需要渲染不同内容时） */}
      {currentPath !== '/' ? (
        <p>非首页内容</p>
      ) : (
        <p>这是首页</p>
      )}
    </div>
  );
}

export default MyComponent;
```

##### 3. 函数组件示例（React Router v5）
v5 中 `useLocation` 同样可用（语法一致），但如果组件是类组件，需用 `withRouter`（见下文）。


#### 三、类组件实现
类组件无法直接使用 `useLocation` 钩子，需通过 `withRouter` 高阶组件注入路由信息（React Router v5/v6 均支持，但 v6 中 `withRouter` 已被标记为遗留，推荐用钩子）。

##### 1. 类组件示例（React Router v6）
```jsx
import { withRouter } from 'react-router-dom';

class MyClassComponent extends React.Component {
  render() {
    // 通过 props 获取路由信息（v6 中 withRouter 注入的 props 包含 location）
    const { location } = this.props;
    const currentPath = location.pathname;

    return (
      <div>
        {currentPath !== '/' && (
          <p>类组件：当前不是首页</p>
        )}
      </div>
    );
  }
}

// 用 withRouter 包裹组件，注入路由信息
export default withRouter(MyClassComponent);
```

##### 2. 类组件示例（React Router v5）
v5 中 `withRouter` 直接注入 `location`、`history`、`match`，用法与 v6 类似：
```jsx
import { withRouter } from 'react-router-dom';

class MyClassComponent extends React.Component {
  render() {
    const { location } = this.props;
    const currentPath = location.pathname;

    return (
      <div>
        {currentPath !== '/' ? (
          <p>v5 类组件：非首页内容</p>
        ) : (
          <p>v5 类组件：首页</p>
        )}
      </div>
    );
  }
}

export default withRouter(MyClassComponent);
```

#### 四、关键细节说明

1. **`location.pathname` 的值**：  
   `location.pathname` 是当前 URL 的路径部分（例如 `/about`、`/user/123`），严格匹配字符串（区分大小写）。

2. **精确匹配 vs 模糊匹配**：  
   若需要类似 Vue 中 `$route.path` 的模糊匹配（例如 `/about` 匹配 `/about/me`），需调整判断逻辑（如使用 `startsWith`）：
   ```jsx
   // 当路径以 '/about' 开头时隐藏元素（模糊匹配）
   {!(currentPath.startsWith('/about')) && <p>非 about 页面</p>}
   ```

3. **路由版本兼容性**：  
   - React Router v6 中 `withRouter` 已被标记为遗留（推荐用钩子），但仍可使用。  
   - v5 及以下版本必须通过 `withRouter` 注入路由信息到类组件。

4. **动态路由处理**：  
   若路径包含动态参数（如 `/user/:id`），`location.pathname` 会保留参数（如 `/user/123`），判断时需注意是否需要排除参数部分（可通过正则或字符串分割处理）。


#### 五、完整示例（React Router v6）
假设项目结构如下：
```
src/
  components/
    HomePage.jsx
    AboutPage.jsx
  App.js
```

`App.js` 中配置路由：
```jsx
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './components/HomePage';
import AboutPage from './components/AboutPage';
import MyComponent from './components/MyComponent';

function App() {
  return (
    <Router>
      <div>
        <nav>
          <a href="/">首页</a>
          <a href="/about">关于</a>
        </nav>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/about" element={<AboutPage />} />
        </Routes>
        <MyComponent /> {/* 无论当前路由是什么，都会渲染 MyComponent */}
      </div>
    </Router>
  );
}

export default App;
```

`MyComponent.js` 中根据路径显示内容：
```jsx
import { useLocation } from 'react-router-dom';

function MyComponent() {
  const location = useLocation();
  const isHomePage = location.pathname === '/';

  return (
    <div>
      {isHomePage ? (
        <p>欢迎来到首页！</p>
      ) : (
        <p>您当前在 {location.pathname} 页面</p>
      )}
    </div>
  );
}

export default MyComponent;
```


#### 总结
- **函数组件**：使用 `useLocation` 获取 `location.pathname`，结合三元运算符或逻辑与（`&&`）实现条件渲染。  
- **类组件**：通过 `withRouter` 注入路由信息，再通过 `props.location.pathname` 判断路径。  
- 核心逻辑是通过路由库提供的 API 获取当前路径，再用 React 的条件语法控制元素显示。



#### 实验

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/demo-react-router)

函数式组件获取当前路由信息：

>使用 `useLocation` 函数获取当前路由信息对象。

- `ReactFunctionComponent/index.jsx`：

  ```jsx
  import React from 'react'
  import { useLocation } from 'react-router-dom';
  
  export default function ReactFunctionComponent() {
      // 获取当前路由的 location 对象（包含 pathname）
      const location = useLocation();
      // 当前路径（例如：'/', '/about'）
      const currentPath = location.pathname;
  
      return (
          <div>ReactFunctionComponent当前路由信息：{currentPath}</div>
      )
  }
  
  ```

  

类式组件获取当前路由信息：

>通过 `withRouter` 高阶组件注入路由信息（`React Router v5/v6` 均支持，但 `v6` 中 `withRouter` 已被标记为遗留，推荐用钩子）。

- `Home/index.jsx`：

  ```jsx
  import React, { Component } from 'react'
  
  import { withRouter } from 'react-router-dom';
  
  
  class Home extends Component {
    render() {
      const { location } = this.props;
      const currentPath = location.pathname;
  
      return (
        <div>
          Home当前路由信息：{currentPath}
        </div>
      )
    }
  }
  
  export default withRouter(Home)
  
  ```

  



## 综合案例 `demo-github-search`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/demo-github-search)

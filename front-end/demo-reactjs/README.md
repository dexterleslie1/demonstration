## `vscode` 安装 `react` 插件

名称：`ES7 React/Redux/GraphQL/React-Native snippets`，作者：`dsznajder`



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



## 组件的核心属性用法

### state属性用法

> 参考 组件的核心属性state demo

### props属性用法

> 参考 组件的核心属性props demo

### refs属性用法

> 参考 组件的核心属性refs demo

## 事件处理

参考 事件处理 demo、react-handling-event

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

## 钩子



### 介绍

在 React 中，**钩子（Hooks）** 是 React 16.8 版本引入的一项核心特性，它允许你在**函数组件**中直接使用状态（State）、生命周期逻辑、上下文（Context）等原本只能在类组件中使用的功能。钩子的出现彻底改变了 React 组件的开发模式，让函数组件成为现代 React 应用的主流选择。


#### 一、为什么需要钩子？
在钩子出现前，React 主要通过**类组件**（Class Component）管理状态和生命周期逻辑。但类组件存在一些痛点：
- **代码冗余**：类组件需要继承 `React.Component`，编写模板代码（如 `constructor`、`render` 方法）。
- **生命周期复杂**：多个生命周期方法（如 `componentDidMount`、`componentDidUpdate`、`componentWillUnmount`）需要拆分逻辑，容易导致代码分散。
- **状态逻辑复用困难**：类组件中通过高阶组件（HOC）或 Render Props 复用状态逻辑，但这些模式可能导致组件嵌套过深（“嵌套地狱”）。

钩子的出现解决了这些问题：
- 函数组件通过钩子直接拥有状态和生命周期能力，无需类语法。
- 钩子提供了更简洁的语法（如 `useState` 替代 `this.state` 和 `setState`）。
- 钩子支持逻辑复用（通过自定义钩子），避免组件嵌套。


#### 二、钩子的核心特点
1. **只能在函数组件或自定义钩子中使用**  
   钩子不能在普通 JavaScript 函数、类组件中使用（除了自定义钩子），确保 React 能正确跟踪状态和副作用。

2. **必须在顶层调用**  
   钩子不能在条件语句、循环或嵌套函数中调用（除非在自定义钩子内部）。React 依赖钩子的调用顺序来关联状态和副作用（例如，第一次渲染时调用 `useState` 会初始化状态，后续渲染必须按相同顺序调用）。

3. **组合性**  
   钩子可以组合使用，实现复杂逻辑。例如，用 `useState` 管理状态，用 `useEffect` 处理副作用，用 `useContext` 访问上下文。


#### 三、常用内置钩子
React 内置了多个常用钩子，覆盖了大部分开发场景：

##### 1. `useState`：管理组件状态
用于在函数组件中声明本地状态（前面已详细介绍）。  
**示例**：
```jsx
import { useState } from 'react';

function Counter() {
  const [count, setCount] = useState(0); // 初始化状态为 0
  return <button onClick={() => setCount(count + 1)}>{count}</button>;
}
```

##### 2. `useEffect`：处理副作用
用于处理组件中的副作用（Side Effects），例如数据获取、事件监听、DOM 操作等。它替代了类组件的 `componentDidMount`、`componentDidUpdate` 和 `componentWillUnmount`。  
**语法**：

```jsx
useEffect(() => {
  // 副作用逻辑（如数据请求、订阅事件）
  return () => {
    // 清理函数（可选，用于取消副作用，如取消订阅、清除定时器）
  };
}, [依赖数组]); // 依赖数组：指定副作用重新执行的时机
```
**示例**（数据请求）：
```jsx
import { useState, useEffect } from 'react';

function UserData() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    // 组件挂载或依赖变化时执行（此处无依赖，仅挂载时执行）
    fetch('/api/user')
      .then(res => res.json())
      .then(data => setUser(data));
    
    // 清理函数：组件卸载时取消请求（假设使用 AbortController）
    return () => abortController.abort();
  }, []); // 空数组表示仅在挂载时执行

  return <div>{user?.name}</div>;
}
```

##### 3. `useContext`：访问上下文（Context）
用于获取 React 上下文（Context）的值，替代类组件的 `static contextType` 或 `Context.Consumer`。  
**示例**：
```jsx
import { createContext, useContext } from 'react';

// 创建上下文
const ThemeContext = createContext({ color: 'red' });

// 提供上下文的组件
function App() {
  return (
    <ThemeContext.Provider value={{ color: 'blue' }}>
      <ChildComponent />
    </ThemeContext.Provider>
  );
}

// 使用上下文的组件
function ChildComponent() {
  const theme = useContext(ThemeContext); // 获取上下文值
  return <div style={{ color: theme.color }}>文本颜色</div>;
}
```

##### 4. `useReducer`：管理复杂状态
用于管理复杂的组件状态逻辑（如需要多个子值或多个操作的情况），是 `useState` 的增强版。它通过“reducer 函数”统一管理状态更新逻辑。  
**语法**：
```jsx
const [state, dispatch] = useReducer(reducer, initialState, init);
```
**示例**（购物车状态）：
```jsx
import { useReducer } from 'react';

// 定义 reducer 函数（处理不同 action 类型）
function cartReducer(state, action) {
  switch (action.type) {
    case 'ADD_ITEM':
      return { ...state, items: [...state.items, action.payload] };
    case 'REMOVE_ITEM':
      return { ...state, items: state.items.filter(item => item.id !== action.payload) };
    default:
      return state;
  }
}

function Cart() {
  const [cart, dispatch] = useReducer(cartReducer, { items: [] });

  return (
    <div>
      {cart.items.map(item => (
        <div key={item.id}>
          {item.name}
          <button onClick={() => dispatch({ type: 'REMOVE_ITEM', payload: item.id })}>
            移除
          </button>
        </div>
      ))}
    </div>
  );
}
```

##### 5. `useRef`：引用 DOM 或保存可变值
用于获取 DOM 节点的引用，或保存一个可变的“不触发重新渲染”的值（如定时器 ID、滚动位置等）。  
**示例**（获取 DOM 引用）：
```jsx
import { useRef } from 'react';

function InputFocus() {
  const inputRef = useRef(null); // 创建 ref 对象

  const handleFocus = () => {
    inputRef.current.focus(); // 直接操作 DOM
  };

  return (
    <div>
      <input ref={inputRef} />
      <button onClick={handleFocus}>聚焦输入框</button>
    </div>
  );
}
```

##### 6. `useMemo`：缓存计算结果
用于缓存复杂计算的中间结果，避免重复计算导致性能问题（仅当依赖项变化时重新计算）。  
**示例**（优化列表渲染）：
```jsx
import { useMemo } from 'react';

function ExpensiveList({ data }) {
  // 仅当 data 变化时重新计算排序后的列表
  const sortedData = useMemo(() => {
    return [...data].sort((a, b) => a.value - b.value);
  }, [data]); // 依赖项：data 变化时重新计算

  return (
    <ul>
      {sortedData.map(item => (
        <li key={item.id}>{item.value}</li>
      ))}
    </ul>
  );
}
```

##### 7. `useCallback`：缓存函数引用
用于缓存函数引用，避免因父组件重新渲染导致子组件不必要的重新渲染（仅当依赖项变化时重新创建函数）。  
**示例**（优化子组件渲染）：
```jsx
import { useCallback } from 'react';

function Parent() {
  const handleClick = useCallback(() => {
    console.log('点击事件');
  }, []); // 无依赖，仅创建一次

  return <Child onClick={handleClick} />;
}

function Child({ onClick }) {
  console.log('Child 重新渲染'); // 仅当 onClick 变化时才会重新渲染
  return <button onClick={onClick}>点击</button>;
}
```


#### 四、自定义钩子（Custom Hooks）
React 钩子的核心优势之一是**逻辑复用**。你可以将多个内置钩子组合成**自定义钩子**，封装可复用的逻辑。自定义钩子本质是一个函数，名称以 `use` 开头（约定）。  

**示例**（自定义表单验证钩子）：
```jsx
import { useState, useCallback } from 'react';

// 自定义钩子：表单验证
function useFormValidation(initialValues, validate) {
  const [values, setValues] = useState(initialValues);
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 处理输入变化
  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    setValues(prev => ({ ...prev, [name]: value }));
    // 实时验证
    const newErrors = validate({ ...values, [name]: value });
    setErrors(newErrors);
  }, [values, validate]);

  // 提交表单
  const handleSubmit = useCallback((e) => {
    e.preventDefault();
    const validationErrors = validate(values);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length === 0) {
      setIsSubmitting(true);
      // 提交逻辑（如 API 请求）
      console.log('提交数据：', values);
    }
  }, [values, validate]);

  return {
    values,
    errors,
    isSubmitting,
    handleChange,
    handleSubmit
  };
}

// 使用自定义钩子的组件
function LoginForm() {
  const validate = (values) => {
    const errors = {};
    if (!values.email) errors.email = '请输入邮箱';
    if (!values.password) errors.password = '请输入密码';
    return errors;
  };

  const { values, errors, isSubmitting, handleChange, handleSubmit } = useFormValidation(
    { email: '', password: '' },
    validate
  );

  return (
    <form onSubmit={handleSubmit}>
      <input
        name="email"
        value={values.email}
        onChange={handleChange}
        placeholder="邮箱"
      />
      {errors.email && <p style={{ color: 'red' }}>{errors.email}</p>}

      <input
        name="password"
        value={values.password}
        onChange={handleChange}
        placeholder="密码"
      />
      {errors.password && <p style={{ color: 'red' }}>{errors.password}</p>}

      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? '提交中...' : '登录'}
      </button>
    </form>
  );
}
```


#### 五、钩子的设计原则
为了确保钩子的可靠性和可维护性，React 官方提出了两条核心原则：
1. **只在顶层使用钩子**：不要在循环、条件或嵌套函数中调用钩子（除非在自定义钩子内部）。这确保了 React 能按顺序跟踪状态和副作用。
2. **只在 React 函数中调用钩子**：钩子只能在 React 函数组件或自定义钩子中调用，不能在普通 JavaScript 函数中使用。


#### 总结
React 钩子是一套用于在函数组件中复用状态逻辑的机制，它让函数组件具备了类组件的所有能力（状态、生命周期、上下文等），同时提供了更简洁的语法和更强的逻辑复用能力。掌握钩子（尤其是 `useState`、`useEffect`、`useContext` 等内置钩子）是现代 React 开发的核心技能。



### `useState`

在 React 中，`useState` 是最常用的 **Hook（钩子）** 之一，用于在**函数组件**中添加和管理本地状态（State）。它让函数组件具备了类组件中 `this.state` 和 `setState` 的能力，但写法更简洁。


#### 一、核心概念
- **状态（State）**：组件内部可变化的数据，会触发组件重新渲染。
- **`useState` 的作用**：在函数组件中声明一个状态变量，并提供一个更新该变量的函数。


#### 二、基础用法
##### 1. 导入 `useState`
首先需要从 React 中导入 `useState`：
```jsx
import React, { useState } from 'react';
```

##### 2. 声明状态
在函数组件内部调用 `useState`，语法为：
```jsx
const [状态变量, 更新状态的函数] = useState(初始值);
```
- `状态变量`：当前状态的值（可直接在组件中使用）。
- `更新状态的函数`：用于修改状态变量的函数（调用后会触发组件重新渲染）。
- `初始值`：状态的初始值（可以是任意类型：基本类型、对象、数组等）。


#### 三、示例：计数器组件
以一个简单的计数器为例，演示 `useState` 的基本使用：

```jsx
import React, { useState } from 'react';

function Counter() {
  // 声明状态：count（初始值为 0），setCount（更新 count 的函数）
  const [count, setCount] = useState(0);

  return (
    <div>
      <p>当前计数：{count}</p>
      {/* 点击按钮时调用 setCount 更新状态 */}
      <button onClick={() => setCount(count + 1)}>+1</button>
      <button onClick={() => setCount(0)}>重置</button>
    </div>
  );
}

export default Counter;
```

##### 关键点解释：
- `useState(0)` 初始化 `count` 为 `0`。
- `setCount` 是更新函数，调用时会触发组件重新渲染，显示新的 `count` 值。
- 每次点击按钮时，`count` 被更新，组件重新渲染最新状态。


#### 四、状态更新的注意事项
##### 1. 状态是不可变的（Immutable）
**不要直接修改状态变量**，必须通过 `更新函数` 来修改。例如：
```jsx
// 错误！直接修改状态变量不会触发重新渲染
count = 5; 

// 正确！通过 setCount 更新状态
setCount(5);
```

##### 2. 对象/数组类型的更新
如果状态是对象或数组，更新时需要**创建新副本**（避免直接修改原对象/数组）：
```jsx
// 错误：直接修改原对象（React 无法检测到变化）
const [user, setUser] = useState({ name: '张三', age: 20 });
user.age = 21; 
setUser(user); // 不会触发重新渲染！

// 正确：创建新对象（浅拷贝）
setUser({ ...user, age: 21 }); // 或 Object.assign({}, user, { age: 21 })

// 数组同理（浅拷贝）
const [list, setList] = useState([1, 2, 3]);
setList([...list, 4]); // 添加新元素
setList(list.filter(item => item !== 2)); // 删除元素
```

##### 3. 异步更新的特性
`setState`（或 `setCount` 等更新函数）是**异步执行**的，连续多次调用可能不会立即生效。例如：
```jsx
// 错误：连续两次更新可能合并为一次
setCount(count + 1);
setCount(count + 1); // 最终 count 只会增加 1（因为两次都基于旧的 count 值）

// 正确：使用函数式更新（基于最新状态）
setCount(prevCount => prevCount + 1);
setCount(prevCount => prevCount + 1); // 最终 count 增加 2
```
**函数式更新**（`setX(prev => ...)`）适用于依赖前一次状态的场景（如异步操作或多次连续更新）。


#### 五、多个状态的声明
一个组件中可以声明**多个独立的状态**，每个状态由自己的 `useState` 管理：
```jsx
function UserInfo() {
  // 状态 1：姓名（字符串）
  const [name, setName] = useState('张三');
  // 状态 2：年龄（数字）
  const [age, setAge] = useState(20);
  // 状态 3：爱好（数组）
  const [hobbies, setHobbies] = useState(['阅读', '编程']);

  return (
    <div>
      <p>姓名：{name}</p>
      <input 
        value={name} 
        onChange={(e) => setName(e.target.value)} // 双向绑定输入框
      />
      
      <p>年龄：{age}</p>
      <button onClick={() => setAge(age + 1)}>年龄+1</button>

      <p>爱好：{hobbies.join(', ')}</p>
      <button onClick={() => setHobbies([...hobbies, '运动'])}>添加爱好</button>
    </div>
  );
}
```


#### 六、初始值的动态计算
如果初始值需要**动态计算**（例如从本地存储读取），可以将初始值设置为一个函数：
```jsx
// 初始值通过函数计算（仅在组件首次渲染时执行）
const [user, setUser] = useState(() => {
  const savedUser = localStorage.getItem('user');
  return savedUser ? JSON.parse(savedUser) : { name: '默认用户' };
});
```


#### 七、总结
`useState` 的核心用法：
1. 导入 `useState`。
2. 在函数组件中调用 `useState(初始值)`，解构得到状态变量和更新函数。
3. 通过更新函数修改状态（基本类型直接传新值，对象/数组需创建新副本）。
4. 状态变化触发组件重新渲染。

**最佳实践**：

- 保持状态局部化（仅需要的组件管理自己的状态）。
- 使用不可变数据（避免直接修改原状态）。
- 复杂状态逻辑可考虑使用 `useReducer` 或自定义 Hook。

#### 八、实验

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/%E7%BB%84%E4%BB%B6%E7%9A%84%E5%87%BD%E6%95%B0%E5%BC%8F%E5%AE%9A%E4%B9%89)

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

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        // 在脚手架创建的项目中，通过 import 导入 useState 函数，如下
        // import { useState } from 'react'
        
        function MyCompoent() {
            const [value, setValue] = React.useState('Hello React!')
            return (
                <div>
                    <div>{value}</div>
                    <div>
                        <button onClick={(e)=>{
                            setValue("Hello React!!!!!!!!!!!!")
                        }}>修改变量的值为：Hello React!!!!!!!!!!!!</button>
                        </div>
                </div>
            )
        }

        // 渲染自定义组件到页面容器中
        ReactDOM.render(<MyCompoent />, document.getElementById("test"))
    </script>
</body>

</html>
```



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



## `JSX`语法规则

### 什么是`JSX`

>[参考维基百科](https://en.wikipedia.org/wiki/JSX_(JavaScript))

`JSX`（`JavaScript XML`，正式名称为 `JavaScript Syntax eXtension`）是一种 `JavaScript` 扩展，允许使用类似 `XML` 的语法创建文档对象模型 (`DOM`) 树。 [1] `JSX` 最初由 `Facebook` 创建，用于与 `React` 一起使用，现已被多个 `Web` 框架采用。[2]: 5 [3]: 11  作为一种语法糖，`JSX` 通常被转换为嵌套的 `JavaScript` 函数调用，其结构与原始 `JSX` 类似。

### 创建虚拟`DOM`语法

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/jsx%E8%AF%AD%E6%B3%95%E8%A7%84%E5%88%99/%E5%88%9B%E5%BB%BA%E8%99%9A%E6%8B%9Fdom%E8%AF%AD%E6%B3%95)

`index.html`

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

    <script type="text/javascript" src="../../js/react.development.js" ></script>
    <script type="text/javascript" src="../../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../../js/babel.min.js" ></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        const vdom = (
            <h1>Hello, React</h1>
        )

        // 渲染虚拟dom到页面容器中
        ReactDOM.render(vdom, document.getElementById("test"))
    </script>
</body>
</html>
```



### 插入`JS`表达式

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/jsx%E8%AF%AD%E6%B3%95%E8%A7%84%E5%88%99/%E6%8F%92%E5%85%A5js%E8%A1%A8%E8%BE%BE%E5%BC%8F)

`index.html`

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

    <script type="text/javascript" src="../../js/react.development.js" ></script>
    <script type="text/javascript" src="../../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../../js/babel.min.js" ></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        const myId = "aBC123"
        const myData = "Hello, React"

        const items = ["Angular","ReactJS", "Vue"]

        const vdom = (
            <div>
                <h1 id={myId.toLowerCase()}>{myData.toLowerCase()}</h1>
                <hr/>
                <div>
                    <h1>{myData}</h1>
                    <ul>
                        {
                            // 使用 {} 插入 JS 表达式
                            items.map(function(item, index) {
                                return <li key={index}>{item}</li>
                            })
                        }
                    </ul>
                </div>
            </div>
        )

        // 渲染虚拟dom到页面容器中
        ReactDOM.render(vdom, document.getElementById("test"))
    </script>
</body>
</html>
```



### 设置虚拟`DOM`的`class`属性

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/jsx%E8%AF%AD%E6%B3%95%E8%A7%84%E5%88%99/%E8%AE%BE%E7%BD%AE%E8%99%9A%E6%8B%9Fdom%E7%9A%84class%E5%B1%9E%E6%80%A7)

`index.html`

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style type="text/css">
        .title {
            background-color: orange;
        }
    </style>
</head>
<body>
    <!-- reactjs追加dom的容器 -->
    <div id="test"></div>

    <script type="text/javascript" src="../../js/react.development.js" ></script>
    <script type="text/javascript" src="../../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../../js/babel.min.js" ></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        const myId = "aBC123"
        const myData = "Hello, React"

        const vdom = (
            /* 使用className设置class属性*/
            <h1 className="title" id={myId.toLowerCase()}>{myData.toLowerCase()}</h1>
        )

        // 渲染虚拟dom到页面容器中
        ReactDOM.render(vdom, document.getElementById("test"))
    </script>
</body>
</html>
```



### 设置虚拟`DOM`的`style`属性

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/%E9%9D%9E%E8%84%9A%E6%89%8B%E6%9E%B6/jsx%E8%AF%AD%E6%B3%95%E8%A7%84%E5%88%99/%E8%AE%BE%E7%BD%AE%E8%99%9A%E6%8B%9Fdom%E7%9A%84style%E5%B1%9E%E6%80%A7)

`index.html`

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

    <script type="text/javascript" src="../../js/react.development.js" ></script>
    <script type="text/javascript" src="../../js/react-dom.development.js" ></script>
    <script type="text/javascript" src="../../js/babel.min.js" ></script>

    <!-- 因为使用jsx语法编写虚拟dom代码，所以这里type必须为text/babel -->
    <script type="text/babel">
        const myId = "aBC123"
        const myData = "Hello, React"

        const vdom = (
            /* 设置 style 属性 */
            <h1 style={{backgroundColor: "orange"}} id={myId.toLowerCase()}>{myData.toLowerCase()}</h1>
        )

        // 渲染虚拟dom到页面容器中
        ReactDOM.render(vdom, document.getElementById("test"))
    </script>
</body>
</html>
```



### `React`片段

在 JSX（React 的语法扩展）中，`<></>` 是 **Fragment（片段）的短语法**，用于在不向 DOM 中添加额外父节点的情况下，包裹多个子元素。它的核心作用是解决 JSX 要求“单一根元素”的限制，同时保持 DOM 结构的简洁。


#### 一、为什么需要 Fragment？
JSX 有一个重要规则：**一个组件或表达式的返回值必须有且仅有一个根元素**。例如，以下代码会直接报错：
```jsx
// 错误：JSX 表达式必须有一个单一根元素
return (
  <h1>标题</h1>
  <p>内容</p>
);
```
此时，`Fragment` 就像一个“隐形容器”，可以包裹多个子元素，使 JSX 满足“单一根元素”的要求，同时**不会在最终渲染的 DOM 中生成任何实际节点**。


#### 二、`<></>` 的两种形式
React 支持两种 Fragment 写法：


##### 1. 短语法（`<></>`）
最简洁的写法，用空标签 `<></>` 包裹子元素：
```jsx
return (
  <>  {/* 短语法 Fragment */}
    <h1>标题</h1>
    <p>内容</p>
  </>
);
```
**特点**：
- 语法简洁，无需导入 `React`。
- **不支持属性**（如 `key`、`className` 等），仅用于纯包裹场景。


##### 2. 显式语法（`<React.Fragment>`）
用完整的 `<React.Fragment>` 标签包裹子元素：
```jsx
import React from 'react';

return (
  <React.Fragment>  {/* 显式 Fragment */}
    <h1>标题</h1>
    <p>内容</p>
  </React.Fragment>
);
```
**特点**：
- 支持添加属性（如 `key`、`className` 等），适用于需要为片段添加元信息的场景（例如列表渲染时的 `key`）。
- 在 React 开发工具中会显示为 `<Fragment>` 标签（但 DOM 中仍无实际节点）。


#### 三、Fragment 的渲染结果
无论是短语法还是显式语法，Fragment **最终不会生成任何 DOM 节点**。例如：
```jsx
// JSX 代码
return (
  <React.Fragment>
    <span>A</span>
    <span>B</span>
  </React.Fragment>
);

// 渲染后的 DOM 结构
<span>A</span>
<span>B</span>
```
而如果不用 Fragment，直接返回两个 `<span>` 会报错；如果用 `<div>` 包裹，则 DOM 中会多出一个 `<div>` 节点：
```jsx
// 用 div 包裹（会生成额外 DOM 节点）
return (
  <div>
    <span>A</span>
    <span>B</span>
  </div>
);

// 渲染后的 DOM 结构
<div>
  <span>A</span>
  <span>B</span>
</div>
```


#### 四、使用场景
Fragment 适用于以下场景：


##### 1. 列表渲染（需 `key` 时）
当需要渲染列表且每个列表项需要 `key` 时，Fragment 可以作为父容器并传递 `key`：
```jsx
const List = ({ items }) => (
  <ul>
    {items.map((item) => (
      <React.Fragment key={item.id}>  {/* Fragment 作为父容器并传递 key */}
        <li>{item.name}</li>
        <li>{item.desc}</li>
      </React.Fragment>
    ))}
  </ul>
);
```
此时若用短语法 `<></>`，则无法传递 `key`，会导致 React 报错（列表项必须有唯一 `key`）。


##### 2. 避免额外 DOM 节点
当需要分组元素但不想影响布局时（例如样式受父容器限制），Fragment 可以保持 DOM 结构干净：
```jsx
return (
  <div className="container">
    {/* 用 Fragment 包裹，避免额外 div 影响样式 */}
    <>
      <h2>子标题</h2>
      <p>子内容</p>
    </>
  </div>
);
```


##### 3. 条件渲染多个元素
在条件渲染中，若需要根据条件返回多个元素，Fragment 可以避免额外的父节点：
```jsx
return (
  <div>
    {isLoggedIn ? (
      <>  {/* 条件满足时返回多个元素 */}
        <button>退出登录</button>
        <span>欢迎回来！</span>
      </>
    ) : (
      <button>登录</button>
    )}
  </div>
);
```


#### 五、注意事项
- **短语法不支持属性**：`<></>` 不能添加 `key`、`className` 等属性，必须用 `<React.Fragment>` 替代。
- **嵌套限制**：Fragment 可以嵌套使用，但通常没有必要（除非需要多层分组）。
- **性能优化**：Fragment 不会生成 DOM 节点，因此在频繁更新的场景中（如列表渲染）比 `<div>` 更高效。


#### 总结
`<></>` 是 React 中 Fragment 的短语法，用于在不添加额外 DOM 节点的情况下包裹多个子元素，解决 JSX“单一根元素”的限制。它适用于列表渲染、条件渲染、避免额外布局节点等场景，是 React 开发中保持 DOM 结构简洁的重要工具。



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

#### 类式组件 - 标准用法

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



#### 类式组件 - 精简用法

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



#### 函数式组件 - 用法

>请参考本站 <a href="/react/README.html#八、实验" target="_blank">链接</a>



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

### 基本用法

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



### 阻止事件默认行为

>阻止 `a` 标签点击后默认跳转到指定 `url` 行为。

```jsx
{
  currentPath !== '/' && currentPath !== '/productList' && (
    <>&nbsp;&nbsp;<a href="#" onClick={(e)=>{
      // 阻止 a 标签点击默认跳转行为
      e.preventDefault()
      // 返回上一页
      history.goBack()
    }}>返回</a></>
  )
}
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

/* 用于协助测试嵌套样式引用 */
.mystyle .nested-1 {
    color: orange;
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
                {/* 用于协助测试嵌套样式引用 */}
                <div className={component1css['nested-1']}>Component1</div>
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

>说明：全局作用域（可能造成样式冲突）。
>
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

新建 `src/setupProxy.js`

```js
const { createProxyMiddleware } = require("http-proxy-middleware")

// https://create-react-app.dev/docs/proxying-api-requests-in-development/
module.exports = function (app) {
    app.use(
        createProxyMiddleware("/api/v1", {
            target: "https://httpbin.org",
            // 需要修改请求头中的Host值为httpbin.org
            changeOrigin: true,
            pathRewrite: { "^/api/v1": "" },
        }),
        createProxyMiddleware("/api/v2", {
            target: "https://httpbin.org",
            changeOrigin: true,
            pathRewrite: { "^/api/v2": "" },
        })
    )
}

```

`axios` 请求

```js
import axios from 'axios'

handleOnClick2 = () => {
    axios.get("/api/v2/get")
      .then(
        response => {
          console.log(response)
        },
        error => {
          console.error(error)
        }
      )
  }
```

`axios` 发出的请求被 `http proxy` 代理到 `https://httpbin.org`。



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

编程式路由主要通过调用`this.props.history.push(``/home/message/detail/${id}/${content}``)`、`this.props.history.replace(``/home/message/detail/${id}/${content}``)`接口实现。



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

  

### 返回上一页

在 React 中实现“返回上一页”的功能，主要依赖浏览器历史记录 API 或路由库（如 React Router）提供的导航方法。以下是具体实现方式和场景说明：


#### 一、使用 React Router（推荐，SPA 场景）
如果项目使用 **React Router** 管理路由（主流 SPA 方案），推荐通过路由库提供的导航方法实现返回上一页，确保与路由状态同步。


##### 1. React Router v6 及以上（使用 `useNavigate` 钩子）
React Router v6 推荐使用 `useNavigate` 钩子替代旧版的 `useHistory`，它提供了更灵活的导航控制。

**步骤**：
1. 导入 `useNavigate`；
2. 在组件中调用 `navigate(-1)` 触发返回上一页。

**代码示例**：
```jsx
import { useNavigate } from 'react-router-dom';

function BackButton() {
  // 获取导航函数（navigate）
  const navigate = useNavigate();

  // 返回上一页（等价于浏览器后退）
  const handleBack = () => {
    navigate(-1); // -1 表示后退一步，1 表示前进一步
  };

  return (
    <button onClick={handleBack}>
      返回上一页
    </button>
  );
}

export default BackButton;
```


##### 2. React Router v5 及以下（使用 `useHistory` 钩子或 `withRouter`）
React Router v5 中使用 `useHistory` 钩子获取 `history` 对象，或通过 `withRouter` 高阶组件注入 `history`。

**方式 1：`useHistory` 钩子（函数组件）**：
```jsx
import { useHistory } from 'react-router-dom';

function BackButton() {
  const history = useHistory();

  const handleBack = () => {
    history.goBack(); // 等价于 history.go(-1)
  };

  return (
    <button onClick={handleBack}>
      返回上一页
    </button>
  );
}
```

**方式 2：`withRouter` 高阶组件（类组件）**：
```jsx
import { withRouter } from 'react-router-dom';

class BackButton extends React.Component {
  handleBack = () => {
    this.props.history.goBack();
  };

  render() {
    return (
      <button onClick={this.handleBack}>
        返回上一页
      </button>
    );
  }
}

// 使用 withRouter 注入 history 对象
export default withRouter(BackButton);
```


#### 二、不使用路由库（原生浏览器 API）
如果项目未使用 React Router（如简单页面），可以直接调用浏览器原生的 `history` 对象实现返回。


##### 1. `history.back()`
直接调用 `window.history.back()`，等价于用户点击浏览器“后退”按钮。

**代码示例**：
```jsx
function BackButton() {
  const handleBack = () => {
    window.history.back(); // 返回上一页
  };

  return (
    <button onClick={handleBack}>
      返回上一页
    </button>
  );
}
```


##### 2. `history.go(-1)`
`history.go(n)` 可以跳转到历史记录中相对于当前页面的第 `n` 步（`n=-1` 表示后退一步，`n=1` 表示前进一步）。

**代码示例**：
```jsx
function BackButton() {
  const handleBack = () => {
    window.history.go(-1); // 等价于 history.back()
  };

  return (
    <button onClick={handleBack}>
      返回上一页
    </button>
  );
}
```


#### 三、注意事项
1. **SPA 路由同步**：  
   在 React Router 管理的 SPA 中，优先使用路由库的 `navigate` 或 `history` 方法，因为它们会同步更新路由状态（如匹配的路由组件、参数等），避免页面状态与路由不一致。

2. **历史记录为空的情况**：  
   如果用户直接访问当前页面（历史记录仅有当前页），调用 `back()` 或 `go(-1)` 会跳转到浏览器历史的上一个记录（可能是其他网站）。如需避免这种情况，可以添加判断：
   ```jsx
   // React Router v6 中判断是否可后退
   const navigate = useNavigate();
   const canGoBack = window.history.length > 1; // 历史记录长度大于 1 时可后退
   
   const handleBack = () => {
     canGoBack ? navigate(-1) : navigate('/'); // 不可后退时跳转到首页
   };
   ```

3. **类组件与函数组件**：  
   React Router v6 仅支持函数组件使用 `useNavigate`，类组件需通过包装组件或升级到函数组件实现。


#### 总结
- **推荐方案**：使用 React Router 的 `useNavigate`（v6）或 `useHistory`（v5），与路由状态深度集成。  
- **简单场景**：直接使用 `window.history.back()` 或 `go(-1)`，适合未使用路由库的小型项目。  
- **边界处理**：判断历史记录长度，避免无后退路径时跳转到外部页面。



#### 示例

```jsx
{
  currentPath !== '/' && currentPath !== '/productList' && (
    <>&nbsp;&nbsp;<a href="#" onClick={(e)=>{
      // 阻止 a 标签点击默认跳转行为
      e.preventDefault()
      // 返回上一页
      history.goBack()
    }}>返回</a></>
  )
}
```



## 综合案例 `demo-github-search`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-reactjs/demo-github-search)

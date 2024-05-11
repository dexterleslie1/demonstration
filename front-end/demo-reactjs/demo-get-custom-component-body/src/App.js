import logo from './logo.svg';
import './App.css';

import MyComponent from './components/MyComponent';

function App() {
  return (
    // 自定义 MyComponent 组件并传递一些标签体内容
    <MyComponent>
      <a href="https://www.baidu.com" target="_blank">跳转到百度</a>
    </MyComponent>
  );
}

export default App;

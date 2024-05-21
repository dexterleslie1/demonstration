import logo from './logo.svg';
import './App.css';
import Component1 from './components/Component1';
import Component2 from './components/Component2';
import ComponentScss from './components/ComponentScss';

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
      <Component1 />
      <Component2 />
      <ComponentScss/>
    </div>
  );
}

export default App;

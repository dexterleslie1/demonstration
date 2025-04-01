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

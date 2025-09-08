import logo from './logo.svg';
import './App.css';
import axios from './api/axios';

function App() {
  return (
    <div className="App">
      <button onClick={(e)=>{
        axios.get("/api/v1/get", {
          needHeader: true,
          params: { param1: "Dexterleslie0" },
          headers: { header1: "my-header1", header2: 'my-header2' }
        }).then((data) => {
          alert(JSON.stringify(data))
        }).catch(function (error) {
          alert(JSON.stringify(error))
        })
      }}>测试</button>
    </div>
  );
}

export default App;

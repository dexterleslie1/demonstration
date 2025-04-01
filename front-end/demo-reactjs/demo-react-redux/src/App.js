import logo from './logo.svg';
import './App.css';
import Count from './containers/Count';
import Person from './containers/Person'

function App() {
  return (
    <div className="App">
      <Count />
      <hr/>
      <Person/>
    </div>
  );
}

export default App;

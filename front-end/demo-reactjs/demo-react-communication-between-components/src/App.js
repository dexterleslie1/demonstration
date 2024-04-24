import ChildPub from './components/ChildPub';
import ChildSub from './components/ChildSub';

import logo from './logo.svg';
import './App.css';

function App() {
  // pubsub-js 用法
  // https://www.npmjs.com/package/pubsub-js
  return (
    <div className="App">
      <ChildPub/>
      <ChildSub/>
    </div>
  );
}

export default App;

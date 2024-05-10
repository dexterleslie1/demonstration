import logo from './logo.svg';
import './App.css';

import { NavLink, Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import About from './components/About';

function App() {
  // 添加 react-router-dom 依赖

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
    }}>
      {/* header */}
      <div style={{
        // backgroundColor: 'yellow',
        width: '100%',
        borderBottom: '1px solid black',
      }}>
        Demo React Router
      </div>
      {/* main content */}
      <div style={{
        display: 'flex',
        flexDirection: 'row',
        width: '100%',
        // backgroundColor: 'greenyellow'
      }}>
        {/* nav */}
        <div style={{
          flex: 3,
          // backgroundColor: 'green'
          display: 'flex',
          flexDirection: 'column',
          borderRight: '1px solid black',
        }}>
          <NavLink className={({ isActive }) => { return isActive ? 'my-active' : 'none' }} to="/">Home</NavLink>
          <NavLink className={({ isActive }) => { return isActive ? 'my-active' : 'none' }} to="/about">About</NavLink>
        </div>
        {/* content */}
        <div style={{
          flex: 7,
          // backgroundColor: 'red'
        }}>
          <Routes>
            <Route path='/' Component={Home} />
            <Route path='/about' Component={About} />
          </Routes>
        </div>
      </div>
    </div>
  );
}

export default App;

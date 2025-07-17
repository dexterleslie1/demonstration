import logo from './logo.svg';
import './App.css';

import { NavLink, Route, Switch } from 'react-router-dom';
import Home from './components/Home';
import About from './components/About';
import ReactFunctionComponent from './components/ReactFunctionComponent';

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
          <NavLink exact activeClassName='my-active' to="/">Home</NavLink>
          <NavLink exact activeClassName='my-active' to="/about">About</NavLink>
          <NavLink exact activeClassName='my-active' to="/reactFunctionComponent">React Function Component</NavLink>
        </div>
        {/* content */}
        <div style={{
          flex: 7,
          // backgroundColor: 'red'
        }}>
          <Switch>
            {/* exact 强制路由进行​​精确匹配​​（只有路径完全一致时才渲染对应组件） */}
            <Route exact path='/' component={Home} />
            <Route exact path='/about' component={About} />
            <Route exact path='/reactFunctionComponent' component={ReactFunctionComponent} />
          </Switch>
        </div>
      </div>
    </div>
  );
}

export default App;

import logo from './logo.svg';
import './App.css';

import { NavLink, Route, Switch, useLocation, useHistory } from 'react-router-dom';

import Login from './components/Login';
import ProductList from './components/ProductList';
import ListByUserId from './components/ListByUserId';
import ListByMerchantId from './components/ListByMerchantId';
import ProductInfo from './components/ProductInfo'
import CreateProduct from './components/CreateProduct'

function App() {
  // 获取当前路由的 location 对象（包含 pathname）
  const location = useLocation();
  // 当前路径（例如：'/', '/about'）
  const currentPath = location.pathname;

  const history = useHistory()

  return (
    <div className="App">
      {/* 顶部显示栏 */}
      {
        // 不为登录页面才显示
        currentPath !== '/' && (
          <div style={{
            marginTop: '10px'
          }}>
            当前用户ID：{localStorage.getItem("userId")}，当前商家ID：{localStorage.getItem("merchantId")}
            &nbsp;&nbsp;<a href="/">首页</a>
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
          </div>
        )
      }
      <div style={{
        marginTop: '10px'
      }}>
        <Switch>
          {/* exact 强制路由进行​​精确匹配​​（只有路径完全一致时才渲染对应组件） */}
          <Route exact path='/' component={Login} />
          <Route exact path='/productList' component={ProductList} />
          <Route exact path='/listByUserId' component={ListByUserId} />
          <Route exact path='/listByMerchantId' component={ListByMerchantId} />
          <Route exact path='/productInfo' component={ProductInfo} />
          <Route exact path='/createProduct' component={CreateProduct} />
        </Switch>
      </div>
    </div>
  );
}

export default App;

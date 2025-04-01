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

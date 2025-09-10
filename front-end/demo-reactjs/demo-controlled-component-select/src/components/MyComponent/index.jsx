import React, { Component } from 'react'

export default class MyComponent extends Component {

    state = {
        status: '',
    }

    render() {
        return (
            <div>
                <select value={this.state.status/* 读取 state 中的值 */} onChange={(e) => {
                    // 更新 state 中的值
                    this.setState({ status: e.target.value })
                }}>
                    <option value="">全部</option>
                    <option value="Unpay">未支付</option>
                    <option value="Canceled">已取消</option>
                </select>
                <br />
                <button onClick={(e) => { alert(this.state.status) }}>显示state中的status值</button>
            </div>
        )
    }
}

import React, { Component } from 'react'
import store from '../../redux/store'
import {
    createIncrementAction,
    createDecrementAction,
    createIncrementAsyncAction
} from '../../redux/actions/count'

export default class Count extends Component {

    handleAdd = () => {
        const { value } = this.selectNumber
        // 通知redux执行reducer中的加法
        store.dispatch(createIncrementAction(parseInt(value)))
    }
    handleMinus = () => {
        const { value } = this.selectNumber
        // 通知redux执行reducer中的减法
        store.dispatch(createDecrementAction(parseInt(value)))
    }
    handleAddOdd = () => {
        const { value } = this.selectNumber
        const intValue = parseInt(value)
        if (intValue % 2 != 0) {
            store.dispatch(createIncrementAction(intValue))
        }
    }
    handleAddAsync = () => {
        const { value } = this.selectNumber
        // 通知redux执行reducer中的异步加法
        store.dispatch(createIncrementAsyncAction(parseInt(value), 1000))
    }

    render() {
        return (
            <div>
                <div>当前求和：{store.getState()/* 获取redux状态 */}</div>
                <select ref={c => this.selectNumber = c}>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                </select>&nbsp;
                <button onClick={this.handleAdd}>+</button>&nbsp;
                <button onClick={this.handleMinus}>-</button>&nbsp;
                <button onClick={this.handleAddOdd}>当前求和为基数再加</button>&nbsp;
                <button onClick={this.handleAddAsync}>异步加</button>
            </div>
        )
    }
}

import { Component } from 'react'

import {
    createDecrementAction,
    createIncrementAction,
    createIncrementAsyncAction
} from '../../redux/actions/count'

import { connect } from 'react-redux'

class Count extends Component {

    handleAdd = () => {
        const { value } = this.selectNumber
        this.props.jia(parseInt(value))
    }
    handleMinus = () => {
        const { value } = this.selectNumber
        this.props.jian(parseInt(value))
    }
    handleAddOdd = () => {
        const { value } = this.selectNumber
        const intValue = parseInt(value)
        if (intValue % 2 != 0) {
            this.props.jia(intValue)
        }
    }
    handleAddAsync = () => {
        const { value } = this.selectNumber
        this.props.jiaAsync(parseInt(value), 1000)
    }

    render() {
        return (
            <div>
                <h1>Count组件，下面组件人数：{this.props.personCount}</h1>
                <div>当前求和：{this.props.count}</div>
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


function mapStateToProps(state/* redux的状态对象 */) {
    // 映射redux状态对象数据myCount到props，子组件使用props.count使用此数据
    return { count: state.myCount, personCount:state.myPerson.length }
}

function mapDispatchToProps(dispatch) {
    // 映射redux dispatch数据操作函数到props，子组件使用props.funCall调用此函数
    return {
        jia: (data) => dispatch(createIncrementAction(data)),
        jian: (data) => dispatch(createDecrementAction(data)),
        jiaAsync: (data, time) => dispatch(createIncrementAsyncAction(data, time))
    }
}

// 导出CountUI的react-redux容器
export default connect(mapStateToProps, mapDispatchToProps)(Count)

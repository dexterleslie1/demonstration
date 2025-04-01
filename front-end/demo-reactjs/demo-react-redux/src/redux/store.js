import { createStore, applyMiddleware, combineReducers } from 'redux'
import countReducer from "./reducers/count"
import personReducer from "./reducers/person"
// redux支持异步action
import { thunk } from "redux-thunk"
import { composeWithDevTools } from '@redux-devtools/extension';

// 定义redux状态对象的数据结构{myCount:...,myPerson:...}
// 使用redux状态对象中数据state.myCount
const allInOneReducers = combineReducers({
    myCount: countReducer,
    myPerson: personReducer
})

// 暴露redux store，整个应用只有一个store
export default createStore(allInOneReducers, composeWithDevTools(applyMiddleware(thunk)/* redux支持异步action */)/* 激活浏览器redux-devtools */)

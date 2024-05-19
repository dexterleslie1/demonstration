import { createStore, applyMiddleware } from 'redux'
import countReducer from "./reducers/count"
// redux支持异步action
import { thunk } from "redux-thunk"

// 暴露redux store，整个应用只有一个store
export default createStore(countReducer, applyMiddleware(thunk)/* 使redux支持异步action */)

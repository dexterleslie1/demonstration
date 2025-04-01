import { INCREMENT, DECREMENT } from "../const"

// redux状态中的count的初始值
const initState = 0
export default function countReducer(preState = initState, action) {
    const { type, data } = action
    switch (type) {
        // 加法
        case INCREMENT:
            return preState + data
        // 减法
        case DECREMENT:
            return preState - data
        default:
            // 初始化时候执行
            return preState
    }
}
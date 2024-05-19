import { INCREMENT, DECREMENT } from "../const"

// 同步action
export const createIncrementAction = (data) => ({ type: INCREMENT, data: data })
export const createDecrementAction = (data) => ({ type: DECREMENT, data: data })

// 异步action
export const createIncrementAsyncAction = (data, time) => {
    return (dispatch) => {
        setTimeout(() => {
            dispatch({ type: INCREMENT, data: data })
        }, time)
    }
}

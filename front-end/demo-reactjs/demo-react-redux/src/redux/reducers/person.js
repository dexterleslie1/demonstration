import { ADDPERSON } from "../const";

const initState = [{ id: '01', name: '张三', age: 18 }]
export default function personReducer(preState = initState, action) {
    const { type, data } = action
    switch (type) {
        case ADDPERSON:
            return [data, ...preState]
        default:
            return preState
    }

}

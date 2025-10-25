export default {
    namespaced: true,
    state: {
        count: 0,
        loginStatus: ''
    },
    getters: {
        countPower(state) {
            return function (powerCount) {
                let returnValue = 1
                for (let i = 0; i < powerCount; i++) {
                    returnValue = returnValue * state.count
                }
                return returnValue
            }
        }
    },
    // mutations中不编写业务逻辑，只是简单地修改state中数据
    mutations: {
        add(state) {
            state.count++
        },
        sub(state) {
            state.count--
        },

        addWithSingleParameter(state, step) {
            state.count = state.count + step
        },
        subWithSingleParameter(state, step) {
            state.count = state.count - step
        },

        addWithMultipleParameter(state, payload) {
            state.count = state.count + payload.step1 + payload.step2
        },
        subWithMultipleParameter(state, payload) {
            state.count = state.count - payload.step1 - payload.step2
        }
    },
    // actions中可以编写复杂的业务逻辑或者调用后端接口
    actions: {
        // 模拟登录
        login(context, payload) {
            context.state.loginStatus = '正在登录中'
            return new Promise(function (resolve, reject) {
                // 模拟请求登录接口
                setTimeout(function () {
                    var randomBool = Math.random() >= 0.5
                    if (randomBool) {
                        context.state.loginStatus = `登录成功参数${JSON.stringify(payload)}`
                        resolve()
                    } else {
                        context.state.loginStatus = `登录失败参数${JSON.stringify(payload)}`
                        reject("登录失败！")
                    }
                }, 2000)
            })
        }
    }
}
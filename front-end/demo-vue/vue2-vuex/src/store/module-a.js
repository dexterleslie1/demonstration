import Vue from 'vue'

export default {
    namespaced: true,
    state: {
        count: 0,
        loginStatus: ''
    },
    getters: {
        countPower(state) {
            return function(powerCount) {
                let returnValue = 1
                for(let i=0; i<powerCount; i++) {
                    returnValue = returnValue * state.count
                }
                return returnValue
            }
        }
    },
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
    actions: {
        login(context, payload) {
            context.state.loginStatus = '正在登录中'
            return new Promise(function(resolve, reject) {
                setTimeout(function() {
                    context.state.loginStatus = `登录参数${JSON.stringify(payload)}`
                    resolve()
                }, 2000)
            })
        }
    }
}
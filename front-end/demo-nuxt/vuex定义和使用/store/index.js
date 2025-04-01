export const state = function () {
    return {
        bNav: false,
        bLoading: false
    }
}

export const mutations = {
    M_UPDATE_NAV(state, payload) {
        state.bNav = payload
    },
    M_UPDATE_LOADING(state, palyload) {
        state.bLoading = palyload
    }
}

export const actions = {
    nuxtServerInit(context, store) {

    }
}

export const getters = {
    getNav(state) {
        return state.bNav ? '显示' : '隐藏'
    }
}

import Vue from 'vue'
import Vuex from 'vuex'

import MessageStore from './message'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    'message': MessageStore
  }
})

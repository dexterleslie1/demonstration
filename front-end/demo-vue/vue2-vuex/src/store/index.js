import Vue from 'vue'
import Vuex from 'vuex'

import ModuleA from './module-a'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    'moduleA': ModuleA
  }
})

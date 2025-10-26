import Vue from 'vue'
import Vuex from 'vuex'

import moduleA from './module-a'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    helloStr: "Hello World!"
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    moduleA
  }
})

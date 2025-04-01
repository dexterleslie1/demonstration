import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import ModuleState from '@/views/module-state'
import ModuleMutations from '@/views/module-mutations'
import ModuleGetters from '@/views/module-getters'
import ModuleActions from '@/views/module-actions'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'module-state',
    component: ModuleState
  },
  {
    path: '/module-mutations',
    name: 'module-mutations',
    component: ModuleMutations
  },
  {
    path: '/module-getters',
    name: 'module-getters',
    component: ModuleGetters
  },
  {
    path: '/module-actions',
    name: 'module-actions',
    component: ModuleActions
  },
  {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: function () {
      return import(/* webpackChunkName: "about" */ '../views/About.vue')
    }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router

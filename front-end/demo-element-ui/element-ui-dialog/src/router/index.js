import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'

import ElDialogDemo from '@/components/el-dialog-demo'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'el-dialog-demo',
    component: ElDialogDemo
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

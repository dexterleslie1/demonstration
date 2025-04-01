import Vue from 'vue'
import VueRouter from 'vue-router'
import ElImageLazy from '@/components/el-image-lazy'
import ElImageUsage from '@/components/el-image-usage'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'el-image-lazy',
    component: ElImageLazy
  },
  {
    path: '/el-image-usage',
    name: 'el-image-usage',
    component: ElImageUsage
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

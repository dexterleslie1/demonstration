import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Help from "@/views/Help";
import ComponentHelp from "@/views/ComponentHelp";

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/help',
    name: 'Help',
    component: Help,
    children: [
      {
        // 点击帮助菜单后默认跳转路由
        path: '',
        name: 'defaultHelp',
        // 以下两种写法等价
        // redirect: '/help/helpArticle?article=download',
        redirect: {name: 'helpArticle', query: {article: 'download'}}
      },
      {
        path: 'helpArticle',
        name: 'helpArticle',
        component: ComponentHelp
      }
    ]
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

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 导航守卫，每次导航都触发
router.beforeEach((to, from, next)=>{
  let fullPath = to.fullPath;
  console.log(`fullPath=${fullPath}`)
  next()
})

export default router

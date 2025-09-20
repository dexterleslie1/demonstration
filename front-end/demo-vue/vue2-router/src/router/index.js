import Vue from 'vue';
import VueRouter from 'vue-router';

// 引入目标页面组件
import Home from '@/components/Home.vue';
import About from '@/components/About.vue';

Vue.use(VueRouter);

// 定义路由规则
const routes = [
    {
        path: '/',          // 根路径
        name: 'Home',       // 路由名称（可选，用于编程式导航）
        component: Home     // 对应组件
    },
    {
        path: '/about',
        name: 'About',
        component: About
    }
];

// 创建路由实例
const router = new VueRouter({
    mode: 'history',      // 路由模式（hash 或 history，推荐 history）
    base: process.env.BASE_URL, // 基础路径（可选）
    routes              // 注册路由规则
});

export default router; // 导出路由实例
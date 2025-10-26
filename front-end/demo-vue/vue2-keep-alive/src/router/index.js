import Vue from 'vue';
import VueRouter from 'vue-router';

// 引入目标页面组件
import Component1 from '@/components/Component1.vue';
import Component2 from '@/components/Component2.vue';
import Component3 from '@/components/Component3.vue';

Vue.use(VueRouter);

// 定义路由规则
const routes = [
    {
        path: '/',          // 根路径
        name: 'RouterNameComponent1',       // 路由名称（可选，用于编程式导航）
        component: Component1     // 对应组件
    },
    {
        path: '/component2',
        name: 'RouterNameComponent2',
        component: Component2
    },
    {
        path: '/component3',
        name: 'RouterNameComponent3',
        component: Component3
    }
];

// 创建路由实例
const router = new VueRouter({
    mode: 'history',      // 路由模式（hash 或 history，推荐 history）
    base: process.env.BASE_URL, // 基础路径（可选）
    routes              // 注册路由规则
});

export default router; // 导出路由实例
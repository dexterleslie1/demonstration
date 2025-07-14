import Vue from 'vue';
import VueRouter from 'vue-router';

// 引入目标页面组件
import SessionInfo from '@/components/SessionInfo.vue'
import ProductList from '@/components/ProductList.vue'
import ProductInfo from '@/components/ProductInfo.vue'
import ListByUserId from '@/components/ListByUserId.vue'
import ListByMerchantId from '@/components/ListByMerchantId.vue'
import CreateProduct from '@/components/CreateProduct.vue'

Vue.use(VueRouter);

// 定义路由规则
const routes = [
    {
        path: '/',          // 根路径
        name: 'SessionInfo',       // 路由名称（可选，用于编程式导航）
        component: SessionInfo     // 对应组件
    },
    {
        path: '/productList',
        name: 'ProductList',
        component: ProductList
    },
    {
        path: '/productInfo',
        name: 'ProductInfo',
        component: ProductInfo
    },
    {
        path: '/listByUserId',
        name: 'ListByUserId',
        component: ListByUserId
    },
    {
        path: '/listByMerchantId',
        name: 'ListByMerchantId',
        component: ListByMerchantId
    }, {
        path: '/createProduct',
        name: 'CreateProduct',
        component: CreateProduct
    },
];

// 创建路由实例
const router = new VueRouter({
    mode: 'history',      // 路由模式（hash 或 history，推荐 history）
    base: process.env.BASE_URL, // 基础路径（可选）
    routes              // 注册路由规则
});

export default router; // 导出路由实例
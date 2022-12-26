import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Drawer from "@/views/Drawer";
import Scrollbar from "@/views/Scrollbar";
import Tabs from "@/views/Tabs";
import Container from "@/views/Container";

const routes = [{
  path: '/login',
  name: 'Home',
  component: Login
}, {
  path: '/drawer',
  name: "Drawer",
  component: Drawer
}, {
  path: '/scrollbar',
  name: "Scrollbar",
  component: Scrollbar
}, {
  path: '/tabs',
  name: "Tabs",
  component: Tabs
}, {
  path: '/container',
  name: "Container",
  component: Container
}]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router

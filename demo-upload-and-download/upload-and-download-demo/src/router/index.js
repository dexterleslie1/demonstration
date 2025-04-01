import { createRouter, createWebHistory } from 'vue-router'
import Stream from "../components/Stream";
import Slice from "../components/Slice";
import FileReaderApi from "../components/FileReaderApi";
import Download from "../components/Download"

const routes = [
  {
    path: '/',
    name: 'stream',
    component: Stream
  }, {
    path: '/slice',
    name: 'slice',
    component: Slice
  }, {
    path: '/download',
    name: 'Download',
    component: Download
  }, {
    path: '/fileReaderApi',
    name: 'fileReaderApi',
    component: FileReaderApi
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router

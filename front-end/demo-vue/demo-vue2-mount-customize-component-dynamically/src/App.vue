<template>
  <div id="app">
    <div>测试动态挂载自定义组件到指定 DOM 节点</div>
    <button @click="handleClickMountDynamically()">动态挂载</button>
    <div ref="mountPoint"></div>
    <hr />
  </div>
</template>

<script>
import MyComponent from '@/components/MyComponent.vue'
import Vue from 'vue'

export default {
  name: 'App',
  methods: {
    handleClickMountDynamically() {
      const MyComponentConstructor = Vue.extend(MyComponent)
      const instance = new MyComponentConstructor({
        propsData: { name: "Dexter" }
      })

      // 手动挂载到 DOM（挂载到 #mountPoint 内）
      instance.$mount();
      this.$refs.mountPoint.appendChild(instance.$el);

      // 监听关闭事件（销毁实例）
      instance.$on('close', () => {
        // 销毁组件实例
        instance.$destroy();
        // 从 DOM 移除
        instance.$el.parentNode.removeChild(instance.$el);
      });
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>

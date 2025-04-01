<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <hr>
    <div>vue组件生命周期演示</div>
  </div>
</template>

<script>
// https://m.php.cn/faq/486565.html
export default {
  beforeCreate() {
    // 在实例初始化之后，数据观测 (data observer) 和 event/watcher 事件配置之前被调用。初始化阶段，应用不多。
    console.log(`++++++++++++beforeCreate`)
  },
  created() {
    // 在实例创建完成后被立即调用。在这一步，实例已完成以下的配置：数据观测 (data observer)，property 和方法的运算，watch/event 事件回调。然而，挂载阶段还没开始，$el property 目前尚不可用。
    console.log(`++++++++++++created`)
  },
  beforeMount() {
    // 在挂载开始之前被调用：相关的 render 函数首次被调用。
    console.log(`++++++++++++beforeMount`)
  },
  mounted() {
    // 该阶段执行完了模板解析，以及挂载。同时组件根组件元素被赋给了 $el 属性，该阶段可以通过 DOM 操作来对组件内部元素进行处理了。
    console.log(`++++++++++++mounted`)
  },
  beforeUpdate() {
    // 数据更新时调用，但是还没有对视图进行重新渲染，这个时候，可以获取视图更新之前的状态。
    console.log(`++++++++++++beforeUpdate`)
  },
  updated() {
    // 由于数据的变更导致的视图重新渲染，可以通过 DOM 操作来获取视图的最新状态。
    console.log(`++++++++++++updated`)
  },
  beforeDestroy() {
    // 实例销毁之前调用，移除一些不必要的冗余数据，比如定时器。
    console.log(`++++++++++++beforeDestroy`)
  },
  destroyed() {
    // Vue 实例销毁后调用。
    console.log(`++++++++++++destroyed`)
  },
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>

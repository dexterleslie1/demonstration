<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <!-- vue mouse相关事件处理演示 -->
    <!-- https://juejin.cn/post/6999525569708687374 -->
    <hr>
    <div>mouseover：当鼠标移入元素或其子元素都会触发事件，所以有一个重复触发，冒泡过程。对应的移除事件是 mouseout</div>
    <div style="width:100px;height:100px;background-color: antiquewhite;" @mouseover="handleMouseover"
      @mouseout="handleMouseout">
      <div style="width:50px;height:50px;background-color: aqua;"></div>
    </div>

    <hr>
    <div>mouseenter:当鼠标移入元素本身（不包含元素的子元素）会触发事件，也就是不会冒泡。对应的移除事件是 mouseleave</div>
    <div style="width:100px;height:100px;background-color: antiquewhite;" @mouseenter="handleMouseenter"
      @mouseleave="handleMouseleave">
      <div style="width:50px;height:50px;background-color: aqua;"></div>
    </div>
  </div>
</template>

<script>

export default {
  methods: {
    handleMouseover() {
      console.log(`++++++++++mouseover`)
    },
    handleMouseout() {
      console.log(`++++++++++mouseout`)
    },

    handleMouseenter() {
      console.log(`++++++++++mouseenter`)
    },
    handleMouseleave() {
      console.log(`++++++++++mouseleave`)
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

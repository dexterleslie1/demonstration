<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <hr>
    <div>
      演示获取div宽度和高度<br>
      https://stackoverflow.com/questions/44948714/get-element-height-with-vuejs
    </div>
    <div>
      <div ref="myBox" style="width:98px;height:99px;background-color: antiquewhite;"></div>
    </div>
    <div>
      <button @click="handleClick">获取div宽度和高度</button>
    </div>
  </div>
</template>

<script>
export default {
  // NOTE: created不能引用this.$refs
  mounted() {
    console.log(`output from created width: ${this.$refs.myBox.offsetWidth},height: ${this.$refs.myBox.offsetHeight}`)
  },
  methods: {
    handleClick() {
      console.log(`output from handleClick width: ${this.$refs.myBox.offsetWidth},height: ${this.$refs.myBox.offsetHeight}`)
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

<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <div v-for="(item, index) in this.dataList">
      <button @click="handleClick(index)">点击我input聚焦</button>
      <input :ref="`input${index}`"/>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dataList: ['a', 'b', 'c']
    }
  },
  methods: {
    handleClick(index) {
      this.$refs[`input${index}`][0].focus()
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

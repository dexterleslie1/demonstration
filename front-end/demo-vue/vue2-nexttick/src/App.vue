<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <hr>
    <div>
      演示nexttick用法<br>
      https://www.jianshu.com/p/a7550c0e164f
    </div>
    <div>
      <div ref="msgDiv">{{ msg }}</div>
      <div v-if="msg1">Message got outside $nextTick: {{ msg1 }}</div>
      <div v-if="msg2">Message got inside $nextTick: {{ msg2 }}</div>
      <div v-if="msg3">Message got outside $nextTick: {{ msg3 }}</div>
      <button @click="changeMsg">
        Change the Message
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      msg: 'Hello Vue.',
      msg1: '',
      msg2: '',
      msg3: ''
    }
  },
  methods: {
    changeMsg() {
      this.msg = "Hello world."
      this.msg1 = this.$refs.msgDiv.innerHTML
      this.$nextTick(() => {
        this.msg2 = this.$refs.msgDiv.innerHTML
      })
      this.msg3 = this.$refs.msgDiv.innerHTML
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

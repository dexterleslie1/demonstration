<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <div>密码登录</div>
    验证码：<img :src="imageCaptcha">
    帐号：<input type="text" v-model="username" />
    密码：<input type="password" v-model="password" />
    <button @click="handleLoginWithPassword()">登录</button>
    <hr />
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    HelloWorld
  },
  data() {
    return {
      imageCaptcha: null,
      username: "admin",
      password: "123456",
    }
  },
  mounted() {
    this.$axios.get("/api/v1/auth/getCaptcha").then((data) => {
      this.imageCaptcha = data.image
    }).catch(function (error) {
      alert(JSON.stringify(error))
    })
  },
  methods: {
    handleLoginWithPassword() {
      this.$axios.post("/api/v1/auth/login", null, {
        params: {
          type: "password",
          principal: this.username,
          credentials: this.password,
        }
      }).then((data) => {
        alert(JSON.stringify(data))
      }).catch(function (error) {
        alert(JSON.stringify(error))
      })
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

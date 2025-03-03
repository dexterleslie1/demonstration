<template>
  <div>
    <hr />
    <div>$store.state.user.data.title: {{ $store.state.user.data.title }}</div>
    <div><button @click="setUserData()">修改vuex.user</button></div>

    <hr />
    <div>$store.state.home.data.title: {{ $store.state.home.data.title }}</div>
  </div>
</template>

<script>
export default {
  name: 'IndexPage',

  async fetch({ $axios, store }) {
    await $axios.get('/api/v1/test1')
      .then(function (response) {
        console.log(`my response=${JSON.stringify(response.data)}`)

        store.commit('home/M_UPDATE_HOME', { err: 0, data: { title: response.data.data } })
      }).catch(function (error) {
        console.log(`my error=${error}`)
      })
  },

  methods: {
    setUserData() {
      // 使用dispatch方式异步修改数据
      // this.$store.dispatch('user/A_UPDATE_USER', { err: 0, msg: '登录成功', token: '假token', data: { title: 'setUserData通过$store.dispatch提交的数据' } })

      // 使用commit方式同步修改数据
      this.$store.commit('user/M_UPDATE_USER', { err: 0, msg: '登录成功', token: '假token', data: { title: 'setUserData通过$store.commit提交的数据' } })
    },
  }
}
</script>

<template>
  <div>
    <button @click="handleClickSetCookie">设置cookie</button>
    <button @click="handleClickGetCookie">获取cookie</button>
    <button @click="handleClickClearCookie">清除cookie</button>
  </div>
</template>

<script>
export default {
  name: 'IndexPage',

  methods: {
    // https://blog.csdn.net/qq_37564189/article/details/123892113
    handleClickSetCookie() {
      this.$cookies.set('my-cookie', 'my-cookie-val', {path:'/'})
    },
    handleClickGetCookie() {
      let val = this.$cookies.get('my-cookie')
      console.log(`cookie my-cookie value: ${val}`)
    },
    handleClickClearCookie() {
      this.$cookies.removeAll()
      alert('成功清楚所有cookie')
    }
  }
}
</script>

<template>
  <div></div>
</template>

<script>
export default {
  // 运行在服务器端axios
  async asyncData({$axios}) {
    await $axios.get('/api/v1/test')
    .then(function(response) {
      console.log(`my response=${JSON.stringify(response.data)}`)
    }).catch(function(error) {
      console.log(`my error=${error}`)
    })
  },
  // 运行在服务器端axios
  async fetch({$axios}) {
    await $axios.get('/api/v1/test')
    .then(function(response) {
      console.log(`fetch my response=${JSON.stringify(response.data)}`)
    }).catch(function(error) {
      console.log(`fetch my error=${error}`)
    })
  },

  name: 'IndexPage'
}
</script>

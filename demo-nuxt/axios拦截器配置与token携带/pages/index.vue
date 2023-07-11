<template>
    <div>title: {{ title }}</div>
</template>

<script>
export default {
  async asyncData({$axios}) {
    let data
    await $axios.get('/api/v1/test1')
    .then(function(response) {
      console.log(`response=${JSON.stringify(response.data)}`)
      data = response
    })
    .catch(function(error) {
      console.log(`my error=${error}`)
    })
    .finally(function() {
      console.log(`my finally`)
    })

    return {
      title: data.data.data
    }
  },

  name: 'IndexPage'
}
</script>

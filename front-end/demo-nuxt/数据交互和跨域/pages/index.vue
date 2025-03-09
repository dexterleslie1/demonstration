<template>
    <div>
      <div>title: {{ title }}</div>
      <div>title1: {{ title1 }}</div>
    </div>
</template>

<script>
export default {
  data() {
    return {
      title1: ''
    }
  },

  // 使用asyncData方式读取数据
  async asyncData({$axios}) {
    // SSR读取同域数据
    let data = await $axios({'url': '/data/data.json'})

    // SSR读取跨域数据
    let dataCross
    await $axios.get('/api/v1/test1')
    .then(function(response) {
      dataCross = response
    }).catch(function(error) {
        console.log(`my error=${error}`)
    }).finally(function() {
        console.log(`/api/v1/test1 request finally`)
    })
    console.log(dataCross.data)

    // 通过return合并数据到vue中
    return {
      title: data.data.title
    }
  },

  // 使用fetch方式读取数据
  async fetch({$axios}) {
    let data = await $axios({'url': '/data/data.json'})
    // todo 设置title1不起作用
    this.title1 = data.data.title
  },

  name: 'IndexPage',
}
</script>

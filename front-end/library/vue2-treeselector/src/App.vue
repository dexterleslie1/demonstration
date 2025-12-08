<template>
  <div id="app">
    <treeselect v-model="value" :multiple="true" :options="options" placeholder="请选择" :default-expand-level="20"
      :normalizer="normalizer" />
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'

import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

export default {
  name: 'App',
  components: {
    HelloWorld,
    Treeselect
  },
  data() {
    return {
      value: null,
      options: [{
        myId: 'id-a',
        myLabel: 'a',
        children: [{
          myId: 'id-aa',
          myLabel: 'aa',
        }, {
          myId: 'id-ab',
          myLabel: 'ab',
        }],
      }, {
        myId: 'id-b',
        myLabel: 'b',
      }, {
        myId: 'id-c',
        myLabel: 'c',
      }],
    }
  },
  watch: {
    value(val) {
      console.log(`vue-treeselect的值：${val}`)
    }
  },
  methods: {
    // 转换业务对象到vue-treeselect需要的数据格式
    // node为每个树形组件每个节点的数据对象
    normalizer(node) {
      return {
        id: node.myId,
        label: node.myLabel,
        children: node.children,
      }
    },
  }
}
</script>

<style>
#app {}
</style>

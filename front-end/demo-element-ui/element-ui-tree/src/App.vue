<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <el-tree :data="treeData" :props="{ children: 'children', label: 'label' }" @node-click="handleClickNode"
      default-expand-all></el-tree>
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
      treeData: [
        {
          label: '1',
          orderNum: 1,
          children: [{
            label: '1-1',
            children: [{
              label: '1-1-1',
              orderNum: 1,
            }, {
              label: '1-1-2',
              orderNum: 2,
            }]
          }]
        }, {
          label: '2',
          orderNum: 2,
          children: [
            {
              label: '2-1',
              orderNum: 1,
            },
            {
              label: '2-2',
              orderNum: 2,
            }
          ]
        }
      ]
    }
  },
  methods: {
    handleClickNode(data) {
      console.log(`label=${data.label},orderNum=${data.orderNum}`)
    }
  }
}
</script>

<style></style>

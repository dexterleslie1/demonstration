<template>
  <div>
    <el-button type="primary" @click="handleClick">点击我</el-button>
  </div>
</template>

<script>
export default {
  name: 'IndexPage',
  
  methods: {
    handleClick() {
      alert('Hello world!')
    }
  }
}
</script>

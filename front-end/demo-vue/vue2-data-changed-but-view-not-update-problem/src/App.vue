<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->
    <hr>
    <div>动态为对象新增属性</div>
    <div>
      <button @click="handleClick1">点击我</button>
      <div v-for="(val, key, index) in obj1">
        index={{ index }},{{ key }}={{ val }}
      </div>
    </div>

    <hr>
    <div>动态删除对象属性</div>
    <div>
      <button @click="handleClick2">点击我</button>
      <div v-for="(val, key, index) in obj1">
        index={{ index }},{{ key }}={{ val }}
      </div>
    </div>

    <hr>
    <div>使用索引修改数组值</div>
    <div>
      <button @click="handleClick3">点击我</button>
      <div v-for="(item, index) in arr1">
        index={{ index }},{{ item }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      obj1: {
        key1: 'val1',
        key2: 'val2'
      },
      arr1: [1, 2, 3]
    }
  },
  methods: {
    handleClick1() {
      // 动态为对象添加属性不能自动更新到视图中
      this.obj1.key3 = '88888'

      // 解决方案1、使用this.$forceUpdate()
      // this.$forceUpdate()

      // 解决方案2、使用this.$set()
      // this.$set(this.obj1, 'key3', '88888')
    },

    handleClick2() {
      // 动态删除对象属性不能自动更新到视图中
      delete this.obj1.key2

      // 解决方案1、使用this.$forceUpdate()
      // this.$forceUpdate()

      // 解决方案2、使用this.$delete()
      // this.$delete(this.obj1, 'key2')
    },

    handleClick3() {
      // 使用索引修改数组值不能自动更新到视图中，push 和 pop 方法正常
      // this.arr1[1] = 888
      // this.arr1.push("999")
      // this.arr1.pop()

      // 解决方案1、使用this.$forceUpdate()
      this.$forceUpdate()

      // 解决方案2、使用this.$set()
      // this.$set(this.arr1, 1, '888')
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

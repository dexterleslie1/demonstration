<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <!-- https://www.jb51.net/article/180823.htm -->
    <hr>
    <div>
      遍历普通数组
    </div>
    <div>
      <span style="margin: 5px;border-style: solid;border-width: 1px;" v-for="(item, index) in datum1">{{ index }} - {{
        item }}</span>
    </div>

    <hr>
    <div>
      遍历对象数组
    </div>
    <div>
      <div style="margin: 5px;border-style: solid;border-width: 1px;" v-for="(item, index) in datum2">{{ index }} - {{
        item.text }}
        <div style="margin: 5px;border-style: solid;border-width: 1px;" v-for="(itemI1, indexI1) in item.children">
          {{ itemI1.text }}
        </div>
      </div>
    </div>

    <hr>
    <div>
      遍历对象
    </div>
    <div>
      <div style="margin: 5px;border-style: solid;border-width: 1px;" v-for="(val, key, index) in datum3">
        {{ index }} - {{ key }} - {{ val }}
      </div>
    </div>

    <hr>
    <div>
      动态 ref 对象
    </div>
    <div>
      <div v-for="(item, index) in this.dataList">
        <button @click="handleClick(item)">点击我input聚焦</button>
        <input :ref="`input${item}`" />
      </div>
    </div>

    <hr>
    <div>
      v-for 一个范围
    </div>
    <div>
      <div v-for="index in this.count">
        {{ index }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      // 协助测试 v-for 一个范围
      count: 3,
      dataList: ['a', 'b', 'c'],
      datum1: [1, 2, 3, 4, 5],
      datum2: [{
        text: 'item1',
        children: [
          {
            text: 'item1-1'
          }, {
            text: 'item1-2'
          }
        ]
      }, {
        text: 'item2',
        children: [
          {
            text: 'item2-1'
          }, {
            text: 'item2-2'
          }
        ]
      }],
      datum3: {
        id: 1,
        name: 'Dexterleslie',
        age: 22
      }
    }
  },
  methods: {
    handleClick(item) {
      this.$refs[`input${item}`][0].focus()
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

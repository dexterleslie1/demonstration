<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <div>
      删除指定索引的元素
    </div>
    <div>
      arrayInt: {{ this.arrayInt }}
      <input type="button" value="点击我" @click="handleClickRemoveSpecifyIndex" />
    </div>
    <hr />

    <div>
      indexOf 函数获取指定 primitive 元素索引，注意：indexOf不能用于对象类型，需要使用findIndex方法
    </div>
    <div>
      indexOfElement: {{ this.indexOfElement }}
      <input type="button" value="点击我" @click="handleClickIndexOf" />
    </div>
    <hr />

    <div>
      findIndex 函数获取指定对象元素的索引
    </div>
    <div>
      findIndexOfObject: {{ this.findIndexOfObject }}
      <input type="button" value="点击我" @click="handleClickFindIndexOfObject" />
    </div>
    <hr />

    <div>
      在指定 index 位置插入元素
    </div>
    <div>
      arrayInt: {{ this.arrayInt }}
      <input type="button" value="点击我" @click="handleClickInsertElementBySpecifyingIndex" />
    </div>
    <hr />
  </div>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'
import _ from 'lodash'

export default {
  name: 'App',
  components: {
    // HelloWorld
  },
  data() {
    return {
      arrayInt: [1, 2, 3],
      indexOfElement: NaN,
      findIndexOfObject: NaN,
      arrayObject: [{
        userId: 1,
        nickname: 'n1'
      }, {
        userId: 2,
        nickname: 'n2'
      }, {
        userId: 3,
        nickname: 'n3'
      }],
    }
  },
  methods: {
    handleClickRemoveSpecifyIndex() {
      // 删除索引为 1 的元素
      _.remove(this.arrayInt, function (value, index, array) {
        return index == 1
      })
      // 重新给 arrayInt 赋值，否则视图不会更新
      this.arrayInt = [...this.arrayInt]
    },
    handleClickIndexOf() {
      this.indexOfElement = _.indexOf(this.arrayInt, 3)
    },
    handleClickFindIndexOfObject() {
      this.findIndexOfObject = _.findIndex(this.arrayObject, function (o) {
        return o.userId == 2
      })
    },
    handleClickInsertElementBySpecifyingIndex() {
      // 第一个参数 1 指定了开始修改数组的位置索引。索引是从 0 开始的，所以 1 指的是数组的第二个位置。
      // 第二个参数 0 指定了要删除的元素数量。在这个例子中，0 意味着不删除任何元素。
      // 从第三个参数开始，后面的参数（在这个例子中是 2）指定了要添加到数组中的新元素。这些新元素会被添加到由第一个参数指定的位置之后。
      this.arrayInt.splice(1, 0, 5)
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
  margin-top: 60px;
}
</style>

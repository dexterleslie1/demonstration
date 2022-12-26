<template>
  <div>
    <el-divider content-position="left">演示插值法</el-divider>
    &nbsp;counter={{counter}}
    <!-- 插值法简单的运算 -->
    ,counter*2={{counter*2}}
    &nbsp;<el-button v-on:click="counter++">计数器加1</el-button>

    <el-divider content-position="left">演示v-pre用法</el-divider>
    <!-- 表示v-pre内的模板不被vue解析原样输出 -->
    <div v-pre>{{counter}}</div>

    <el-divider content-position="left">演示v-once用法</el-divider>
    <!-- v-once表示只用初值意思，之后变量的变动不会反应在ui上 -->
    counter初值:<span v-once>{{counter}}</span>

    <el-divider content-position="left">演示v-text用法</el-divider>
    <!-- 相当于innerHTML -->
    counter=&nbsp;<span v-text="counter"></span>

    <el-divider content-position="left">演示v-html用法</el-divider>
    <span v-html="html"></span>
    &nbsp;&nbsp;<span>{{html}}</span>

    <el-divider content-position="left">演示v-bind和绑定语法糖属性绑定用法</el-divider>
    <!-- v-bind属性内容为vue模板 -->
    couter=<input v-bind:value="counter"/>
    &nbsp;(语法糖)couter=<input :value="counter"/>

    <el-divider content-position="left">演示v-bind style和css属性绑定用法</el-divider>
    <!-- 
      https://www.jianshu.com/p/14e406de3942 
      https://learnvue.co/tutorials/reactive-styles-vue-3
    -->
    <!-- <div class="my-box" :style="{'background-color': backgroundColor}">box1</div>  -->
    <!-- 等价于上面写法 -->
    <div class="my-box" :style="{backgroundColor: backgroundColor}">box1</div>
    <div class="my-box my-background-color">box2</div>
    &nbsp;<el-button v-on:click="backgroundColor='yellow'">修改背景为黄色</el-button>

    <el-divider content-position="left">演示v-bind 动态class用法</el-divider>
    <div class="my-box" :class="{'my-background-color-red':toggleBackgroundColor,'my-background-color-yellow':!toggleBackgroundColor}">box1</div>
    &nbsp;<el-button v-on:click="toggleBackgroundColor=!toggleBackgroundColor">切换背景色</el-button>
  </div>
</template>

<script>
export default {
  name: "App",
  data() {
    return {
      counter: 1,
      html: "<a href='https://www.baidu.com'>百度</a>",
      backgroundColor: "red",
      toggleBackgroundColor: true
    }
  }
}
</script>

<style>
.my-box {
  width: 50px;
  height: 50px;
  border-style: solid;
  border-color: black;
}

.my-background-color {
  background-color: v-bind("backgroundColor");
}

.my-background-color-red {
  background-color: red;
}
.my-background-color-yellow {
  background-color: yellow;
}
</style>

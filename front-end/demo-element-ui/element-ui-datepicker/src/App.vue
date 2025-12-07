<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <div>选择日期 - 默认</div>
    <div>
      <el-date-picker type="date" placeholder="选择日期" v-model="value1">
      </el-date-picker>
    </div>
    <hr />

    <div>选择日期 - 带快捷选项</div>
    <div>
      <el-date-picker type="date" v-model="value2" placeholder="选择日期" :picker-options="pickerOptions">
      </el-date-picker>
    </div>
    <hr />

    <div>选择日期范围 - 默认</div>
    <div>
      <el-date-picker v-model="value3" type="daterange" range-separator="至" start-placeholder="开始日期"
        end-placeholder="结束日期"></el-date-picker>
    </div>
    <hr />

    <div>选择日期范围 - 带快捷选项</div>
    <div>
      <el-date-picker v-model="value4" type="daterange" unlink-panels range-separator="至" start-placeholder="开始日期"
        end-placeholder="结束日期" :picker-options="pickerOptions2">

      </el-date-picker>
    </div>
    <hr />
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
      value1: '',
      value2: '',
      value3: '',
      value4: '',
      pickerOptions: {
        disableDate(time) {
          return time.getTime() > Date.now()
        },
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date())
          }
        }, {
          text: '昨天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24);
            picker.$emit('pick', date);
          }
        }, {
          text: '一周前',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', date);
          }
        }]
      },
      pickerOptions2: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
    }
  }
}
</script>

<style>
#app {}
</style>

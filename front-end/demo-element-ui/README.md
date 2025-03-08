# Element-UI



## Vue2 集成 Element-UI

>请参考本站 <a href="/vue/集成element-ui.html" target="_blank">链接</a>



## 消息提示 Message

>[Element-UI 官方参考消息提示 Message](https://element.eleme.cn/#/zh-CN/component/message)

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-element-ui/demo-element-ui-message)

点击按钮弹出成功、警告、消息、错误消息提示

```vue
<el-button :plain="true" @click="open2">成功</el-button>
<el-button :plain="true" @click="open3">警告</el-button>
<el-button :plain="true" @click="open1">消息</el-button>
<el-button :plain="true" @click="open4">错误</el-button>
```

弹出成功、警告、消息、错误消息提示代码如下：

```vue
<script>
export default {
  methods: {
    open1() {
      this.$message('这是一条消息提示');
    },
    open2() {
      this.$message({
        message: '恭喜你，这是一条成功消息',
        type: 'success'
      });
    },

    open3() {
      this.$message({
        message: '警告哦，这是一条警告消息',
        type: 'warning'
      });
    },

    open4() {
      this.$message.error('错了哦，这是一条错误消息');
    }
  }
}
</script>
```


<template>
    <div>
        <el-button type="text" @click="dialogVisible = true">点击打开 Dialog</el-button>

        <!-- NOTE: 下面传递props.visible一定要使用:visible.sync写法，否则子组件不能使用this.$emit('update:visible', false)方法修改父组件data中visible值 -->
        <my-el-dialog-biz
            :visible.sync="dialogVisible" 
            @cancelCallback="handleCancelCallback"
            @confirmCallback="handleConfirmCallback"></my-el-dialog-biz>    
    </div>
</template>

<script>
import MyElDialogBiz from '@/components/my-el-dialog-biz'

export default {
    components: {
        MyElDialogBiz
    },
    data() {
        return {
            dialogVisible: false,
        }
    },
    methods: {
      handleCancelCallback(count) {
          this.$message(`点击取消count=${count}`)
      },
      handleConfirmCallback(count) {
          this.$message(`点击确定count=${count}`)
      }
    }
}
</script>
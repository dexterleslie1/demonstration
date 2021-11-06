<template>
    <div>
        <el-button @click="visible = true">打开联系人选择器</el-button>
        <my-contact-chooser 
            :visible.sync="visible"
            @cancelCallback="handleCancelCallback"
            @confirmCallback="handleConfirmCallback"></my-contact-chooser>
    </div>
</template>

<script>
import MyContactChooser from '@/components/my-contact-chooser'

export default {
    components: {
        MyContactChooser
    },
    data() {
        return {
            visible: false
        }
    },
    created() {
        
    },
    methods: {
        handleCancelCallback(contactIdListSelected) {
            this.$message(`点击取消contactIdListSelected=${JSON.stringify(contactIdListSelected)}`)
        },
        handleConfirmCallback(contactIdListSelected) {
            this.$message(`点击确定contactIdListSelected=${JSON.stringify(contactIdListSelected)}`)
        }
    }
}
</script>
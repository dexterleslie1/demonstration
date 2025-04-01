<template>
    <el-dialog
        title="提示"
        :visible.sync="visible"
        width="30%"
        :before-close="handleBeforeClose">
        <div>
            <el-button @click="count--">-</el-button>
            {{count}}
            <el-button @click="count++">+</el-button>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button @click="handleCancel">取 消</el-button>
            <el-button type="primary" @click="handleConfirm">确 定</el-button>
        </span>
    </el-dialog>
</template>

<script>
export default {
    props: {
        // 是否显示el-dialog
        visible: {
            type: Boolean,
            required: true
        }
    },
    data() {
        return {
            count: 0
        }
    },
    methods: {
        handleCancel() {
            this.$emit('update:visible', false)
            this.$emit('cancelCallback', this.count)
        },
        handleConfirm() {
            this.$emit('update:visible', false)
            this.$emit('confirmCallback', this.count)
        },
        handleBeforeClose() {
            this.$emit('update:visible', false)
            this.$emit('cancelCallback', this.count)
        }
    }
}
</script>
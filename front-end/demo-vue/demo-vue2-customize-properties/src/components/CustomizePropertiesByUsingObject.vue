<template>
    <div>
        <p>Message: {{ message }}</p>
        <p>Count: {{ count }}</p>
        <p>User: {{ user.name }}</p>
        <p>Timeout: {{ timeout }}</p>
        <p>Status: {{ status }}</p>
    </div>
</template>

<script>
export default {
    props: {
        // 基础类型校验（null/undefined 允许任何类型）
        message: String,

        // 多个可能的类型
        count: [Number, String], // count 可以是 Number 或 String

        // 必填项 + 类型校验
        user: {
            type: Object,
            required: true // 必须传入，否则控制台警告
        },

        // 默认值（仅对非必填属性有效）
        timeout: {
            type: Number,
            default: 1000 // 默认值为 1000
        },

        // 自定义校验函数
        status: {
            type: String,
            validator: function (value) {
                // 校验值必须是 'active' 或 'inactive'
                return ['active', 'inactive'].includes(value)
            }
        }
    },
}
</script>

<style scoped></style>
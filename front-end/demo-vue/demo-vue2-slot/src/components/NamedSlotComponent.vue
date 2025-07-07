<template>
    <div class="card">
        <!-- 头部插槽 -->
        <slot name="header"></slot>
        <!-- 主体插槽（默认插槽，可省略 name） -->
        <slot></slot>
        <!-- 底部插槽 -->
        <slot name="footer"></slot>
    </div>
</template>

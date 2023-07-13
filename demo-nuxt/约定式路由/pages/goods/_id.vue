<template>
    <div>
        <div>商品详情</div>
        <div>
            <!-- https://www.coder.work/article/1035775 -->
            this.$route.fullPath: {{ this.$route.fullPath }}<br/>
            this.$route.path: {{ this.$route.path }}<br/>
            this.$route.params: {{ this.$route.params }}<br/>
            this.$route.query: {{ this.$route.query }}<br/>
        </div>
    </div>
</template>
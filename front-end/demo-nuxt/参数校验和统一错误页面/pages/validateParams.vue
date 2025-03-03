<template>
    <div>
        参数校验界面
    </div>
</template>

<script>
export default {
    validate({ params, query }) {
        return query.p1 && query.p1 != ""
    }
}
</script>
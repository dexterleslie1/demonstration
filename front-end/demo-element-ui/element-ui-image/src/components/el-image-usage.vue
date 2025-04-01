<template>
    <el-image 
        :src="imageUrl" 
        style="
            height: 40px;
            width: 40px;"></el-image>
</template>

<script>
export default {
    data() {
        return {
            imageUrl: 'http://42.240.131.221:9085/obs-default-epu-9223372036854775807-1/2021-09-14/id-6086-qaz1wsx12-Avatar-small.jpg'
        }
    }
}
</script>
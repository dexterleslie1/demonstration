<template>
    <div>
        <div>演示vuex actions用法</div>
        {{$store.state.moduleA.loginStatus}}
        <button v-on:click="login">开始模拟</button>
        <hr/>
    </div>
</template>

<script>
export default {
    methods: {
        login() {
            this.$store.dispatch('moduleA/login', {param1:'param1', param2:'param2'})
            .then(function(data) {
                alert("登录成功")
            }).catch(function(error) {
                alert(error)
            })
        }
    }
}
</script>
<template>
    <div>
        <div>演示vuex actions用法</div>
        {{ $store.state.moduleA.loginStatus }}
        <button v-on:click="handleClickLogin">开始模拟</button>
        <hr />
    </div>
</template>

<script>
import { mapActions } from 'vuex';

export default {
    methods: {
        ...mapActions("moduleA", ["login"]),
        handleClickLogin() {
            // this.$store.dispatch('moduleA/login', { param1: 'param1', param2: 'param2' })
            //     .then(function (data) {
            //         alert("登录成功")
            //     }).catch(function (error) {
            //         alert(error)
            //     })

            this.login({ param1: 'param1', param2: 'param2' })
                .then(function (data) {
                    alert("登录成功")
                }).catch(function (error) {
                    alert(error)
                })
        }
    }
}
</script>
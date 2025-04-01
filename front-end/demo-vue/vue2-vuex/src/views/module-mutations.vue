<template>
    <div>
    <div>演示不带参数vuex mutations使用</div>
    <button v-on:click="$store.commit('moduleA/add')">+</button>
    {{$store.state.moduleA.count}}
    <button v-on:click="$store.commit('moduleA/sub')">-</button>
    <hr/>

    <div>演示带一个参数vuex mutations使用</div>
    <button v-on:click="$store.commit('moduleA/addWithSingleParameter', 2)">+</button>
    {{$store.state.moduleA.count}}
    <button v-on:click="$store.commit('moduleA/subWithSingleParameter', 2)">-</button>
    <hr/>

    <div>演示带多个参数vuex mutations使用</div>
    <button v-on:click="$store.commit('moduleA/addWithMultipleParameter', {step1:1, step2:2})">+</button>
    {{$store.state.moduleA.count}}
    <button v-on:click="$store.commit('moduleA/subWithMultipleParameter', {step1:1, step2:2})">-</button>
    <hr/>
  </div>
</template>

<script>
export default {
    
}
</script>
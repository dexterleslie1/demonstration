<template>
  <div>
    <div>演示带参数getters用法</div>
    <button v-on:click="$store.commit('moduleA/add')">+</button>
    <button v-on:click="$store.commit('moduleA/sub')">-</button>
    <div>Count：{{$store.state.moduleA.count}} 3次乘积结果：{{$store.getters['moduleA/countPower'](3)}}</div>
    <hr/>
  </div>
</template>

<script>
export default {
    
}
</script>
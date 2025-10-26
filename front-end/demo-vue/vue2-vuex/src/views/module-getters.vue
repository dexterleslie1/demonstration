<template>
  <div>
    <div>演示带参数getters用法</div>
    <button v-on:click="$store.commit('moduleA/add')">+</button>
    <button v-on:click="$store.commit('moduleA/sub')">-</button>
    <!-- <div>Count：{{ $store.state.moduleA.count }} 3次乘积结果：{{ $store.getters['moduleA/countPower'](3) }}</div> -->
    <div>Count：{{ $store.state.moduleA.count }} 3次乘积结果：{{ countPower(3) }}</div>
    <hr />
  </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  computed: {
    // 下面写法错误：mapGetters用于映射具体的 getter 函数，而不是整个模块
    // ...mapGetters(["moduleA"])
    // 将模块中的 moduleA countPower getter 映射到组件
    ...mapGetters('moduleA', ['countPower'])
  }
}
</script>
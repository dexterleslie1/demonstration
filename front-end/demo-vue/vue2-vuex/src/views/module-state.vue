<template>
    <div>
        <!-- 不在模块内直接引用 -->
        <div>{{ helloStr }}</div>
        <div>$store.state写法</div>
        <div>
            <button @click="$store.state.moduleA.count++">+</button>
            {{ $store.state.moduleA.count }}
            <button @click="$store.state.moduleA.count--">-</button>
        </div>

        <div>...mapState写法</div>
        <div>
            <!-- 通过模块名称引用，如下所示 -->
            <button @click="moduleA.count++">+</button>
            {{ moduleA.count }}
            <button @click="moduleA.count--">-</button>
        </div>
    </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
    computed: {
        ...mapState(['moduleA', "helloStr"])
    }
}
</script>
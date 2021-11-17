<template>
    <div>
        <button @click="$store.state.moduleA.count++">+</button>
        {{$store.state.moduleA.count}}
        <button @click="$store.state.moduleA.count--">-</button>
    </div>
</template>

<script>
export default {
    
}
</script>
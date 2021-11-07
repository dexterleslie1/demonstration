<template>
    <div>
        key1: <input type="text" v-model="key1"/><br/>
        key2: <input type="text" v-model="key2"/><br/>
        obj1.prop1: <input type="text" v-model="obj1.prop1"/><br/>
        obj2.prop1: <input type="text" v-model="obj2.prop1"/><br/>
    </div>
</template>

<script>
export default {
    data() {
        return {
            key1: '',
            key2: '5',
            obj1: {
                prop1: ''
            },
            obj2: {
                prop1: '3'
            }
        }
    },
    watch: {
        // 监听普通属性
        key1(newValue, oldValue) {
            console.log(`key1 new value=${newValue}, old value=${oldValue}`)
        },
        // 监听普通属性并且在第一次初始化时候就调用
        key2: {
            handler(newValue, oldValue) {
                console.log(`key2 new value=${newValue}, old value=${oldValue}`)
            },
            // 代表在wacth里声明了firstName这个方法之后立即先去执行handler方法
            immediate: true
        },
        // 监听对象所有属性
        obj1: {
            // NOTE: newValue和oldValue指向同一个内存对象，所以newValue和oldValue值相同
            handler(newValue, oldValue) {
                console.log(`obj1 new value=${JSON.stringify(newValue)}, old value=${JSON.stringify(oldValue)}`)
            },
            immediate: true,
            deep: true
        },
        // 监听对象指定属性
        'obj2.prop1': {
            handler(newValue, oldValue) {
                console.log(`obj2.prop1 new value=${newValue}, old value=${oldValue}`)
            }
        }
    }
}
</script>
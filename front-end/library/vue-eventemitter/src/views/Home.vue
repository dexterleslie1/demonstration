<template>
  <div class="home">
    <input type="button" value="使用once绑定test-event1事件监听器" @click="handleAddListenerByOnce()"/>
    <input type="button" value="使用on绑定test-event1事件监听器" @click="handleAddListener()"/>
    <input type="button" value="解绑test-event1事件监听器" @click="handleRemoveListener()"/>

    <input type="button" value="发送test-event1事件" @click="handleSendEvent()"/>
  </div>
</template>

<script>

export default {
  name: 'Home',
  components: {
  },
  methods: {
    handleSendEvent() {
      this.$eventbus.emit('test-event1', {param1: 'paramv1', param2: 'paramv2'})
    },
    handleRemoveListener() {
      this.$eventbus.off('test-event1')
    },
    handleAddListener() {
      this.$eventbus.on('test-event1', function(params) {
        console.log(`接收到事件test-event1，参数：${JSON.stringify(params)}`)
      })
    },
    handleAddListenerByOnce() {
      this.$eventbus.once('test-event1', function(params) {
        console.log(`接收到事件test-event1，参数：${JSON.stringify(params)}`)
      })
    }
  },
}
</script>

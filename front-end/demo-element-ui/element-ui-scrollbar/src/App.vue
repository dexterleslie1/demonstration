<template>
  <div id="app">
    <!-- 垂直滚动 -->
    <el-scrollbar style="border: 1px solid #e0e0e0;">
      <div v-for="msg in messages" :key="msg.id" class="message">
        {{ msg.content }}
      </div>
    </el-scrollbar>

    <!-- 水平滚动 -->
    <el-scrollbar style="border: 1px solid #e0e0e0;">
      <!-- 强制div不换行，否则el-scrollbar不会水平滚动 -->
      <div style="white-space: nowrap;">
        {{ longMessage }}
      </div>
    </el-scrollbar>
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    HelloWorld,
  },
  data() {
    return {
      messages: [],
      longMessage: "",
    }
  },
  mounted() {
    for (var i = 0; i < 100; i++) {
      this.messages.push({ id: i, content: `内容${i}` })
    }

    for (var i = 0; i < 10000; i++) {
      this.longMessage = this.longMessage + i + "-";
    }
  },
  methods: {
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

#app > div {
  width: 100%;
}

#app > div:nth-child(1) {
  flex: 1;
}
#app > div:nth-child(2) {
  flex: 1;
}
</style>

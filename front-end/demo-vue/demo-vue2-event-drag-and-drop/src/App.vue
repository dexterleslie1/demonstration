<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <div style="display:flex;flex-direction:column;justify-content:center;align-items:center;">
      <div class="drop-zone" @dragenter.prevent="isDragging = true" @dragover.prevent
        @dragleave.prevent="isDragging = false" @drop.prevent="handleDrop" :class="{ 'dragging': isDragging }"
        @click="$refs.fileInput.click()">
        拖拽文件到这里或者点击选择文件
        <input type="file" name="files" ref="fileInput" @change="handleChangeFile" multiple style="display:none;" />
      </div>

      <div style="margin-top:20px;">
        <div>
          已经选择文件列表：
        </div>
        <div v-if="fileList.length == 0" style="color:red;font-size:16px;padding-top:10px;">无数据</div>
        <div style="display:flex;flex-direction:column;align-items:start;padding-left:10px;padding-top:10px;">
          <div v-for="item in this.fileList" style="padding:2px 0px;">
            <span>•</span><span style="margin-left:10px;font-weight:bold;">{{ item.name }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    // HelloWorld
  },
  data() {
    return {
      fileList: [],
      isDragging: false,
    }
  },
  mounted() {
    // 禁止文件拖拽自动下载或者打开的默认行为，需要 dragover 和 drop 同时 prevent 才生效
    document.addEventListener('dragover', this.handleDragOverPrevent);
    document.addEventListener('drop', this.handleDropPrevent);
  },
  beforeDestroy() {
    document.removeEventListener('dragover', this.handleDragOverPrevent);
    document.removeEventListener('drop', this.handleDropPrevent);
  },
  methods: {
    handleDrop(event) {
      this.isDragging = false
      let files = event.dataTransfer.files
      if (files && files.length > 0) {
        for (let i = 0; i < files.length; i++) {
          this.fileList.push(files[i])
        }
      }
    },
    handleChangeFile(event) {
      this.isDragging = false
      let files = event.target.files
      if (files && files.length > 0) {
        for (let i = 0; i < files.length; i++) {
          this.fileList.push(files[i])
        }
      }
      this.$refs.fileInput.value = null
    },
    handleDragOverPrevent(event) {
      event.preventDefault();
    },
    handleDropPrevent(event) {
      event.preventDefault();
    }
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
  margin-top: 60px;
}

.drop-zone {
  border-width: 2px;
  border-style: dashed;
  border-color: #ccc;
  border-radius: 10px;
  padding: 50px 0px;
  width: 80%;
}

.drop-zone.dragging {
  border-color: black;
  background-color: rgb(228, 228, 228);
}
</style>

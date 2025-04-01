<template>
  <div id="app">
    <div>演示 input file 控件的自定义开发</div>
    <div>
      <!-- multiple 表示支持文件多选 -->
      <input ref="fileInput" type="file" name="files" multiple @change="handleChangeFileInput" style="display:none;">
      <input type="button" value="选择文件 ..." @click="() => { this.$refs.fileInput.click() }" />
    </div>
    <div>
      <div>选择文件列表如下：</div>
      <div v-if="!this.fileList || this.fileList.length==0">没有文件</div>
      <div v-for="item in this.fileList" :key="item.id">
        <input type="button" value="-" @click="handleClickRemoveFile(item)" style="width:25px;height:25px;" />
        &nbsp;&nbsp;{{ item.file.name }}
      </div>
    </div>
  </div>
</template>

<script>
import { v4 as uuidv4 } from 'uuid'
import _ from 'lodash'

export default {
  name: 'App',
  components: {
  },
  data() {
    return {
      // 自定义文件列表用于存放准备上传的文件对象
      fileList: []
    }
  },
  methods: {
    handleChangeFileInput(event) {
      let files = event.target.files
      if (files && files.length > 0) {
        for (let i = 0; i < files.length; i++) {
          // 使用 id 是为了在 v-for 中拥有唯一的 key，在删除数据时候就不会错乱
          let fileEntry = { id: uuidv4(), file: files[i] }
          this.fileList.push(fileEntry)
        }
      }

      // 清空 input file 的选择记录
      this.$refs.fileInput.value = null
    },
    handleClickRemoveFile(item) {
      _.remove(this.fileList, function (value, index, array) {
        return value.id == item.id
      })
      // 重新给赋值，否则视图不会更新
      this.fileList = [...this.fileList]
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
</style>

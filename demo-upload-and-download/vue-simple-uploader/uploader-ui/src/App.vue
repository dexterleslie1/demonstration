<template>
  <div>
    <uploader :key="uploader_key" :options="options" class="uploader-example"
              :autoStart="false"
              @file-success="onFileSuccess"
              @file-added="filesAdded">
      <uploader-unsupport></uploader-unsupport>
      <uploader-drop>
        <uploader-btn :single="true" :attrs="attrs">选择文件</uploader-btn>
      </uploader-drop>
      <uploader-list></uploader-list>
    </uploader>
  </div>
</template>

<script>
import SparkMD5 from 'spark-md5'


export default {
  data() {
    return {
      uploader_key: new Date().getTime(),
      options: {
        target: '/chunk/chunkUpload'
      },
      attrs: {
        accept: '*'
      }
    }
  },
  methods: {
    onFileSuccess: function (rootFile, file, response, chunk) {
      console.log(JSON.parse(response).model);
    },
    computeMD5(file) {
      const loading = this.$loading({
        lock: true,
        text: '正在计算MD5',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      let fileReader = new FileReader();
      let time = new Date().getTime();
      let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;
      let currentChunk = 0;
      const chunkSize = 10 * 1024 * 1000;
      let chunks = Math.ceil(file.size / chunkSize);
      let spark = new SparkMD5.ArrayBuffer();
      file.pause();

      loadNext();

      fileReader.onload = (e => {
        spark.append(e.target.result);
        if (currentChunk < chunks) {
          currentChunk++;
          loadNext();
          this.$nextTick(() => {
            console.log('校验MD5 ' + ((currentChunk / chunks) * 100).toFixed(0) + '%')
          })
        } else {
          let md5 = spark.end();
          loading.close();
          this.computeMD5Success(md5, file);
          console.log(`MD5计算完毕：${file.name} \nMD5：${md5} \n分片：${chunks} 大小:${file.size} 用时：${new Date().getTime() - time} ms`);
        }
      });
      fileReader.onerror = function () {
        this.error(`文件${file.name}读取出错，请检查该文件`);
        loading.close();
        file.cancel();
      };

      function loadNext() {
        let start = currentChunk * chunkSize;
        let end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
        fileReader.readAsArrayBuffer(blobSlice.call(file.file, start, end));
      }
    },
    computeMD5Success(md5, file) {
      file.uniqueIdentifier = md5;//把md5值作为文件的识别码
      // file.resume();//开始上传
    },
    filesAdded(file, event) {
      // //大小判断
      // const isLt100M = file.size / 1024 / 1024 < 10;
      // if (!isLt100M) {
      //   this.$message.error("上传文件大于100M");
      // } else {
        this.computeMD5(file)
      // }
    }
  }
}
</script>

<style>
.uploader-example {
  width: 90%;
  padding: 15px;
  margin: 40px auto 0;
  font-size: 12px;
  box-shadow: 0 0 10px rgba(0, 0, 0, .4);
}

.uploader-example .uploader-btn {
  margin-right: 4px;
}

.uploader-example .uploader-list {
  max-height: 440px;
  overflow: auto;
  overflow-x: hidden;
  overflow-y: auto;
}
</style>

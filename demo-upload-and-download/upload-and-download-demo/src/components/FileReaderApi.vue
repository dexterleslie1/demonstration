<template>
  <div>
    <div>演示使用FileReader分片读取文件</div>
    <el-upload
        ref="uploadComponent"
        action=""
        :auto-upload="false"
        :limit="10"
        :file-list="fileList"
        :multiple="true"
        :http-request="handleUploadFile"
        :on-change="handleOnChange"
        :show-file-list="false">
      <el-button size="small" type="primary">选择文件 (注意: 最多选择10个文件)</el-button>
    </el-upload>

    <el-table
        :data="fileList"
        style="width: 100%">
      <el-table-column
          prop="name"
          label="名称"
          width="300">
      </el-table-column>
      <el-table-column
          label="状态"
          width="250">
        <template #default="scope">
          <span v-if="scope.row.analysingSlice">分析分片中...</span>
          <el-progress :percentage="scope.row.percent"></el-progress>
        </template>
      </el-table-column>
      <el-table-column
          prop="size"
          label="大小"
          width="180">
      </el-table-column>
      <el-table-column
          label="操作"
          width="100">
        <template #default="scope">
          <el-button
              size="mini"
              @click="handleDelete(scope.$index, scope.row)" type="danger" >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <hr/>

    <div>演示使用FileReader接口</div>
    <el-upload
        :auto-upload="false"
        :limit="1"
        :multiple="true"
        :on-change="handleOnChangeFileReaderApiDemo"
        :show-file-list="true">
      <el-button size="small" type="primary">选择文件后查看控制台打印相关日志</el-button>
    </el-upload>
  </div>
</template>

<script>
// File Api MDN Web Docs
// https://developer.mozilla.org/zh-CN/docs/Web/API/File

// FileReader Api MDN Web Docs
// https://developer.mozilla.org/zh-CN/docs/Web/API/FileReader

// html5 FileReader对象使用
// https://www.cnblogs.com/huancheng/p/9376730.html

// html5 FileReader读取文件进度progress
// https://blog.csdn.net/sunshine102548/article/details/84145262

// 使用FileReader进行文件分片读取
// https://blog.csdn.net/qq_15506981/article/details/109859317

// Element-UI 自定义upload组件(进度条,删除,下载)
// https://juejin.cn/post/6844903890417090568

// vue spark-md5使用
// https://blog.csdn.net/a772116804/article/details/111578665
import {ElMessage} from "element-plus"
import SparkMD5 from "spark-md5"

export default {
  name: "FileReaderApi",
  data() {
    return {
      fileList:[]
    }
  },
  methods: {
    handleOnChange(file, fileList) {
      if(file.status === 'ready') {
        this.fileList = fileList
        let fileListObjectRef
        this.fileList.forEach(item=>{
          if(item.name === file.name) {
            fileListObjectRef = item
          }
        })

        // 判断是否支持FileReader对象
        let reader;
        if(FileReader) {
          reader = new FileReader();
        }
        if(!reader) {
          ElMessage({
            type: 'error',
            message: '当前浏览器不支持FileReader api'
          })
          this.$refs.uploadComponent.clearFiles()
          return
        }

        // 添加上传文件
        let fileObject = file.raw
        let fileSize = fileObject.size
        let sizeTemporary = fileSize
        let unit = '字节'
        if(sizeTemporary>=1024) {
          sizeTemporary = (sizeTemporary/1024).toFixed(0)
          unit = 'KB'
        }
        // 每个分片大小1MB
        let eachSliceSize = 1024*1024
        // 当前选中文件总分片数
        let totalSlices = Math.ceil(fileSize/eachSliceSize)
        console.log(`选中文件 ${file.name} 总大小:${sizeTemporary}${unit}，每个分片大小:${eachSliceSize/1024/1024}MB，总分片数:${totalSlices}片`)

        // 当前读取分片索引
        let currentSliceIndex = 0
        // 当前已读取字节数
        let totalLoadedSize = 0

        let spark = new SparkMD5()

        // FileReader对象读取大文件显示读取进度
        reader.onprogress = function(event){
          let progress = ((totalLoadedSize/fileSize)*100).toFixed(0)
          progress = parseInt(progress)
          fileListObjectRef.analysingSlice = true
          fileListObjectRef.percent = progress
        }

        // FileReader对象读取文件完毕回调
        reader.onload = function(event){
          currentSliceIndex++
          totalLoadedSize += event.loaded
          let md5Result = SparkMD5.hashBinary(event.target.result);
          spark.appendBinary(event.target.result)
          let message = "已读取第 " + currentSliceIndex + " 个分片，读取字节数: " + event.loaded + "，分片md5:" + md5Result;
          console.log(message)
          if(currentSliceIndex<=totalSlices-1) {
            readSlice(currentSliceIndex)
          } else {
            let md5 = spark.end()
            message = "读取完毕，总共读取: " + totalLoadedSize + " 字节，文件md5: " + md5;
            console.log(message)
            let progress = ((totalLoadedSize/fileSize)*100).toFixed(0)
            progress = parseInt(progress)
            fileListObjectRef.percent = progress
          }
        }

        readSlice(currentSliceIndex)

        function readSlice(sliceIndex) {
          let start = sliceIndex*eachSliceSize
          let end = start + eachSliceSize
          if(end>fileSize) {
            end = fileSize
          }
          let fileSlice = fileObject.slice(start, end)
          reader.readAsBinaryString(fileSlice)
        }
      }
    },
    handleDelete(index, row) {
      this.fileList.splice(index, 1)
    },

    handleOnChangeFileReaderApiDemo(file, fileList) {
      let fileObject = file.raw
      let filename = fileObject.name

      this.computeFileMd5(fileObject, function(progress) {
        console.log(`读取文件 ${filename} 读取进度 ${progress}%`)
      }, true)
      .then(function(response) {
        let md5 = response.md5
        console.log(`读取文件 ${filename} 完毕，文件md5：${md5}`)
      })
      .catch(function(error) {
        console.error(error)
      })
    },
    // 计算文件md5值
    computeFileMd5(file, onProgress, slice=false /* 是否分片读取文件 */) {
      let promise = new Promise(function(resolve, reject) {
        if(!file || !(file instanceof File)) {
          reject({
            errorCode: 600,
            errorMessage: '没有提供file参数或者file参数非File类型对象'
          })
          return
        }

        let reader = new FileReader()
        let spark = new SparkMD5()
        // NOTE: 下面不使用分片方法计算超大文件md5值错误，无法得到正确的md5值
        // if(!slice) {
        //   reader.onprogress = function(event) {
        //     if(event.type && event.type=='progress') {
        //       let total = event.total
        //       let loaded = event.loaded
        //       let progress = parseInt(((loaded/total) * 100).toFixed(0))
        //       if(onProgress) {
        //         onProgress(progress)
        //       }
        //     }
        //   }
        //   reader.onload = function(event) {
        //     spark.appendBinary(event.target.result)
        //     let md5 = spark.end()
        //     // 文件读取完毕返回md5值
        //     resolve({
        //       md5: md5
        //     })
        //   }

        //   // 开始读取文件
        //   reader.readAsBinaryString(file)

        // } else {
          let fileSize = file.size
          // 每个分片大小1MB
          let eachSliceSize = 1024*1024
          // 当前选中文件总分片数
          let totalSlices = Math.ceil(fileSize/eachSliceSize)
          
          // 当前读取分片索引
          let currentSliceIndex = 0
          // 当前已读取字节数
          let totalLoadedSize = 0

          // FileReader对象读取大文件显示读取进度
          reader.onprogress = function(event){
            let progress = ((totalLoadedSize/fileSize)*100).toFixed(0)
            progress = parseInt(progress)
            if(onProgress) {
              onProgress(progress)
            }
          }

          // FileReader对象读取文件完毕回调
          reader.onload = function(event){
            currentSliceIndex++
            totalLoadedSize += event.loaded
            spark.appendBinary(event.target.result)
            if(currentSliceIndex<=totalSlices-1) {
              readSlice(currentSliceIndex)
            } else {
              let md5 = spark.end()
              let progress = ((totalLoadedSize/fileSize)*100).toFixed(0)
              progress = parseInt(progress)
              if(onProgress) {
                onProgress(progress)
              }
              resolve({
                md5: md5
              })
            }
          }

          readSlice(currentSliceIndex)

          function readSlice(sliceIndex) {
            let start = sliceIndex*eachSliceSize
            let end = start + eachSliceSize
            if(end>fileSize) {
              end = fileSize
            }
            let fileSlice = file.slice(start, end)
            reader.readAsBinaryString(fileSlice)
          }
        // }
      })
      return promise
    }
  }
}
</script>

<style scoped>

</style>
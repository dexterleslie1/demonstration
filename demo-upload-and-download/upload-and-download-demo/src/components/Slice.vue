<template>
  <div>
    <el-divider content-position="center">分片上传演示</el-divider>
    <el-row>
      <el-col :span="12">
        <el-upload
            ref="sliceUploadComponent"
            action=""
            :auto-upload="false"
            :limit="1"
            :file-list="sliceFileList"
            :multiple="false"
            :show-file-list="false"
            :http-request="handleSliceUploadFile"
            :on-preview="handleSliceOnPreview"
            :on-remove="handleSliceOnRemove"
            :on-success="handleSliceOnSuccess"
            :on-error="handleSliceOnError"
            :on-progress="handleSliceOnProgress"
            :on-change="handleSliceOnChange"
            :before-upload="handleSliceBeforeUpload"
            :before-remove="handleSliceBeforeRemove"
            :on-exceed="handleSliceOnExceed">
          <template #trigger>
            <el-button size="small" type="primary">选择上传文件 (注意: 最多选择1个文件)</el-button>
          </template>
          <el-button style="margin-left: 10px;" size="small" type="success" @click="handleSliceUploadSubmit">上传</el-button>
        </el-upload>
        <el-table
            :data="sliceFileList"
            style="width: 100%">
          <el-table-column
              prop="name"
              label="名称"
              width="180">
          </el-table-column>
          <el-table-column
              label="状态"
              width="200">
            <template #default="scope">
              <span v-if="scope.row.status===1">分析分片中...</span>
              <span v-else-if="scope.row.status===2">上传分片中...</span>
              <span v-else-if="scope.row.status===3">合并分片中...</span>
              <span v-else-if="scope.row.status===4">上传完毕!</span>
              <span v-else>准备上传</span>
              <el-progress v-if="scope.row.status===1 || scope.row.status===2" :percentage="scope.row.percent"></el-progress>
            </template>
          </el-table-column>
          <el-table-column
              prop="size"
              label="大小"
              width="100">
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
      </el-col>
      <el-col :span="12">
        <el-table
            :data="fileUploaded"
            style="width: 100%">
          <el-table-column
              prop="filename"
              label="名称"
              width="360">
          </el-table-column>
          <el-table-column
              prop="size"
              label="大小"
              width="180">
          </el-table-column>
          <el-table-column
              label="操作"
              width="180">
            <template #default="scope">
              <el-button
                  size="mini"
                  @click="handleFileDelete(scope.$index, scope.row)" type="danger" >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from "../util/axios"
import {ElMessage} from "element-plus"
import SparkMD5 from "spark-md5";

export default {
  name: "Slice",
  data() {
    return {
      sliceFileList: [],
      fileUploaded: []
    }
  },
  created() {
    this.initLoad()
  },
  methods: {
    initLoad() {
      request.get("/api/v1/extra/listUploaded", null).then((result) => {
        this.fileUploaded = result.data.data
      })
    },
    // 分片上传文件
    handleSliceUploadFile(param) {
      let thisRef = this
      let fileListObjectRef = this.sliceFileList[0]
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

      let file = param.file
      let filename = file.name
      let fileSize = file.size
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

      let sliceIndexToSliceMd5Map = new Map()

      let spark = new SparkMD5()

      // FileReader对象读取大文件显示读取进度
      reader.onprogress = function(event){
        let progress = ((totalLoadedSize/fileSize)*100).toFixed(0)
        progress = parseInt(progress)
        fileListObjectRef.status = 1
        fileListObjectRef.percent = progress
      }

      // FileReader对象读取文件完毕回调
      reader.onload = function(event){
        currentSliceIndex++
        totalLoadedSize += event.loaded
        let sliceMd5 = SparkMD5.hashBinary(event.target.result);
        sliceIndexToSliceMd5Map.set(currentSliceIndex-1, sliceMd5)
        spark.appendBinary(event.target.result)
        let message = "已读取第 " + currentSliceIndex + " 个分片，读取字节数: " + event.loaded + "，分片md5:" + sliceMd5;
        console.log(message)
        if(currentSliceIndex<=totalSlices-1) {
          readSlice(currentSliceIndex)
        } else {
          let fileMd5 = spark.end()
          message = "读取完毕，总共读取: " + totalLoadedSize + " 字节，文件md5: " + fileMd5;
          console.log(message)

          // 读取完整个文件md5之后上传分片到服务器
          doUpload(fileMd5)
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

      function doUpload(fileMd5) {
        // 判断分片是否已上传
        let slices = new Array()
        for (let [key, value] of sliceIndexToSliceMd5Map) {
          let sliceTemporary = {order:key+1, md5:value}
          slices.push(sliceTemporary)
        }
        let sliceVO = {fileMd5:fileMd5, slices:slices}
        request.post("/api/v1/upload/sliceExists", sliceVO).then(res => {
          t1(res.data.data)
        }).catch(err => {
          ElMessage({
            type: 'error',
            message: err
          })
        })

        function t1(sliceVO) {
          fileListObjectRef.status = 2
          // 总共上传字节数
          let totalUploaded = 0
          let currentUploadSliceIndex = 0

          doUploadSlice(currentUploadSliceIndex)

          function doUploadSlice(sliceIndex) {
            let sliceMd5 = sliceIndexToSliceMd5Map.get(sliceIndex)
            let start = sliceIndex * eachSliceSize
            let end = start + eachSliceSize
            if (end > fileSize) {
              end = fileSize
            }

            totalUploaded = end
            let num = ((totalUploaded / fileSize) * 100).toFixed(0)
            num = parseInt(num)
            fileListObjectRef.percent = num

            let sliceExists = sliceVO.slices[sliceIndex].exists
            if (!sliceExists) {
              let fileSlice = file.slice(start, end)
              let formData = new FormData()
              formData.append("file", fileSlice)
              formData.append("fileMd5", fileMd5)
              formData.append("sliceMd5", sliceMd5)
              let config = {
                headers: {
                  'Content-Type': 'multipart/form-data'
                }
              }

              request.post("/api/v1/upload/sliceUpload", formData, config).then(res => {
                console.log(`第${currentUploadSliceIndex+1}个分片上传成功，服务器响应: ${res.data.data}`)
              }).catch(err => {
                console.log("上传分片错误，原因:" + err)
              }).finally(() => {
                currentUploadSliceIndex++
                if (currentUploadSliceIndex < totalSlices) {
                  doUploadSlice(currentUploadSliceIndex)
                } else {
                  console.log(`所有分片上传成功，总数:${totalSlices}，总上传字节数:${totalUploaded}`)
                  merge(sliceVO, filename)
                }
              })
            } else {
              console.log(`第${currentUploadSliceIndex+1}个分片已存在，不需要重复上传`)
              currentUploadSliceIndex++
              if (currentUploadSliceIndex < totalSlices) {
                doUploadSlice(currentUploadSliceIndex)
              } else {
                console.log(`所有分片上传成功，总数:${totalSlices}，总上传字节数:${totalUploaded}`)
                merge(sliceVO, filename)
              }
            }
          }
        }

        function merge(sliceVO, filename) {
          sliceVO.filename = filename
          fileListObjectRef.status = 3
          request.put("/api/v1/upload/sliceMerge", sliceVO).then(res => {
            ElMessage({
              type: 'success',
              message: res.data.data
            })
            fileListObjectRef.status = 4
            thisRef.initLoad()
          }).catch(err => {
            ElMessage({
              type: 'error',
              message: err
            })
          })
        }
      }
    },
    handleSliceUploadSubmit() {
      this.$refs.sliceUploadComponent.submit()
    },
    handleSliceOnPreview(file) {
      // 点击文件列表中已上传的文件时的钩子
      let filename = file.name
      console.log(`点击文件 ${filename}`)
    },
    handleSliceOnRemove(file, fileList) {
      // 文件列表移除文件时的钩子
      let filename = file.name
      console.log(`删除文件 ${filename}`)
    },
    handleSliceOnSuccess(response, file, fileList) {
      // 文件上传成功时的钩子
      let filename = file.name
      console.log(`文件上传成功 ${filename}`)
    },
    handleSliceOnError(error, file, fileList) {
      // 文件上传失败时的钩子
      let filename = file.name
      console.log(`文件上传失败 ${filename}，原因: ${error}`)
    },
    handleSliceOnProgress(event, file, fileList) {
      // 文件上传时的钩子
      console.log(`文件 ${file.name} 正在上传中，进度: ${event.percent}%`)
    },
    handleSliceOnChange(file, fileList) {
      // 文件状态改变时的钩子，添加文件、上传成功和上传失败时都会被调用
      console.log(`文件 ${file.name} 状态改变，状态: ${file.status}`)
      this.sliceFileList = fileList
    },
    handleSliceBeforeUpload(file) {
      // 上传文件之前的钩子，参数为上传的文件，若返回 false 或者返回 Promise 且被 reject，则停止上传。
      console.log(`上传文件 ${file.name} 之前before upload`)
    },
    handleSliceBeforeRemove(file, fileList) {
      // 删除文件之前的钩子，参数为上传的文件和文件列表，若返回 false 或者返回 Promise 且被 reject，则停止删除。
      console.log(`删除文件 ${file.name} 之前before remove`)
    },
    handleSliceOnExceed(files, fileList) {
      // 文件超出个数限制时的钩子
      console.log(`最多选择10个文件，当前选择${files.length}个`)
    },
    handleDelete(index, row) {
      this.sliceFileList.splice(index, 1)
    },
    handleFileDelete(index, row) {
      this.$confirm(`确定删除文件 ${row.filename} 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let filename = row.filename
        let url = `api/v1/extra/deleteUploaded?filename=${filename}`
        request.put(url).then((result)=>{
          this.initLoad()
          ElMessage({
            type: 'success',
            message: result.data.data
          })
        }).catch((error)=>{
          ElMessage({
            type: 'error',
            message: error
          })
        })
      }).catch(() => {
        // 取消删除
      })
    },
  }
}
</script>

<style scoped>

</style>
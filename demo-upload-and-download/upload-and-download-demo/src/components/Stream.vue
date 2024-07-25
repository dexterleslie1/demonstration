<template>
  <div>
    <el-divider content-position="center">流式上传演示</el-divider>
    <el-row>
      <el-col :span="12">
        <el-upload
            ref="uploadComponent"
            action=""
            :auto-upload="false"
            :limit="10"
            :file-list="fileList"
            multiple="true"
            :http-request="handleUploadFile">
          <template #trigger>
            <el-button size="small" type="primary">选择上传文件 (注意: 最多选择10个文件)</el-button>
          </template>
          <el-button style="margin-left: 10px;" size="small" type="success" @click="handleUploadSubmit">上传</el-button>
        </el-upload>
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
                  @click="handleDelete(scope.$index, scope.row)" type="danger" >删除</el-button>
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

export default {
  name: "Stream",
  data() {
    return {
      fileList: [],
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
    handleUploadFile(param) {
      let formData = new FormData()
      formData.append("file", param.file)
      let config = {
        headers: {
          'Content-Type': 'multipart/form-data'
        }, onUploadProgress: (progressEvent) => {
          // 使用本地 progress 事件
          if (progressEvent.lengthComputable) {
            let num = Math.round((progressEvent.loaded / progressEvent.total) * 100)
            param.onProgress({percent: num})
          }
        }
      }
      request.post("/api/v1/upload/stream",formData,config).then(res => {
        ElMessage({
          type: 'success',
          message: res.data.data
        })
        param.onSuccess()
        this.initLoad()
      }).catch(err => {
        param.onError()
        this.initLoad()
      })
    },
    handleDelete(index, row) {
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
    handleUploadSubmit() {
      this.$refs.uploadComponent.submit()
    }
  }
}
</script>

<style scoped>

</style>
<template>
  <div>
    <el-divider content-position="center">流式下载演示</el-divider>
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
          <el-link :href="'/api/v1/download/stream/' + scope.row.filename">[下载]</el-link>
        </template>
      </el-table-column>
    </el-table>

    <el-divider content-position="center">图片预览演示</el-divider>
    <el-row>
      <el-col :span="12">
        <el-table
            :data="fileImages"
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
              <el-button type="info" @click="handleOnImagePreview(scope.$index, scope.row)">预览</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <el-col :span="12">
        <el-image :src="src">
          <template #placeholder>
            <div class="image-slot">
              加载中<span class="dot">...</span>
            </div>
          </template>
        </el-image>
      </el-col>
    </el-row>

    <el-divider content-position="center">范围下载演示</el-divider>
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
          <el-link :href="'/api/v1/download/range/' + scope.row.filename">[下载]</el-link>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from "../util/axios";

export default {
  name: "Download",
  data() {
    return {
      fileUploaded:[],
      fileImages:[],
      src: ''
    }
  },
  created() {
    this.initLoad()
    this.initLoadImages()
  },
  methods: {
    initLoad() {
      request.get("/api/v1/extra/listUploaded", null).then((result) => {
        this.fileUploaded = result.data.data
      })
    },
    initLoadImages() {
      request.get("/api/v1/extra/listUploadedImages", null).then((result) => {
        this.fileImages = result.data.data
      })
    },
    handleOnImagePreview(index, row) {
      this.src = "/api/v1/download/stream/" + row.filename
    }
  }
}
</script>

<style scoped>

</style>
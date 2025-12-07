<template>
  <div id="app">
    <div>基础用法 - 页数较少时的效果</div>
    <div>
      <el-pagination layout="prev,pager,next" :total="50"></el-pagination>
    </div>
    <hr />

    <div>基础用法 - 大于 7 页时的效果</div>
    <div>
      <el-pagination layout="prev,pager,next" :total="1000"></el-pagination>
    </div>
    <hr />

    <div>带有背景色的分页</div>
    <div>
      <el-pagination layout="prev,pager,next" :total="1000" background></el-pagination>
    </div>
    <hr />

    <div>附加功能 - 显示总数</div>
    <div>
      <el-pagination layout="total,prev,pager,next" :total="1000" :page-size="100" :current-page="currentPage1"
        @current-change="handleCurrentChange" @size-change="handleSizeChange"></el-pagination>
    </div>
    <hr />

    <div>附加功能 - 调整每页显示条数</div>
    <div>
      <el-pagination layout="sizes,prev,pager,next" :total="1000" :page-size="100" :current-page="currentPage2"
        :page-sizes="[1, 2, 3, 4, 5]" @current-change="handleCurrentChange"
        @size-change="handleSizeChange"></el-pagination>
    </div>
    <hr />

    <div>附加功能 - 直接前往</div>
    <div>
      <el-pagination layout="prev,pager,next,jumper" :total="1000" :page-size="100" :current-page="currentPage3"
        @current-change="handleCurrentChange" @size-change="handleSizeChange"></el-pagination>
    </div>
    <hr />

    <div>附加功能 - 完整功能</div>
    <div>
      <el-pagination layout="total,sizes,prev,pager,next,jumper" :total="1000" :page-size="100"
        :current-page="currentPage4" :page-sizes="[1, 2, 3, 4, 5]" @current-change="handleCurrentChange"
        @size-change="handleSizeChange"></el-pagination>
    </div>
    <hr />
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    HelloWorld
  },
  data() {
    return {
      currentPage1: 1,
      currentPage2: 1,
      currentPage3: 1,
      currentPage4: 1,
    }
  },
  methods: {
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
    }
  }
}
</script>

<style>
#app {}
</style>

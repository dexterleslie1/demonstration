<template>
  <div>
    <el-divider content-position="left">演示el-table用法</el-divider>
    <div style="width:50%;display:inline-block;">
      <el-table :data="dataSelected">
        <el-table-column prop="name" label="赛事" width="180" />
        <el-table-column prop="startTime" label="时间" width="180" />
        <el-table-column prop="sourceName" label="源" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button link type="danger" size="small" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div style="width:50%;display:inline-block;">
      <el-table :data="getDataToSelect()">
        <el-table-column prop="name" label="赛事" />
        <el-table-column prop="startTime" label="时间" />
        <el-table-column prop="sourceName" label="源" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleSelect(scope.$index, scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-divider content-position="left">多个属性值显示为一列</el-divider>
    <!-- https://stackoverflow.com/questions/67565022/el-table-column-prop-with-several-values-in-an-array -->
    <el-table :data="dataTestList">
        <el-table-column label="名称" >
          <template #default="scope">
            {{ scope.row.firstname }}.{{ scope.row.lastname }}
          </template>
        </el-table-column>
      </el-table>
  </div>
</template>

<script>
import _ from 'lodash'

export default {
  name: "App",
  data() {
    return {
      dataToSelectOriginalReadOnly: [],
      dataSelected: [],
      dataTestList: [{
        firstname: "Dexter",
        lastname: "Chan"
      }]
    }
  },
  created() {
    this.dataToSelectOriginalReadOnly = [
      {
        id: 1,
        name: "中国vs巴西",
        startTime: "2022-12-26 05:00:00",
        sourceName: "源1"
      },{
        id: 2,
        name: "中国1vs巴西1",
        startTime: "2022-12-26 05:00:00",
        sourceName: "源2"
      },{
        id: 3,
        name: "美国vs加拿大",
        startTime: "2022-12-27 05:00:00",
        sourceName: "源1"
      },{
        id: 4,
        name: "美国1vs加拿大1",
        startTime: "2022-12-27 05:00:00",
        sourceName: "源2"
      },{
        id: 5,
        name: "美国3vs加拿大3",
        startTime: "2022-12-27 05:00:00",
        sourceName: "源3"
      },
    ]
  },
  methods: {
    handleSelect(index, row) {
      var id = row.id
      var index = _.findIndex(this.dataSelected, function(o) {
        if(o.id==id)
          return true
        return false
      })
      if(index == -1)
        this.dataSelected.splice(this.dataSelected.length, 0, row)
    },
    handleDelete(indexP, row) {
      _.remove(this.dataSelected, function(value, index, array) {
        if(index==indexP)
          return true
      })
    },
    getDataToSelect() {
      var sourceNameList = []
      for(var i=0; i<this.dataSelected.length; i++) {
        var selectedRow = this.dataSelected[i]
        sourceNameList.splice(sourceNameList.length, 0, selectedRow.sourceName)
      }

      var startTime
      if(this.dataSelected.length >= 1) {
        startTime = this.dataSelected[0].startTime
      }

      var dataToSelect = []
      for(var i=0; i<this.dataToSelectOriginalReadOnly.length; i++) {
        var row = this.dataToSelectOriginalReadOnly[i]
        var sourceName = row.sourceName
        var index = _.indexOf(sourceNameList, sourceName)
        if(index == -1 && (!startTime || row.startTime == startTime))
          dataToSelect.splice(dataToSelect.length, 0, row)
      }
      return dataToSelect
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
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>

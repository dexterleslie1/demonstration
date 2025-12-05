<template>
  <div id="app">
    <!-- 搜索区域 -->
    <el-form :inline="true" size="small">
      <el-form-item prop="name" label="部门名称">
        <el-input v-model="name" placeholder="请输入部门名称" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" size="mini" type="primary" @click="handleClickSearch()">搜索</el-button>
        <!-- <el-button icon="el-icon-refresh" size="mini" @click="handleClickReset()">重置</el-button> -->
      </el-form-item>
    </el-form>

    <!-- 快速操作按钮区域 -->
    <div>
      <el-button type="primary" plain icon="el-icon-plus" size="mini"
        @click="handleClickAddNavWithoutParent">新增</el-button>
    </div>

    <!-- 部门列表显示区域 -->
    <el-table :data="deptTree" row-key="id" v-loading="loading" default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
      <el-table-column prop="name" label="部门名称"></el-table-column>
      <el-table-column prop="orderNum" label="显示顺序"></el-table-column>
      <el-table-column prop="createTime" label="创建时间"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scopeVar">
          <el-button size="mini" type="text" icon="el-icon-edit"
            @click="handleClickUpdateNav(scopeVar.row)">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" @click="handleClickAddNav(scopeVar.row)">新增</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete"
            @click="handleClickDelete(scopeVar.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增或者修改部门弹出框区域 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible">
      <!-- 注意：需要设置label-width="100px"，否则上级部门选择器显示变形 -->
      <el-form :model="deptForm" label-width="100px">
        <el-form-item v-if="deptTreeSelectorVisible" label="上级部门">
          <el-tree :data="deptTree" :props="{ label: 'name', children: 'children' }" @node-click="handleNodeClick"
            :default-expand-all="true" :expand-on-click-node="false"></el-tree>
        </el-form-item>
        <el-form-item label="部门名称">
          <el-input v-model="deptForm.name" />
        </el-form-item>
        <el-form-item label="显示顺序">
          <el-input v-model="deptForm.orderNum" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handleClickDoAddOrUpdate()">确定</el-button>
      </template>
    </el-dialog>
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
      // 新增或者修改部门dialog的标题
      dialogTitle: '新增部门',
      // 部门名称搜索
      name: '',
      // 数据加载中指示器开关
      loading: false,
      // 开发前端未接入后端api时，用于协助调试前端代码mock数据
      // deptList: [
      //   { id: 1, name: '部门1', order_num: 1, create_time: '2021-07-22 13:10:07', parentId: null },
      //   { id: 2, name: '部门2', order_num: 2, create_time: '2021-07-22 13:10:07', parentId: null },
      //   { id: 21, name: '部门21', order_num: 1, create_time: '2021-07-22 13:10:07', parentId: 2 },
      //   { id: 22, name: '部门22', order_num: 2, create_time: '2021-07-22 13:10:07', parentId: 2 },
      //   { id: 23, name: '部门23', order_num: 3, create_time: '2021-07-22 13:10:07', parentId: 2 },
      //   { id: 3, name: '部门3', order_num: 3, create_time: '2021-07-22 13:10:07', parentId: null },
      // ],
      // 部门树形结构列表数据
      deptTree: null,
      dialogFormVisible: false,
      // 新增部门时有两种情况：1、指定上级部门新增部门，2、不指定上级部门新增部门
      // 此字段用于适应这两种情况是否显示上级部门树形控件选择器
      deptTreeSelectorVisible: false,
      // 新增或者修改部门的form数据
      deptForm: {
        id: null,
        parentId: null,
        name: '',
        orderNum: 0,
      },
      // 当前状态是新增部门吗？true为是，否则为更新部门信息
      deptAdd: false
    }
  },
  mounted() {
    this.loadDept()
  },
  methods: {
    // 加载部门数据
    loadDept() {
      // this.deptTree = this.buildTreeFromList(this.deptList)
      this.loading = true
      this.$axios.get("api/v1/dept/list", {
        params: { name: this.name },
      }).then((data) => {
        this.deptTree = this.buildTreeFromList(data)
      }).catch(function (error) {
        alert(JSON.stringify(error))
      }).finally(() => {
        this.loading = false
      })
    },
    // 点击搜索按钮
    handleClickSearch() {
      this.loadDept()
    },
    // 把后端返回的部门列表转换为树形结构的部门列表
    buildTreeFromList(deptList) {
      // 部门id和部门对象对照表
      var idToDeptMap = {}
      for (var dept of deptList) {
        var deptId = dept.id
        idToDeptMap[deptId] = dept;
      }

      // 所有顶级部门
      var deptListTopLevel = []
      for (var dept of deptList) {
        var parentId = dept.parentId
        // 当前部门列表中不存在的上级部门说明是顶级部门
        if (!idToDeptMap[parentId]) {
          dept = this.buildTreeNodeChildrenFieldRecursively(dept, deptList)
          deptListTopLevel.push(dept)
        }
      }
      return deptListTopLevel
    },
    // 递归构建部门树节点的children字段
    buildTreeNodeChildrenFieldRecursively(deptParent, deptList) {
      var deptId = deptParent.id;
      var deptListChildren = []
      for (var dept of deptList) {
        var deptParentId = dept.parentId
        if (deptParentId == deptId) {
          deptListChildren.push(dept)
        }
      }

      if (deptListChildren.length > 0) {
        for (var dept of deptListChildren) {
          this.buildTreeNodeChildrenFieldRecursively(dept, deptList)
        }
      }

      deptParent.children = deptListChildren
      return deptParent
    },
    // 点击删除部门按钮
    handleClickDelete(dept) {
      this.$axios.delete("api/v1/dept/delete", {
        params: { id: dept.id },
      }).then((data) => {
        this.loadDept()
      }).catch(function (error) {
        alert(JSON.stringify(error))
      })
    },
    // 指定上级部门情况新增部门
    handleClickAddNav(dept) {
      this.dialogTitle = "新增部门"
      this.deptAdd = true
      this.deptForm.parentId = dept.id
      this.dialogFormVisible = true
      this.deptTreeSelectorVisible = false
    },
    // 没有指定上面部门情况新增部门
    handleClickAddNavWithoutParent() {
      this.dialogTitle = "新增部门"
      this.deptAdd = true
      this.deptForm.parentId = null
      this.dialogFormVisible = true
      this.deptTreeSelectorVisible = true
    },
    // 点击弹出框确定新增或者修改部门
    handleClickDoAddOrUpdate() {
      // 校验是否选择上级部门
      if (this.deptAdd && this.deptTreeSelectorVisible) {
        if (!this.deptForm.parentId) {
          alert("请选择上级部门！")
          return
        }
      }

      if (this.deptAdd) {
        // 新增部门
        this.$axios.post("api/v1/dept/add", this.deptForm, null)
          .then((data) => {
            this.loadDept()
            this.dialogFormVisible = false
          }).catch(function (error) {
            alert(JSON.stringify(error))
          })
      } else {
        // 更新部门信息
        this.$axios.put("api/v1/dept/update", this.deptForm, null)
          .then((data) => {
            this.loadDept()
            this.dialogFormVisible = false
          }).catch(function (error) {
            alert(JSON.stringify(error))
          })
      }
    },
    // 点击修改部门
    handleClickUpdateNav(dept) {
      this.dialogTitle = "修改部门"
      this.deptAdd = false
      this.deptForm.id = dept.id
      this.deptForm.name = dept.name
      this.deptForm.orderNum = dept.orderNum
      this.dialogFormVisible = true
      this.deptTreeSelectorVisible = false
    },
    // 新增部门时，在弹出框中点击部门选择器的节点
    handleNodeClick(data) {
      this.deptForm.parentId = data.id
    }
  }
}
</script>

<style></style>

<template>
  <div id="app">
    <el-row :gutter="20">
      <el-col :span="4">
        <!-- 左侧部门导航 -->
        <el-input v-model="deptName" placeholder="请输入部门名称" size="mini"></el-input>
        <!-- :expand-on-click-node="false"表示点击树中的节点不折叠和展开 -->
        <!-- :highlight-current="true"表示选中的树节点高亮显示 -->
        <el-tree :data="deptTree" default-expand-all @node-click="handleNodeClickDeptTree" :expand-on-click-node="false"
          :props="{ label: 'name', children: 'children' }" node-key="id" v-loading="deptTreeLoading"
          :filter-node-method="filterNode" ref="deptTree" :highlight-current="true">
        </el-tree>
      </el-col>
      <!-- 右侧区域 -->
      <el-col :span="20">
        <!-- 搜索区域 -->
        <el-form :inline="true" size="small">
          <el-form-item label="用户名称">
            <el-input v-model="userName" placeholder="请输入用户名称" size="mini"></el-input>
          </el-form-item>
          <el-form-item label="创建时间">
            <!-- type="daterange"表示日期范围选择 -->
            <!-- unlink-panels表示左右日期选择面板不联动 -->
            <!-- value-format="yyyy-MM-dd"表示日期值的格式为yyyy-MM-dd -->
            <el-date-picker size="mini" v-model="createTimeRange" type="daterange" unlink-panels range-separator="-"
              start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd"></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="mini" @click="handleClickSearch">搜索</el-button>
          </el-form-item>
        </el-form>

        <!-- 快捷操作按钮区域 -->
        <el-row>
          <el-col :span="24">
            <el-button size="mini" type="primary" plain icon="el-icon-plus"
              @click="handleClickQuickUserAddNav">新增</el-button>
            <el-button size="mini" type="success" plain icon="el-icon-edit"
              :disabled="!(userListSelection && userListSelection.length == 1)"
              @click="handleClickQuickUserEditNav">修改</el-button>
            <el-button size="mini" type="danger" plain icon="el-icon-delete"
              :disabled="!(userListSelection && userListSelection.length > 0)"
              @click="handleClickQuickUserDelete">删除</el-button>
          </el-col>
        </el-row>
        <!-- 用户列表区域 -->
        <el-row>
          <el-col :span="24">
            <!-- @selection-change="handleSelectionChange"表示选中用户记录的响应事件 -->
            <el-table :data="userData" v-loading="userListLoading" size="small"
              @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="55" align="center"></el-table-column>
              <el-table-column prop="userName" label="登录名" align="center"></el-table-column>
              <el-table-column prop="nickName" label="昵称" align="center"></el-table-column>
              <el-table-column prop="dept.name" label="所属部门" align="center"></el-table-column>
              <el-table-column prop="createTime" label="创建时间" align="center"></el-table-column>
              <el-table-column label="操作" align="center">
                <template slot-scope="scopeVar">
                  <el-button type="text" size="mini" @click="handleClickUserEditNav(scopeVar.row)"
                    icon="el-icon-edit">修改</el-button>
                  <el-button type="text" size="mini" @click="handleClickUserDelete(scopeVar.row)"
                    icon="el-icon-delete">删除</el-button>
                  <el-button type="text" size="mini" @click="handleClickUserResetPassword(scopeVar.row)"
                    icon="el-icon-key">重置密码</el-button>
                </template>
              </el-table-column>
            </el-table>
            <!-- :current-page.sync="paginationCurrentPage"表示el-pagination组件支持修改paginationCurrentPage变量 -->
            <!-- :page-sizes="[2, 3, 4, 5]"表示每页显示的记录数选项 -->
            <!-- :page-size.sync="paginationPageSize"表示每页显示的记录数 -->
            <!-- :total="paginationTotal"表示总记录数 -->
            <el-pagination layout="total,sizes,prev,pager,next,jumper" :total="paginationTotal"
              :page-size.sync="paginationPageSize" :page-sizes="[2, 3, 4, 5]" :current-page.sync="paginationCurrentPage"
              style="position:absolute;right:0">

            </el-pagination>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <!-- 新增或者修改用户 -->
    <el-dialog :visible.sync="dialogFormVisible" size="small" :title="dialogFormTitle">
      <el-form :form="dialogForm" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户所属部门">
              <treeselect v-model="dialogForm.deptId" :multiple="false" :options="deptTree" placeholder="请选择所属部门"
                :default-expand-level="20" :normalizer="normalizer" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户登录名">
              <el-input size="mini" v-model="dialogForm.userName" placeholder="请输入用户登录名"
                :disabled="dialogFormInEditState"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称">
              <el-input size="mini" v-model="dialogForm.nickName" placeholder="请输入用户昵称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录密码">
              <el-input size="mini" v-model="dialogForm.password" placeholder="请输入登录密码" show-password
                :disabled="dialogFormInEditState"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button type="primary" size="mini" @click="handleClickDialogFormConfirm">确定</el-button>
        <el-button size="mini" @click="dialogFormVisible = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

export default {
  name: 'App',
  components: {
    Treeselect
  },
  data() {
    return {
      // 左边部门导航树根据部门名称过滤
      deptName: '',
      // 根据用户名称搜索
      userName: '',
      // 创建时间搜索
      createTimeRange: '',
      // 当前部门树形控件选中的部门id
      deptId: null,
      // 部门导航数据加载中指示
      deptTreeLoading: false,
      deptTree: null,
      // 用户表格数据
      userData: null,
      // 用户列表数据加载中指示
      userListLoading: false,
      // 多选选择的用户列表
      userListSelection: null,
      // 分页控件total
      paginationTotal: 0,
      // 分页控件page-size
      paginationPageSize: 2,
      // 分页控件current-page
      paginationCurrentPage: 1,
      // 新增或者修改用户dialog form
      dialogForm: {
        id: null,
        userName: null,
        nickName: null,
        password: null,
        deptId: null,
      },
      // 新增或者修改用户dialog form是否显示
      dialogFormVisible: false,
      // 新增或者修改用户dialog form显示的标题
      dialogFormTitle: null,
      // dialog form是否处于修改用户信息状态
      dialogFormInEditState: false,
    }
  },
  mounted() {
    this.loadDept()
    this.loadUserList()
  },
  methods: {
    // 重置dialog form
    resetDialogForm() {
      this.dialogForm.id = null
      this.dialogForm.userName = null
      this.dialogForm.nickName = null
      this.dialogForm.password = null
      this.dialogForm.deptId = null
    },
    // 点击搜索按钮
    handleClickSearch() {
      this.loadUserList()
    },
    // 加载部门数据
    loadDept() {
      this.deptTreeLoading = true
      this.$axios.get("/api/v1/dept/list", {
        params: { name: this.name },
      }).then((data) => {
        this.deptTree = this.buildTreeFromList(data.data)
      }).catch(function (error) {
        alert(JSON.stringify(error))
      }).finally(() => {
        this.deptTreeLoading = false
      })
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

      if (deptListChildren.length > 0) {
        deptParent.children = deptListChildren
      }
      return deptParent
    },
    // 节点过滤函数，此函数会被回调用于根据关键字过滤节点
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    // 点击部门导航树中的节点
    handleNodeClickDeptTree(data) {
      this.deptId = data.id
      this.loadUserList()
    },
    loadUserList() {
      var deptId = this.deptId
      var createTimeStart = null;
      var createTimeEnd = null;
      if (this.createTimeRange && this.createTimeRange.length > 0) {
        createTimeStart = this.createTimeRange[0]
        createTimeEnd = this.createTimeRange[1]
      }
      this.userListLoading = true
      this.$axios.get("/api/v1/user/list", {
        params: { deptId, userName: this.userName, createTimeStart, createTimeEnd, pageNum: this.paginationCurrentPage, pageSize: this.paginationPageSize },
      }).then((data) => {
        this.userData = data.data
        this.paginationTotal = data.totalRecords
      }).catch(function (error) {
        alert(JSON.stringify(error))
      }).finally(() => {
        this.userListLoading = false
      })
    },
    handleSelectionChange(val) {
      this.userListSelection = val
    },
    // 快捷操作按钮删除用户
    handleClickQuickUserDelete() {
      var idList = ""
      for (var user of this.userListSelection) {
        idList = idList + user.id + ","
      }
      this.$axios.delete("/api/v1/user/delete", {
        params: { idList },
      }).then((data) => {
        this.loadUserList()
      }).catch(function (error) {
        alert(JSON.stringify(error))
      }).finally(() => {
      })
    },
    // 点击用户后面的删除按钮
    handleClickUserDelete(user) {
      var id = user.id
      this.$axios.delete("/api/v1/user/delete", {
        params: { idList: id },
      }).then((data) => {
        this.loadUserList()
      }).catch(function (error) {
        alert(JSON.stringify(error))
      }).finally(() => {
      })
    },
    // 点击用户后面的重置密码按钮
    handleClickUserResetPassword(user) {
      var userName = user.userName
      this.$prompt('请输入"' + userName + '"的新密码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        var userId = user.id
        this.$axios.put("/api/v1/user/resetPassword", null, {
          params: { id: userId, password: value },
        }).then((data) => {
          this.$message({
            type: 'success',
            message: '成功重置密码'
          })
        }).catch(function (error) {
          alert(JSON.stringify(error))
        }).finally(() => {
        })
      }).catch(() => { })
    },
    // 点击用户后面的修改按钮
    handleClickUserEditNav(user) {
      this.dialogFormInEditState = true
      this.dialogFormTitle = '修改用户'
      this.dialogFormVisible = true
      this.dialogForm.id = user.id
      this.dialogForm.userName = user.userName
      this.dialogForm.nickName = user.nickName
      this.dialogForm.deptId = user.deptId
    },
    // 点击修改用户快捷按钮
    handleClickQuickUserEditNav() {
      this.handleClickUserEditNav(this.userListSelection[0])
    },
    // 点击dialog form的确定按钮
    handleClickDialogFormConfirm() {
      if (this.dialogFormInEditState) {
        this.$axios.put("/api/v1/user/update", this.dialogForm, {
        }).then((data) => {
          this.loadUserList()
        }).catch(function (error) {
          alert(JSON.stringify(error))
        }).finally(() => {
          this.dialogFormVisible = false
        })
      } else {
        this.$axios.post("/api/v1/user/add", this.dialogForm, {
        }).then((data) => {
          this.loadUserList()
        }).catch(function (error) {
          alert(JSON.stringify(error))
        }).finally(() => {
          this.dialogFormVisible = false
        })
      }
    },
    // 把部门deptTree数据结构转换为vue-treeselect所需要的数据结构
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children,
      }
    },
    // 点击新增用户快捷按钮
    handleClickQuickUserAddNav() {
      this.dialogFormInEditState = false
      this.dialogFormVisible = true
      // 重置dialog form
      this.resetDialogForm()
      this.dialogForm.deptId = this.deptId
    }
  },
  watch: {
    deptName(val) {
      // 触发树形控件过滤
      this.$refs.deptTree.filter(val);
    },
    // 用户列表翻页
    paginationCurrentPage(val) {
      this.loadUserList()
    }
  },
}
</script>

<style>
#app {}
</style>

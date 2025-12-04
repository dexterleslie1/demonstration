<template>
    <div>
        <el-button type="text" @click="dialogTableVisible = true">打开嵌套表格的 Dialog</el-button>
        <el-dialog title="收货地址" :visible.sync="dialogTableVisible">
            <el-table :data="tableData">
                <el-table-column prop="date" label="日期" width="150"></el-table-column>
                <el-table-column prop="name" label="姓名" width="200"></el-table-column>
                <el-table-column prop="address" label="地址"></el-table-column>
            </el-table>
        </el-dialog>

        <el-button type="text" @click="dialogFormVisible = true">打开嵌套表单</el-button>
        <el-dialog title="收货地址" :visible.sync="dialogFormVisible">
            <el-form :model="form">
                <el-form-item label="活动名称：" label-width="120px">
                    <el-input v-model="form.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="活动区域：" label-width="120px">
                    <el-select v-model="form.region" placeholder="请选择活动区域">
                        <el-option label="区域一" value="shanghai"></el-option>
                        <el-option label="区域二" value="beijing"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogFormVisible = false">取消</el-button>
                <el-button type="primary" @click="dialogFormVisible = false">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
export default {
    data() {
        return {
            dialogTableVisible: false,
            tableData: [{
                date: '2016-05-02',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
            }, {
                date: '2016-05-04',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
            }, {
                date: '2016-05-01',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
            }, {
                date: '2016-05-03',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
            }],

            dialogFormVisible: false,
            form: {
                name: '',
                region: '',
            },
        }
    }
}
</script>

<style scoped></style>
<template>
    <!-- 演示自定义列模板表格 -->
    <el-table :data="tableData">
        <el-table-column label="日期" width="180">
            <template slot-scope="scopeVar">
                <i class="el-icon-time"></i>
                <span>{{ scopeVar.row.date }}</span>
            </template>
        </el-table-column>
        <el-table-column label="姓名" width="180">
            <template slot-scope="scopeVar">
                <el-popover trigger="hover" placement="top">
                    <p>姓名：{{ scopeVar.row.name }}</p>
                    <p>住址：{{ scopeVar.row.address }}</p>
                    <div slot="reference">
                        <el-tag size="medium">{{ scopeVar.row.name }}</el-tag>
                    </div>
                </el-popover>
            </template>
        </el-table-column>
        <el-table-column>
            <template slot-scope="scopeVar">
                <el-button size="mini" @click="handleEdit(scopeVar.$index, scopeVar.row)">编辑</el-button>
                <el-button size="mini" type="danger" @click="handleDelete(scopeVar.$index, scopeVar.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
export default {
    data() {
        return {
            tableData: [{
                date: '2016-05-02',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
            }, {
                date: '2016-05-04',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1517 弄'
            }, {
                date: '2016-05-01',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1519 弄'
            }, {
                date: '2016-05-03',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1516 弄'
            }]
        }
    },
    methods: {
        handleEdit(index, row) {
            alert(`index=${index},row=${JSON.stringify(row)}`);
        },
        handleDelete(index, row) {
            alert(`index=${index},row=${JSON.stringify(row)}`);
        }
    }
}
</script>

<style scoped></style>
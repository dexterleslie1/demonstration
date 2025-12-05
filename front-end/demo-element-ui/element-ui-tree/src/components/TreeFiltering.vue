<template>
    <!-- 树形控件的节点过滤用法 -->
    <div>
        <el-input placeholder="输入关键字进行过滤" v-model="filterText" />
        <el-tree :data="treeData" :props="{ label: 'label', children: 'children' }" default-expand-all
            :filter-node-method="filterNode" ref="tree" node-key="id"></el-tree>
    </div>
</template>

<script>
export default {
    data() {
        return {
            filterText: '',
            treeData: [{
                id: 1,
                label: '一级 1',
                children: [{
                    id: 4,
                    label: '二级 1-1',
                    children: [{
                        id: 9,
                        label: '三级 1-1-1'
                    }, {
                        id: 10,
                        label: '三级 1-1-2'
                    }]
                }]
            }, {
                id: 2,
                label: '一级 2',
                children: [{
                    id: 5,
                    label: '二级 2-1'
                }, {
                    id: 6,
                    label: '二级 2-2'
                }]
            }, {
                id: 3,
                label: '一级 3',
                children: [{
                    id: 7,
                    label: '二级 3-1'
                }, {
                    id: 8,
                    label: '二级 3-2'
                }]
            }],
        }
    },
    methods: {
        // 节点过滤函数，此函数会被回调用于根据关键字过滤节点
        filterNode(value, data) {
            if (!value) return true;
            return data.label.indexOf(value) !== -1;
        }
    },
    watch: {
        filterText(val) {
            // 触发树形控件过滤
            this.$refs.tree.filter(val);
        }
    },
}
</script>

<style scoped></style>
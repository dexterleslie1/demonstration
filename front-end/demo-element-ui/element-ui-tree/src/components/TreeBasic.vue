<template>
    <!-- 树形控件的基本用法 -->
    <div>
        <!-- :expand-on-click-node="false"表示点击节点不折叠或展开 -->
        <el-tree :data="treeData" :props="{ children: 'children', label: 'label' }" @node-click="handleClickNode"
            :default-expand-all="true" :expand-on-click-node="false" node-key="id"></el-tree>
    </div>
</template>

<script>

export default {
    name: 'TreeBasic',
    data() {
        return {
            treeData: [
                {
                    id: 1,
                    label: '1',
                    orderNum: 1,
                    children: [{
                        id: 11,
                        label: '1-1',
                        children: [{
                            id: 111,
                            label: '1-1-1',
                            orderNum: 1,
                        }, {
                            id: 112,
                            label: '1-1-2',
                            orderNum: 2,
                        }]
                    }]
                }, {
                    id: 2,
                    label: '2',
                    orderNum: 2,
                    children: [
                        {
                            id: 21,
                            label: '2-1',
                            orderNum: 1,
                        },
                        {
                            id: 22,
                            label: '2-2',
                            orderNum: 2,
                        }
                    ]
                }
            ]
        }
    },
    methods: {
        handleClickNode(data) {
            console.log(`label=${data.label},orderNum=${data.orderNum}`)
        }
    }
}
</script>

<style></style>

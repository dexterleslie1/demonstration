<template>
    <el-dialog
        title="联系人选择器"
        :visible.sync="visible"
        width="60%"
        :before-close="handleBeforeClose">
        <el-transfer
            style="text-align:left;width:100%;"
            filterable
            :titles="titles"
            :props="alias"
            :data="contactList"
            v-model="contactIdListSelected"
            :left-default-checked="leftDefaultChecked"
            :right-default-checked="rightDefaultChecked"
            filter-placeholder="输入昵称/拼音搜索"
            :filter-method="filterMethod"
            @change="handleChange">
            <span slot-scope="{ option }">
                <div style="display:flex;">
                    <div style="display:flex;align-items: center;justify-content: center;">
                        <el-image 
                            fit="fill" 
                            :src="option.avatar"
                            style="height:20px;width:20px;"></el-image>
                    </div>
                    <div style="text-align:center; vertical-align:middle;">
                        {{ option.nickname }}
                    </div>
                </div>
            </span>  
        </el-transfer>
        <span slot="footer" class="dialog-footer">
            <el-button @click="handleCancel">取 消</el-button>
            <el-button type="primary" @click="handleConfirm">确 定</el-button>
        </span>
    </el-dialog>
</template>

<script>
export default {
    props: {
        // 是否显示el-dialog
        visible: {
            type: Boolean,
            required: true
        }
    },
    data() {
        return {
            // 标题
            titles: ['联系人', '已选择'],
            // 联系人数据
            contactList: [],
            // 已选择联系人
            contactIdListSelected: [5, 7, 8],
            // 数据源别名
            alias: {
                key: 'id',
                label: 'nickname'
            },
            // 左边默认选中联系人
            leftDefaultChecked: [2, 3],
            // 右边默认选择联系人
            rightDefaultChecked: [5],
            // 搜索过滤方法
            filterMethod(query, item) {
                return item.nickname.indexOf(query)>-1 
                || item.remarkPinyin.indexOf(query)>-1
            }
        }
    },
    created() {
        // 生成联系人数据
        for(let i=0; i<50; i++) {
            this.contactList.push({
                id: i+1,
                nickname: `昵称${i+1}`,
                avatar: require('@/assets/logo.png'),
                remarkPinyin: `nicheng${i+1}`
            })
        }
    },
    methods: {
      handleChange(value, direction, movedKeys) {
        console.log(value, direction, movedKeys);
      },
      handleCancel() {
        this.$emit('update:visible', false)
        this.$emit('cancelCallback', this.contactIdListSelected)
      },
      handleConfirm() {
        this.$emit('update:visible', false)
        this.$emit('confirmCallback', this.contactIdListSelected)
      },
      handleBeforeClose() {
        this.$emit('update:visible', false)
        this.$emit('cancelCallback', this.contactIdListSelected)
      }
    }
}
</script>
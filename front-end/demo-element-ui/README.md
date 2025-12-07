## `Vue2`集成`Element-UI`

>请参考本站 <a href="/vue/集成element-ui.html" target="_blank">链接</a>

## 按钮Button

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-button
>
>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/button)

### 朴素按钮

```vue
<div>朴素按钮</div>
<div>
  <el-button plain>朴素按钮</el-button>
  <el-button type="primary" plain>主要按钮</el-button>
  <el-button type="success" plain>成功按钮</el-button>
  <el-button type="info" plain>信息按钮</el-button>
  <el-button type="warning" plain>警告按钮</el-button>
  <el-button type="danger" plain>危险按钮</el-button>
</div>
<hr />
```

### 文字按钮

说明：没有边框和背景色的按钮。

```vue
<div>文字按钮</div>
<div><el-button type="text" size="mini">文字按钮</el-button></div>
<hr/>
```

### 图标按钮

说明：带图标的按钮可增强辨识度（有文字）或节省空间（无文字）。设置`icon`属性即可，icon 的列表可以参考 Element 的 icon 组件，也可以设置在文字右边的 icon ，只要使用`i`标签即可，可以使用自定义图标。

```vue
<div>图标按钮</div>
<div>
  <el-button type="primary" icon="el-icon-edit"></el-button>
  <el-button type="primary" icon="el-icon-share"></el-button>
  <el-button type="primary" icon="el-icon-delete"></el-button>
  <el-button type="primary" icon="el-icon-search">搜索</el-button>
  <el-button type="primary">上传<i class="el-icon-upload el-icon--right"></i></el-button>
</div>
```

## 消息提示`Message`

>[Element-UI 官方参考消息提示 Message](https://element.eleme.cn/#/zh-CN/component/message)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/demo-element-ui-message)

点击按钮弹出成功、警告、消息、错误消息提示

```vue
<el-button :plain="true" @click="open2">成功</el-button>
<el-button :plain="true" @click="open3">警告</el-button>
<el-button :plain="true" @click="open1">消息</el-button>
<el-button :plain="true" @click="open4">错误</el-button>
```

弹出成功、警告、消息、错误消息提示代码如下：

```vue
<script>
export default {
  methods: {
    open1() {
      this.$message('这是一条消息提示');
    },
    open2() {
      this.$message({
        message: '恭喜你，这是一条成功消息',
        type: 'success'
      });
    },

    open3() {
      this.$message({
        message: '警告哦，这是一条警告消息',
        type: 'warning'
      });
    },

    open4() {
      this.$message.error('错了哦，这是一条错误消息');
    }
  }
}
</script>
```



## 弹框MessageBox

>[参考官方文档](https://element.eleme.cn/#/zh-CN/component/message-box)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-messagebox)

### 确认消息

说明：提示用户确认其已经触发的动作，并询问是否进行此操作时会用到此对话框。调用`$confirm`方法即可打开消息提示，它模拟了系统的 `confirm`。Message Box 组件也拥有极高的定制性，我们可以传入`options`作为第三个参数，它是一个字面量对象。`type`字段表明消息类型，可以为`success`，`error`，`info`和`warning`，无效的设置将会被忽略。注意，第二个参数`title`必须定义为`String`类型，如果是`Object`，会被理解为`options`。在这里我们用了 Promise 来处理后续响应。

```javascript
<template>
  <div id="app">
    <div>确认消息</div>
    <el-button type="primary" @click="handleClickOk">点击我</el-button>
    <hr />
  </div>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    // HelloWorld
  },
  methods: {
    handleClickOk() {
      this.$confirm('确定删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message(
          {
            message: '点击确定',
            type: 'success'
          }
        )
      }).catch(() => {
        this.$message(
          {
            message: '点击取消',
            type: 'info'
          }
        )
      });
    },
  }
}
</script>

<style>
#app {

}
</style>

```

### 提交内容Prompt

说明：当用户进行操作时会被触发，中断用户操作，提示用户进行输入的对话框。调用`$prompt`方法即可打开消息提示，它模拟了系统的 `prompt`。可以用`inputPattern`字段自己规定匹配模式，或者用`inputValidator`规定校验函数，可以返回`Boolean`或`String`，返回`false`或字符串时均表示校验未通过，同时返回的字符串相当于定义了`inputErrorMessage`字段。此外，可以用`inputPlaceholder`字段来定义输入框的占位符。

```vue
<template>
  <div id="app">
    <div>提交内容</div>
    <el-button type="primary" @click="handleClickPrompt">点击</el-button>
    <hr />
  </div>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    // HelloWorld
  },
  methods: {
    // 提交内容
    handleClickPrompt() {
      this.$prompt('请输入邮箱', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        this.$message({
          type: 'success',
          message: '你的邮箱是: ' + value
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        });
      })
    }
  }
}
</script>

<style>
#app {}
</style>

```

## 表单Form

>说明：示例演示典型表单（包括各种表单项，比如输入框、选择器、开关、单选框、多选框等。）、行内表单用法（当垂直方向空间受限且表单较简单时，可以在一行内放置表单。）。
>
>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-form)
>
>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/form)

## 表格Table

>说明：示例演示基础表格、树形数据表格的用法。
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-table
>
>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/table)

### 基础表格

说明：当`el-table`元素中注入`data`对象数组后，在`el-table-column`中用`prop`属性来对应对象中的键名即可填入数据，用`label`属性来定义表格的列名。可以使用`width`属性来定义列宽。

TableBasic.vue：

```vue
<template>
    <!-- 演示基础表格的用法 -->
    <el-table :data="tableData">
        <el-table-column prop="date" label="日期" width="180">

        </el-table-column>
        <el-table-column prop="name" label="姓名" width="180">

        </el-table-column>
        <el-table-column prop="address" label="地址">

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
    }
}
</script>

<style scoped></style>
```

### 树形数据表格

说明：支持树类型的数据的显示。当 row 中包含 `children` 字段时，被视为树形数据。渲染树形数据时，必须要指定 `row-key`。支持子节点数据异步加载。设置 Table 的 `lazy` 属性为 true 与加载函数 `load` 。通过指定 row 中的 `hasChildren` 字段来指定哪些行是包含子节点。`children` 与 `hasChildren` 都可以通过 `tree-props` 配置。

TableTreeDatum.vue：

```vue
<template>
    <el-table :data="tableData" row-key="id" default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
        <el-table-column prop="date" label="日期" sortable width="180">

        </el-table-column>
        <el-table-column prop="name" label="姓名" sortable width="180">

        </el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
    </el-table>
</template>

<script>
export default {
    data() {
        return {
            tableData: [{
                id: 1,
                date: '2016-05-02',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1518 弄'
            }, {
                id: 2,
                date: '2016-05-04',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1517 弄'
            }, {
                id: 3,
                date: '2016-05-01',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1519 弄',
                children: [{
                    id: 31,
                    date: '2016-05-01',
                    name: '王小虎',
                    address: '上海市普陀区金沙江路 1519 弄'
                }, {
                    id: 32,
                    date: '2016-05-01',
                    name: '王小虎',
                    address: '上海市普陀区金沙江路 1519 弄'
                }]
            }, {
                id: 4,
                date: '2016-05-03',
                name: '王小虎',
                address: '上海市普陀区金沙江路 1516 弄'
            }],
        }
    }
}
</script>

<style scoped></style>
```

### 自定义列模板

说明：自定义列的显示内容，可组合其他组件使用。通过 `Scoped slot` 可以获取到 row, column, $index 和 store（table 内部的状态管理）的数据。

TableCustomizeColumnSlot.vue：

```vue
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
```

### 多选

说明：选择多行数据时使用 Checkbox。实现多选非常简单: 手动添加一个`el-table-column`，设`type`属性为`selection`即可；默认情况下若内容过多会折行显示，若需要单行显示可以使用`show-overflow-tooltip`属性，它接受一个`Boolean`，为`true`时多余的内容会在 hover 时以 tooltip 的形式显示出来。

```vue
<template>
    <!-- 演示多选的用法 -->
    <el-table :data="tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55">

        </el-table-column>
        <el-table-column prop="date" label="日期" width="180">

        </el-table-column>
        <el-table-column prop="name" label="姓名" width="180">

        </el-table-column>
        <el-table-column prop="address" label="地址">

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
            }],
            multipleSelection: null,
        }
    },
    methods: {
        // 多选响应事件
        handleSelectionChange(val) {
            this.multipleSelection = val;
            alert(JSON.stringify(this.multipleSelection))
        }
    }
}
</script>

<style scoped></style>
```

## 加载Loading

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-loading
>
>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/loading)

### 区域加载

说明：在表格等容器中加载数据时显示。Element 提供了两种调用 Loading 的方法：指令和服务。对于自定义指令`v-loading`，只需要绑定`Boolean`即可。默认状况下，Loading 遮罩会插入到绑定元素的子节点，通过添加`body`修饰符，可以使遮罩插入至 DOM 中的 body 上。

```vue
<div>在表格等容器中加载数据时显示。</div>
<div>
  <el-table v-loading="loading" :data="tableData">
    <el-table-column prop="date" label="日期" width="180"></el-table-column>
    <el-table-column prop="name" label="姓名" width="180"></el-table-column>
    <el-table-column prop="address" label="地址"></el-table-column>
  </el-table>
  <el-button size="mini" type="primary" @click="handleClickTableLoading()">点击加载</el-button>
</div>

handleClickTableLoading() {
  this.loading = true
  setTimeout(() => {
    this.loading = false
  }, 2000)
},
```

### 整页加载

说明：页面数据加载时显示。当使用指令方式时，全屏遮罩需要添加`fullscreen`修饰符（遮罩会插入至 body 上），此时若需要锁定屏幕的滚动，可以使用`lock`修饰符；当使用服务方式时，遮罩默认即为全屏，无需额外设置。

```javascript
handleClickLoading() {
    const loading = this.$loading({
        lock: true,
        text: '加载中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
    });
    setTimeout(() => {
        loading.close()
    }, 2000);
}
```

## 对话框Dialog

>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/dialog)

### 自定义内容

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-dialog

说明：Dialog 组件的内容可以是任意的，甚至可以是表格或表单，下面是应用了 Element Table 和 Form 组件的两个样例。

Table：

```vue
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
        }
    }
}
</script>

<style scoped></style>
```

Form：

```vue
<template>
    <div>
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
```

## 树形控件Tree

>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/tree)
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-tree

### 基础用法

说明：基础的树形结构展示。

```vue
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

```

### 节点过滤

说明：通过关键字过滤树节点。在需要对节点进行过滤时，调用 Tree 实例的`filter`方法，参数为关键字。需要注意的是，此时需要设置`filter-node-method`，值为过滤函数。

TreeFiltering.vue：

```vue
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
```

## 布局Layout

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-layout
>
>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/layout)

说明：通过基础的 24 分栏，迅速简便地创建布局。

### 基础布局

说明：使用单一分栏创建基础的栅格布局。通过 row 和 col 组件，并通过 col 组件的 `span` 属性我们就可以自由地组合布局。

LayoutBasic.vue：

```vue
<template>
    <div>
        <el-row>
            <el-col :span="24">
                <div class="grid-content bg-purple-dark"></div>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <div class="grid-content bg-purple"></div>
            </el-col>
            <el-col :span="12">
                <div class="grid-content bg-purple-light"></div>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="8">
                <div class="grid-content bg-purple"></div>
            </el-col>
            <el-col :span="8">
                <div class="grid-content bg-purple-light"></div>
            </el-col>
            <el-col :span="8">
                <div class="grid-content bg-purple"></div>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="6">
                <div class="grid-content bg-purple"></div>
            </el-col>
            <el-col :span="6">
                <div class="grid-content bg-purple-light"></div>
            </el-col>
            <el-col :span="6">
                <div class="grid-content bg-purple"></div>
            </el-col>
            <el-col :span="6">
                <div class="grid-content bg-purple-light"></div>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="4">
                <div class="grid-content bg-purple"></div>
            </el-col>
            <el-col :span="4">
                <div class="grid-content bg-purple-light"></div>
            </el-col>
            <el-col :span="4">
                <div class="grid-content bg-purple"></div>
            </el-col>
            <el-col :span="4">
                <div class="grid-content bg-purple-light"></div>
            </el-col>
            <el-col :span="4">
                <div class="grid-content bg-purple"></div>
            </el-col>
            <el-col :span="4">
                <div class="grid-content bg-purple-light"></div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {

}
</script>

<style scoped>
.el-row {
    margin-bottom: 20px;

    &:last-child {
        margin-bottom: 0;
    }
}

.el-col {
    border-radius: 4px;
}

.bg-purple-dark {
    background: #99a9bf;
}

.bg-purple {
    background: #d3dce6;
}

.bg-purple-light {
    background: #e5e9f2;
}

.grid-content {
    border-radius: 4px;
    min-height: 36px;
}

.row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
}
</style>
```

### 分栏间隔

说明：分栏之间存在间隔。Row 组件 提供 `gutter` 属性来指定每一栏之间的间隔，默认间隔为 0。

LayoutGutter.vue：

```vue
<template>
    <!-- 分栏间隔 -->
    <div>
        <el-row :gutter="20">
            <el-col :span="6">
                <div class="grid-content bg-purple"></div>
            </el-col>
            <el-col :span="6">
                <div class="grid-content bg-purple"></div>
            </el-col>
            <el-col :span="6">
                <div class="grid-content bg-purple"></div>
            </el-col>
            <el-col :span="6">
                <div class="grid-content bg-purple"></div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {

}
</script>

<style scoped>
.el-row {
    margin-bottom: 20px;

    &:last-child {
        margin-bottom: 0;
    }
}

.el-col {
    border-radius: 4px;
}

.bg-purple-dark {
    background: #99a9bf;
}

.bg-purple {
    background: #d3dce6;
}

.bg-purple-light {
    background: #e5e9f2;
}

.grid-content {
    border-radius: 4px;
    min-height: 36px;
}

.row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
}
</style>
```

## 日期选择器DatePicker

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-datepicker
>
>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/date-picker)

### 选择日期

说明：以「日」为基本单位，基础的日期选择控件。基本单位由`type`属性指定。快捷选项需配置`picker-options`对象中的`shortcuts`，禁用日期通过 `disabledDate` 设置，传入函数。

```vue
<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <div>选择日期 - 默认</div>
    <div>
      <el-date-picker type="date" placeholder="选择日期" v-model="value1">
      </el-date-picker>
    </div>
    <hr />

    <div>选择日期 - 带快捷选项</div>
    <div>
      <el-date-picker type="date" v-model="value2" placeholder="选择日期" :picker-options="pickerOptions">
      </el-date-picker>
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
      value1: '',
      value2: '',
      pickerOptions: {
        disableDate(time) {
          return time.getTime() > Date.now()
        },
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date())
          }
        }, {
          text: '昨天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24);
            picker.$emit('pick', date);
          }
        }, {
          text: '一周前',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', date);
          }
        }]
      },
    }
  }
}
</script>

<style>
#app {}
</style>

```

### 选择日期范围

>说明：可在一个选择器中便捷地选择一个时间范围。在选择日期范围时，默认情况下左右面板会联动。如果希望两个面板各自独立切换当前月份，可以使用`unlink-panels`属性解除联动。

```vue
<template>
  <div id="app">
    <div>选择日期范围 - 默认</div>
    <div>
      <el-date-picker v-model="value3" type="daterange" range-separator="至" start-placeholder="开始日期"
        end-placeholder="结束日期"></el-date-picker>
    </div>
    <hr />

    <div>选择日期范围 - 带快捷选项</div>
    <div>
      <el-date-picker v-model="value4" type="daterange" unlink-panels range-separator="至" start-placeholder="开始日期"
        end-placeholder="结束日期" :picker-options="pickerOptions2">

      </el-date-picker>
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
      value3: '',
      value4: '',
      pickerOptions2: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
    }
  }
}
</script>

<style>
#app {}
</style>

```

## 滚动条`el-scrollbar`

`el-scrollbar` 是 Element UI 中的一个**自定义滚动条组件**，它用来替代浏览器默认的滚动条，提供更加美观和统一的视觉风格。

### 主要特点

#### 1. **视觉统一**
- 与 Element UI 的整体设计语言保持一致
- 比浏览器原生滚动条更细、更美观
- 支持主题定制

#### 2. **跨浏览器一致性**
- 在不同浏览器中显示效果一致
- 避免了原生滚动条在不同浏览器中的样式差异

### 基本用法

```vue
<template>
  <el-scrollbar height="400px">
    <div style="padding: 20px;">
      <p v-for="i in 50" :key="i">这是第 {{ i }} 行内容</p>
    </div>
  </el-scrollbar>
</template>

<script>
import { ElScrollbar } from 'element-ui'

export default {
  components: {
    ElScrollbar
  }
}
</script>
```

### 常用属性

| 属性         | 说明               | 类型          | 默认值  |
| ------------ | ------------------ | ------------- | ------- |
| `height`     | 设置滚动区域高度   | string        | -       |
| `max-height` | 设置最大高度       | string        | -       |
| `native`     | 是否使用原生滚动条 | boolean       | `false` |
| `wrap-style` | 容器样式           | string/object | -       |
| `wrap-class` | 容器类名           | string        | -       |
| `view-class` | 内容视图类名       | string        | -       |
| `view-style` | 内容视图样式       | string/object | -       |

### 实际应用场景

#### 1. **侧边栏菜单**
```vue
<template>
  <el-scrollbar class="sidebar-scrollbar">
    <el-menu :default-active="activeIndex">
      <el-menu-item index="1">首页</el-menu-item>
      <el-menu-item index="2">用户管理</el-menu-item>
      <!-- 更多菜单项 -->
    </el-menu>
  </el-scrollbar>
</template>

<style>
.sidebar-scrollbar {
  height: calc(100vh - 60px);
}
</style>
```

#### 2. **表格数据区域**
```vue
<template>
  <div class="table-container">
    <el-table :data="tableData">
      <!-- 表格列定义 -->
    </el-table>
    
    <el-scrollbar max-height="300px">
      <div v-for="item in largeData" :key="item.id">
        {{ item.content }}
      </div>
    </el-scrollbar>
  </div>
</template>
```

#### 3. **聊天消息区域**
```vue
<template>
  <el-scrollbar 
    ref="scrollbar" 
    height="400px" 
    @scroll="handleScroll"
  >
    <div v-for="msg in messages" :key="msg.id" class="message">
      {{ msg.content }}
    </div>
  </el-scrollbar>
</template>

<script>
export default {
  methods: {
    // 滚动到底部
    scrollToBottom() {
      this.$nextTick(() => {
        const scrollbar = this.$refs.scrollbar
        scrollbar.wrap.scrollTop = scrollbar.wrap.scrollHeight
      })
    },
    
    handleScroll({ scrollTop }) {
      // 处理滚动事件
    }
  }
}
</script>
```

### 样式定制

```vue
<template>
  <el-scrollbar
    class="custom-scrollbar"
    wrap-class="custom-wrap"
    view-class="custom-view"
  >
    <!-- 内容 -->
  </el-scrollbar>
</template>

<style>
/* 自定义滚动条样式 */
.custom-scrollbar .el-scrollbar__bar.is-vertical {
  width: 6px;
}

.custom-scrollbar .el-scrollbar__thumb {
  background-color: #409EFF;
  border-radius: 3px;
}

.custom-wrap {
  padding: 10px;
}

.custom-view {
  min-height: 100%;
}
</style>
```

### 与原生滚动条的区别

| 特性   | `el-scrollbar`         | 原生滚动条       |
| ------ | ---------------------- | ---------------- |
| 外观   | 与 Element UI 风格统一 | 浏览器默认样式   |
| 一致性 | 跨浏览器表现一致       | 不同浏览器差异大 |
| 定制性 | 高度可定制             | 有限定制         |
| 性能   | 稍低（需要 JS 计算）   | 更高             |
| 移动端 | 支持触控               | 原生触控支持更好 |

### 注意事项

1. **必须设置高度**：需要明确指定 `height` 或 `max-height`
2. **性能考虑**：对于超长列表，建议使用虚拟滚动
3. **移动端适配**：在移动设备上可能不如原生滚动流畅

`el-scrollbar` 是 Element UI 生态中一个很实用的组件，特别适合需要统一视觉风格的内部系统或管理后台使用。



### 示例

>[front-end/demo-element-ui/element-ui-scrollbar · dexterleslie/demonstration - 码云 - 开源中国](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-scrollbar)

`App.vue`

```vue
<template>
  <div id="app">
    <!-- 垂直滚动 -->
    <el-scrollbar style="border: 1px solid #e0e0e0;">
      <div v-for="msg in messages" :key="msg.id" class="message">
        {{ msg.content }}
      </div>
    </el-scrollbar>

    <!-- 水平滚动 -->
    <el-scrollbar style="border: 1px solid #e0e0e0;">
      <!-- 强制div不换行，否则el-scrollbar不会水平滚动 -->
      <div style="white-space: nowrap;">
        {{ longMessage }}
      </div>
    </el-scrollbar>
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    HelloWorld,
  },
  data() {
    return {
      messages: [],
      longMessage: "",
    }
  },
  mounted() {
    for (var i = 0; i < 100; i++) {
      this.messages.push({ id: i, content: `内容${i}` })
    }

    for (var i = 0; i < 10000; i++) {
      this.longMessage = this.longMessage + i + "-";
    }
  },
  methods: {
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
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

#app > div {
  width: 100%;
}

#app > div:nth-child(1) {
  flex: 1;
}
#app > div:nth-child(2) {
  flex: 1;
}
</style>

```



## 菜单导航

>[front-end/demo-element-ui/element-ui-menu · dexterleslie/demonstration - 码云 - 开源中国](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-menu)
>
>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/menu)



## Cascader级联选择器

>说明：当一个数据集合有清晰的层级结构时，可通过级联选择器逐级查看并选择。
>
>官方参考链接：https://element.eleme.cn/#/zh-CN/component/cascader
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-cascader
## `Vue2`集成`Element-UI`

>请参考本站 <a href="/vue/集成element-ui.html" target="_blank">链接</a>



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



## 加载中提示

>[参考官方文档](https://element.eleme.cn/#/zh-CN/component/loading)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-loading)

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



## 确认弹框

>[参考官方文档](https://element.eleme.cn/#/zh-CN/component/message-box)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/demo-messagebox)

```javascript
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
}
```



## 表单

>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-form)
>
>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/form)



## 布局`Layout`

>说明：通过基础的 24 分栏，迅速简便地创建布局。
>
>详细用法请参考本站[示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-element-ui/element-ui-layout)
>
>[组件 | Element](https://element.eleme.cn/#/zh-CN/component/layout)



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


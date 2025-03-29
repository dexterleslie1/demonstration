# Vue



## 计算属性

>[Vue 官方文档计算属性参考](https://cn.vuejs.org/guide/essentials/computed)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/vue2-computed)

```html
<hr>
<div>演示计算属性的用法</div>
<div>a+b={{ funAPlusB }}</div>
<div>a-b={{ funAMinusB }}</div>
```

```html

<script>
export default {
  data() {
    return {
      a: 11,
      b: 2
    }
  },
  computed: {
    funAPlusB() {
      return this.a + this.b
    },
    funAMinusB() {
      return this.a - this.b
    }
  }
}
</script>
```



## VSCode 开发环境配置



### 安装 Vue 插件

在 VSCode Extensions 中输入 `vue` 关键词进行搜索，安装如下插件：

- Vue VSCode Snippets，作者：sarah.drasner，介绍：Snippets that will supercharge your Vue workflow
- Vue - Official，作者：vuejs.org，介绍：Language Support for Vue



### Vue VSCode Snippets 插件

>[参考链接](https://marketplace.visualstudio.com/items?itemName=sdras.vue-vscode-snippets)



#### Vue 片段

>这些代码片段旨在为您的单文件组件（SFC）提供基础支架。SFC 代表 single file components。

| Snippet     | Purpose            |
| ----------- | ------------------ |
| `vbase-css` | 带 CSS 的 SFC 基础 |



## 组件化开发/自定义组件



### 本站示例

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/vue3-component)

安装依赖

```bash
npm install
```

启动示例

```bash
npm run serve
```

编译发布示例

```bash
npm run build
```



### 自定义组件

下面演示自定义 ComponentNavigator 组件

在 src/components/ 中新建文件 ComponentNavigator.vue，内容如下：

```vue
<template>
  <div>
    这是ComponentConent
  </div>
</template>

<script>
export default {
  name: "ComponentNavigator",
  data() {
    return {
      internalVar: false,
    }
  },
  methods: {
    toggleMethod() {
      this.internalVar = !this.internalVar
      console.log(`internalVar=${this.internalVar}`)
    }
  }
}
</script>

<style scoped>

</style>
```

引用自定义组件，在 src/App.vue 中引用自定义组件，代码如下：

```vue
<template>
  <!-- 布局 -->
  <div class="container">
    <button @click="handleCallComponentMethod()">测试1</button>
    <div class="header">
      <ComponentHeader></ComponentHeader>
    </div>
    <div class="body">
      <div class="body-navigator">
        <!-- 引用自定义组件 -->
        <ComponentNavigator ref="myNavigator"></ComponentNavigator>
      </div>
      <div class="body-content">
        <ComponentContent></ComponentContent>
      </div>
    </div>
    <div class="footer">
      <ComponentFooter></ComponentFooter>
    </div>
  </div>
</template>

<script>
// 导入自定义组件
import ComponentNavigator from "@/components/ComponentNavigator";

export default {
  name: 'App',
  components: {
    // 注册自定义组件
    ComponentNavigator,
  },
}
</script>

<style>
</style>

```



### 组件化开发

下面演示页面的主体布局组件化的思想

src/components/ComponentContent.vue 代码如下：

```vue
<template>
  <div>
    这是ComponentContent
  </div>
</template>

<script>
export default {
  name: "ComponentContent"
}
</script>

<style scoped>

</style>
```

src/components/ComponentFooter.vue 代码如下：

```vue
<template>
  <div>
    这是ComponentFooter
  </div>
</template>

<script>
export default {
  name: "ComponentFooter"
}
</script>

<style scoped>

</style>
```

src/components/ComponentHeader.vue 代码如下：

```vue
<template>
  <div>
    这是ComponentHeader
  </div>
</template>

<script>
export default {
  name: "ComponentHeader"
}
</script>

<style scoped>

</style>
```

src/components/ComponentNavigator.vue 代码如下：

```vue
<template>
  <div>
    这是ComponentConent
  </div>
</template>

<script>
export default {
  name: "ComponentNavigator",
  data() {
    return {
      internalVar: false,
    }
  },
  methods: {
    toggleMethod() {
      this.internalVar = !this.internalVar
      console.log(`internalVar=${this.internalVar}`)
    }
  }
}
</script>

<style scoped>

</style>
```

src/App.vue 引用各个组件，代码如下：

```vue
<template>
  <!-- 布局 -->
  <div class="container">
    <button @click="handleCallComponentMethod()">测试1</button>
    <div class="header">
      <ComponentHeader></ComponentHeader>
    </div>
    <div class="body">
      <div class="body-navigator">
        <!-- 引用自定义组件 -->
        <ComponentNavigator ref="myNavigator"></ComponentNavigator>
      </div>
      <div class="body-content">
        <ComponentContent></ComponentContent>
      </div>
    </div>
    <div class="footer">
      <ComponentFooter></ComponentFooter>
    </div>
  </div>
</template>

<script>
import ComponentHeader from "@/components/ComponentHeader";
// 导入自定义组件
import ComponentNavigator from "@/components/ComponentNavigator";
import ComponentContent from "@/components/ComponentContent";
import ComponentFooter from "@/components/ComponentFooter";

export default {
  name: 'App',
  components: {
    ComponentHeader,
    // 注册自定义组件
    ComponentNavigator,
    ComponentContent,
    ComponentFooter
  },
  methods: {
    handleCallComponentMethod() {
      this.$refs.myNavigator.toggleMethod()
    }
  }
}
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
}
.container {
  width: 600px;
  height: 600px;
  background-color: #888888;
}
.header {
  width: 100%;
  height: 100px;
  background-color: #42b983;
}
.body {
  width: 100%;
  height: 300px;
  background-color: #cccccc;
}
.body-navigator {
  float: left;
  width: 25%;
  height: 100%;
  background-color: aquamarine;
}
.body-content {
  float: right;
  width: 75%;
  height: 100%;
  background-color: beige;
}
.footer {
  width: 100%;
  height: 200px;
  background-color: #42b983;
}
</style>

```



## 数据更新但视图不更新问题

>[参考链接](https://stackoverflow.com/questions/44800470/vue-js-updated-array-item-value-doesnt-update-in-page)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/vue2-data-changed-but-view-not-update-problem)

下面情况存在问题：

- 动态为对象添加属性不能自动更新到视图中
- 动态删除对象属性不能自动更新到视图中
- 使用索引修改数组值不能自动更新到视图中，push 和 pop 方法正常



`动态为对象添加属性不能自动更新到视图中` 解决方案如下：

- 解决方案1、使用 this.$forceUpdate()
- 解决方案2、使用 this.$set()，推荐使用此解决方案。

`动态删除对象属性不能自动更新到视图中` 解决方案如下：

- 解决方案1、使用 this.$forceUpdate()
- 解决方案2、使用 this.$delete()

`使用索引修改数组值不能自动更新到视图中，push 和 pop 方法正常` 解决方案如下：

- 解决方案1、使用 this.$forceUpdate()
- 解决方案2、使用 this.$set()



上面三种情况的示例代码如下：

```vue
<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->
    <hr>
    <div>动态为对象新增属性</div>
    <div>
      <button @click="handleClick1">点击我</button>
      <div v-for="(val, key, index) in obj1">
        index={{ index }},{{ key }}={{ val }}
      </div>
    </div>

    <hr>
    <div>动态删除对象属性</div>
    <div>
      <button @click="handleClick2">点击我</button>
      <div v-for="(val, key, index) in obj1">
        index={{ index }},{{ key }}={{ val }}
      </div>
    </div>

    <hr>
    <div>使用索引修改数组值</div>
    <div>
      <button @click="handleClick3">点击我</button>
      <div v-for="(item, index) in arr1">
        index={{ index }},{{ item }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      obj1: {
        key1: 'val1',
        key2: 'val2'
      },
      arr1: [1, 2, 3]
    }
  },
  methods: {
    handleClick1() {
      // 动态为对象添加属性不能自动更新到视图中
      this.obj1.key3 = '88888'

      // 解决方案1、使用this.$forceUpdate()
      // this.$forceUpdate()

      // 解决方案2、使用this.$set()
      // this.$set(this.obj1, 'key3', '88888')
    },

    handleClick2() {
      // 动态删除对象属性不能自动更新到视图中
      delete this.obj1.key2

      // 解决方案1、使用this.$forceUpdate()
      // this.$forceUpdate()

      // 解决方案2、使用this.$delete()
      // this.$delete(this.obj1, 'key2')
    },

    handleClick3() {
      // 使用索引修改数组值不能自动更新到视图中，push 和 pop 方法正常
      // this.arr1[1] = 888
      // this.arr1.push("999")
      // this.arr1.pop()

      // 解决方案1、使用this.$forceUpdate()
      this.$forceUpdate()

      // 解决方案2、使用this.$set()
      // this.$set(this.arr1, 1, '888')
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

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>

```



运行本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/vue2-data-changed-but-view-not-update-problem)

- 安装依赖

  ```bash
  npm install
  ```

- 运行示例

  ```bash
  npm run serve
  ```












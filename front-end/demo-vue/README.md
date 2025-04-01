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



## Vue eslint 配置

### 取消 no-unused-vars 警告

>[参考链接](https://stackoverflow.com/questions/61874994/vue-disable-no-unused-vars-error-the-simplest-fix)

在 package.json eslintConfig 中添加如下配置：

```javascript
"eslintConfig": {
    "rules": {
      "no-unused-vars": "off"
    }
  },
```



## Vue uuid 库

[npm 库中的 uuid 库](https://www.npmjs.com/package/uuid)

Vue 项目安装 uuid 库

```bash
npm install uuid
```

调用 uuid 库生成 uuid

```javascript
import { v4 as uuidv4 } from 'uuid';
uuidv4(); // ⇨ '9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d'
```



## Vue lodash 库

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/demo-vue-lodash)

### 添加依赖

```bash
npm install lodash
```



### 删除指定索引的元素

```vue
<div>
    删除指定索引的元素
</div>
<div>
    arrayInt: {{ this.arrayInt }}
    <input type="button" value="点击我" @click="handleClickRemoveSpecifyIndex" />
</div>
<hr />
```

```javascript
<script>
// import HelloWorld from './components/HelloWorld.vue'
import _ from 'lodash'

export default {
  name: 'App',
  components: {
    // HelloWorld
  },
  data() {
    return {
      arrayInt: [1, 2, 3],
    }
  },
  methods: {
    handleClickRemoveSpecifyIndex() {
      // 删除索引为 1 的元素
      _.remove(this.arrayInt, function (value, index, array) {
        return index == 1
      })
      // 重新给 arrayInt 赋值，否则视图不会更新
      this.arrayInt = [...this.arrayInt]
    },
  }
}
</script>
```



### indexOf 函数获取指定 primitive 元素索引

> 注意：indexOf不能用于对象类型，需要使用findIndex方法

```vue
<div>
    indexOf 函数获取指定 primitive 元素索引，注意：indexOf不能用于对象类型，需要使用findIndex方法
</div>
<div>
    indexOfElement: {{ this.indexOfElement }}
    <input type="button" value="点击我" @click="handleClickIndexOf" />
</div>
<hr />
```

```javascript
<script>
// import HelloWorld from './components/HelloWorld.vue'
import _ from 'lodash'

export default {
  name: 'App',
  components: {
    // HelloWorld
  },
  data() {
    return {
      indexOfElement: NaN,
    }
  },
  methods: {
    handleClickIndexOf() {
      this.indexOfElement = _.indexOf(this.arrayInt, 3)
    },
  }
}
</script>
```



### findIndex 函数获取指定对象元素的索引

```vue
<div>
    findIndex 函数获取指定对象元素的索引
</div>
<div>
    findIndexOfObject: {{ this.findIndexOfObject }}
    <input type="button" value="点击我" @click="handleClickFindIndexOfObject" />
</div>
<hr />
```

```javascript
<script>
// import HelloWorld from './components/HelloWorld.vue'
import _ from 'lodash'

export default {
  name: 'App',
  components: {
    // HelloWorld
  },
  data() {
    return {
      findIndexOfObject: NaN,
    }
  },
  methods: {
    handleClickFindIndexOfObject() {
      this.findIndexOfObject = _.findIndex(this.arrayObject, function (o) {
        return o.userId == 2
      })
    },
  }
}
</script>
```



### 在指定 index 位置插入元素

```vue
<div>
    在指定 index 位置插入元素
</div>
<div>
    arrayInt: {{ this.arrayInt }}
    <input type="button" value="点击我" @click="handleClickInsertElementBySpecifyingIndex" />
</div>
<hr />
```

```javascript
<script>
// import HelloWorld from './components/HelloWorld.vue'
import _ from 'lodash'

export default {
  name: 'App',
  components: {
    // HelloWorld
  },
  data() {
    return {
      arrayInt: [1, 2, 3],
    }
  },
  methods: {
    handleClickInsertElementBySpecifyingIndex() {
      // 第一个参数 1 指定了开始修改数组的位置索引。索引是从 0 开始的，所以 1 指的是数组的第二个位置。
      // 第二个参数 0 指定了要删除的元素数量。在这个例子中，0 意味着不删除任何元素。
      // 从第三个参数开始，后面的参数（在这个例子中是 2）指定了要添加到数组中的新元素。这些新元素会被添加到由第一个参数指定的位置之后。
      this.arrayInt.splice(1, 0, 5)
    }
  }
}
</script>
```



## 事件



### 事件修饰符

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/vue2-event-modifiers)
>
>[参考链接](https://blog.csdn.net/I_r_o_n_M_a_n/article/details/120251951)

#### .prevent 阻止默认事件

>.prevent 阻止默认事件，例如：a 标签的跳转行为和 form 提交行为。

```vue
<hr>
<div>.prevent阻止默认事件，例如：a标签的跳转行为和form提交行为</div>
<div>
    <a href="http://www.atguigu.com" @click.prevent="handleClick">点我提示信息</a>
</div>
```

```javascript
<script>
export default {
  methods: {
    handleClick() {
      alert('handleClick触发')
    },
  }
}
</script>
```



#### .stop 阻止事件冒泡

```vue
<hr>
<div>.stop阻止事件冒泡</div>
<div>
    <!-- 阻止事件冒泡（常用） -->
    <div @click="handleClick">
        <button @click.stop="handleClick">点我提示信息</button>
    </div>
</div>
```

```javascript
<script>
export default {
  methods: {
    handleClick() {
      alert('handleClick触发')
    },
  }
}
</script>
```



#### .once 事件只触发一次

```vue
<hr>
<div>.once事件只触发一次</div>
<div>
    <button @click.once="handleClick">点我提示信息</button>
</div>
```

```javascript
<script>
export default {
  methods: {
    handleClick() {
      alert('handleClick触发')
    },
  }
}
</script>
```



#### .self 只有 event.target 是当前操作的元素时才触发事件

```vue
<hr>
<div>.self只有event.target是当前操作的元素时才触发事件</div>
<div>
    <div @click.self="handleClickSelf" style="background-color: antiquewhite;">
        <button @click="handleClick">点我提示信息</button>
    </div>
</div>
```

```javascript
<script>
export default {
  methods: {
    handleClick() {
      alert('handleClick触发')
    },
    handleClickSelf() {
      alert('handleClickSelf触发')
    },
  }
}
</script>
```



#### .capture 调整事件冒泡的触发顺序

> 冒泡是从里往外冒，捕获是从外往里捕。当捕获存在时，先从外到里的捕获，剩下的从里到外的冒泡输出。

```vue
<!-- https://blog.csdn.net/catascdd/article/details/108273931 -->
<hr>
<div>.capture调整事件冒泡的触发顺序，冒泡是从里往外冒，捕获是从外往里捕。当捕获存在时，先从外到里的捕获，剩下的从里到外的冒泡输出。</div>
<div>
    <div @click="handleClickCapture('最外层')" style="width:35px;height:35px;background-color:brown;">
        <div @click.capture="handleClickCapture('抓到了爷爷')" style="width:30px;height:30px;background-color:blueviolet;">
            <div @click="handleClickCapture('抓到了父亲')" style="width:25px;height:25px;background-color:aqua;">
                <div @click="handleClickCapture('抓到了儿子')" style="width:20px;height:20px;background-color: antiquewhite;"></div>
            </div>
        </div>
    </div>
</div>
```

```javascript
<script>
export default {
  methods: {
    handleClickCapture(flag) {
      console.log(flag)
    }
  }
}
</script>
```



### 点击

#### 传递自定义参数

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/demo-vue2-event-click)

```vue
<div>按钮点击事件传递自定义参数</div>
<input type="button" value="测试" @click="handleClickPassingCustomizeParameter('Hello world!')"/>
<hr/>
```

```javascript
methods: {
    handleClickPassingCustomizeParameter(param1) {
        alert(`自定义参数：${param1}`)
    }
}
```



### 拖拽

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/demo-vue2-event-drag-and-drop)

```vue
<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <div style="display:flex;flex-direction:column;justify-content:center;align-items:center;">
      <div class="drop-zone" @dragenter.prevent="isDragging = true" @dragover.prevent
        @dragleave.prevent="isDragging = false" @drop.prevent="handleDrop" :class="{ 'dragging': isDragging }"
        @click="$refs.fileInput.click()">
        拖拽文件到这里或者点击选择文件
        <input type="file" name="files" ref="fileInput" @change="handleChangeFile" multiple style="display:none;" />
      </div>

      <div style="margin-top:20px;">
        <div>
          已经选择文件列表：
        </div>
        <div v-if="fileList.length == 0" style="color:red;font-size:16px;padding-top:10px;">无数据</div>
        <div style="display:flex;flex-direction:column;align-items:start;padding-left:10px;padding-top:10px;">
          <div v-for="item in this.fileList" style="padding:2px 0px;">
            <span>•</span><span style="margin-left:10px;font-weight:bold;">{{ item.name }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    // HelloWorld
  },
  data() {
    return {
      fileList: [],
      isDragging: false,
    }
  },
  mounted() {
    // 禁止文件拖拽自动下载或者打开的默认行为，需要 dragover 和 drop 同时 prevent 才生效
    document.addEventListener('dragover', this.handleDragOverPrevent);
    document.addEventListener('drop', this.handleDropPrevent);
  },
  beforeDestroy() {
    document.removeEventListener('dragover', this.handleDragOverPrevent);
    document.removeEventListener('drop', this.handleDropPrevent);
  },
  methods: {
    handleDrop(event) {
      this.isDragging = false
      let files = event.dataTransfer.files
      if (files && files.length > 0) {
        for (let i = 0; i < files.length; i++) {
          this.fileList.push(files[i])
        }
      }
    },
    handleChangeFile(event) {
      this.isDragging = false
      let files = event.target.files
      if (files && files.length > 0) {
        for (let i = 0; i < files.length; i++) {
          this.fileList.push(files[i])
        }
      }
      this.$refs.fileInput.value = null
    },
    handleDragOverPrevent(event) {
      event.preventDefault();
    },
    handleDropPrevent(event) {
      event.preventDefault();
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
  margin-top: 60px;
}

.drop-zone {
  border-width: 2px;
  border-style: dashed;
  border-color: #ccc;
  border-radius: 10px;
  padding: 50px 0px;
  width: 80%;
}

.drop-zone.dragging {
  border-color: black;
  background-color: rgb(228, 228, 228);
}
</style>

```



## input file 控件自定义订制

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/demo-vue2-input-file-customization)

代码如下：

```vue
<template>
  <div id="app">
    <div>演示 input file 控件的自定义开发</div>
    <div>
      <!-- multiple 表示支持文件多选 -->
      <input ref="fileInput" type="file" name="files" multiple @change="handleChangeFileInput" style="display:none;">
      <input type="button" value="选择文件 ..." @click="() => { this.$refs.fileInput.click() }" />
    </div>
    <div>
      <div>选择文件列表如下：</div>
      <div v-if="!this.fileList || this.fileList.length==0">没有文件</div>
      <div v-for="item in this.fileList" :key="item.id">
        <input type="button" value="-" @click="handleClickRemoveFile(item)" style="width:25px;height:25px;" />
        &nbsp;&nbsp;{{ item.file.name }}
      </div>
    </div>
  </div>
</template>

<script>
import { v4 as uuidv4 } from 'uuid'
import _ from 'lodash'

export default {
  name: 'App',
  components: {
  },
  data() {
    return {
      // 自定义文件列表用于存放准备上传的文件对象
      fileList: []
    }
  },
  methods: {
    handleChangeFileInput(event) {
      let files = event.target.files
      if (files && files.length > 0) {
        for (let i = 0; i < files.length; i++) {
          // 使用 id 是为了在 v-for 中拥有唯一的 key，在删除数据时候就不会错乱
          let fileEntry = { id: uuidv4(), file: files[i] }
          this.fileList.push(fileEntry)
        }
      }

      // 清空 input file 的选择记录
      this.$refs.fileInput.value = null
    },
    handleClickRemoveFile(item) {
      _.remove(this.fileList, function (value, index, array) {
        return value.id == item.id
      })
      // 重新给赋值，否则视图不会更新
      this.fileList = [...this.fileList]
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
  margin-top: 60px;
}
</style>

```



## devServer 配置

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/front-end/demo-vue/demo-dev-server)
>
>[参考链接](https://blog.csdn.net/cc_King/article/details/125777373)
>
>[参考链接](https://blog.csdn.net/qq_45973155/article/details/135286089)



### 介绍

`devServer` 是 **前端开发服务器**，常见于 Vue.js、React 等前端框架的开发环境配置中。它的核心作用是 **为开发者提供一个本地开发环境**，支持静态文件服务、API 代理、模块热替换（HMR）等功能，提升开发效率。

**`devServer` 的主要功能**

1. 静态文件服务：
   - 加载项目中的 HTML、CSS、JavaScript 等静态资源，无需手动部署到生产服务器。
2. 模块热替换（HMR - Hot Module Replacement）：
   - 修改代码后，自动更新浏览器中的页面内容，无需手动刷新。
3. API 代理：
   - 通过配置 `proxy`，将前端请求转发到后端服务器，解决跨域问题，简化开发调试。
4. 其他实用功能：
   - **HTTPS 支持**：启用加密连接。
   - **端口配置**：自定义服务器端口（如 `port: 3000`）。
   - **历史模式路由**：支持前端路由（如 `historyApiFallback: true`）。
   - **模拟生产环境**：配置与生产环境一致的路径和规则。

**常见配置示例（以 Vue.js 为例）**

```javascript
module.exports = {
  devServer: {
    port: 3000, // 服务器端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 代理到后端服务器
        changeOrigin: true
      }
    },
    historyApiFallback: true, // 支持前端路由
    hot: true // 启用模块热替换
  }
}
```

**使用场景**

- **开发阶段调试**：在本地运行前端项目，实时预览修改效果。
- **跨域问题解决**：通过代理配置，避免浏览器因 CORS 策略拦截请求。
- **模拟生产环境**：配置与生产环境一致的路径、端口等，提前发现问题。

**总结**

`devServer` 是前端开发中的“全能助手”，通过提供静态文件服务、API 代理、热更新等功能，显著提升了开发效率。它是开发阶段不可或缺的工具，但在项目部署到生产环境时，需使用生产服务器（如 Nginx、Apache）替代。



### 基本配置

通过 devServer 配置，vue.config.js 配置如下：

```javascript
const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    // 自定义 30000 端口
    // https://stackoverflow.com/questions/47219819/how-to-change-port-number-in-vue-cli-project
    port: 30000,
    proxy: {
      // /api 开头的请求转发到 http://localhost:8080 服务器
      '/api': {
        target: 'http://localhost:8080', // 后端服务器地址
        // changeOrigin: true, // 是否改变Origin头信息
        // pathRewrite: {
        //   '^/api': '' // 将/api前缀重写为空字符串
        // }
      }
    }
  }
})
```



### 监听端口配置

通过 port 配置监听端口，vue.config.js 配置如下：

```javascript
const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    // 自定义 30000 端口
    // https://stackoverflow.com/questions/47219819/how-to-change-port-number-in-vue-cli-project
    port: 30000,
    proxy: {
      // /api 开头的请求转发到 http://localhost:8080 服务器
      '/api': {
        target: 'http://localhost:8080', // 后端服务器地址
        // changeOrigin: true, // 是否改变Origin头信息
        // pathRewrite: {
        //   '^/api': '' // 将/api前缀重写为空字符串
        // }
      }
    }
  }
})
```



## 选项式和组合式 API

在 Vue.js 中，组合式 API（Composition API）是 **Vue 3** 引入的一套新的逻辑复用机制，它对应的传统 API 被称为 **选项式 API（Options API）**。两者是 Vue 中组织代码逻辑的两种不同范式：

### 1. **组合式 API（Composition API）**

- **核心特性**：通过 `setup()` 函数、`ref`、`reactive`、`computed`、`watch` 等函数式工具组织代码。
- 优势：
  - 逻辑复用更灵活（通过自定义 Hook，如 `useMouse`、`useFetch`）。
  - 更好的类型推断（配合 TypeScript）。
  - 代码组织更直观（按功能拆分而非选项分类）。
- **适用场景**：复杂组件、需要逻辑复用的场景，或 Vue 3 项目。

### 2. **选项式 API（Options API）**

- **核心特性**：通过 `data`、`methods`、`computed`、`watch` 等选项组织代码。
- 特点：
  - 代码按功能（如数据、方法、生命周期）分类，适合简单场景。
  - 逻辑复用依赖 Mixins（可能引发命名冲突）。
- **适用场景**：简单组件、Vue 2 项目，或需要快速开发时。

### 对比示例

- **选项式 API**：

  ```javascript
  export default {
    data() {
      return { count: 0 };
    },
    methods: {
      increment() { this.count++; }
    },
    watch: {
      count(newVal) { console.log(newVal); }
    }
  };
  ```

- **组合式 API**：

  ```javascript
  import { ref, watch } from 'vue';
  export default {
    setup() {
      const count = ref(0);
      const increment = () => { count.value++; };
      watch(count, (newVal) => console.log(newVal));
      return { count, increment };
    }
  };
  ```

### 总结

- **组合式 API** 是 Vue 3 推荐的新范式，尤其适合中大型项目。
- **选项式 API** 仍被支持，适合简单组件或 Vue 2 迁移项目。
- 两者可共存于同一项目中，但推荐新项目优先使用组合式 API。

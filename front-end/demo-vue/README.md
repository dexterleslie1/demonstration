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



## 创建并发布自定义组件

### 基于 Vue2

#### 使用 Vue CLI

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-vue/demo-vue2-cli-my-component-lib)

使用 Vue CLI 创建项目

```bash
$ vue demo-vue2-cli-my-component-lib
Vue CLI v5.0.8
? Please pick a preset: Manually select features
? Check the features needed for your project: Babel
? Choose a version of Vue.js that you want to start the project with 2.x
? Where do you prefer placing config for Babel, ESLint, etc.? In package.json
? Save this as a preset for future projects? No
```

配置 vue.config.js 内容如下：

```javascript
const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // configureWebpack 用于直接对内部的 Webpack 配置对象进行扩展或覆盖。
  // 在这里，通过 configureWebpack 添加了 output 和 externals 配置，主要目的是修改打包输出的格式和指定外部依赖。
  configureWebpack: {
    // 这部分配置主要用于打包一个库（Library），指定打包后的文件如何暴露给外部使用。
    // 如果你的 Vue 项目是为了开发一个组件库（而不仅仅是一个独立应用），这些配置非常关键，因为它们决定了你的组件库如何被其他项目引入和使用。
    output: {
      // 指定库的导出方式是 default，即暴露组件的默认导出。
      // 如果你的组件库默认导出的是一个 Vue 组件（如 export default {}），需要设置为 default。
      libraryExport: 'default',
      // 指定库的打包格式为 UMD（Universal Module Definition）。
      // UMD 是一种通用模块定义规范，可兼容多种加载方式（如 CommonJS、AMD 和全局变量）。
      // 这种格式适合发布到 npm，同时支持在浏览器环境中通过 <script> 标签直接引入。
      libraryTarget: 'umd',
      // 指定库的全局变量名称为 MyComponentLib。
      // 如果库通过 <script> 的方式引入，MyComponent 会作为全局变量暴露。
      library: 'MyComponentLib'
    },
    // externals 配置用于将某些依赖标记为外部依赖，从而避免在打包时将它们一起捆绑进来。
    // vue: 'vue' 表示 Vue 本身不会被打包到生成的库中，而是假定在运行时由宿主环境（即最终使用组件库的项目）提供 Vue。
    // 如果你的组件库依赖 Vue 或其他第三方库，并且希望宿主项目来提供这些依赖，就可以使用 externals。
    externals: process.env.NODE_ENV === 'production' ? { vue: 'vue' } : {}
  },
  // 该配置用于控制 Vue CLI 如何处理项目中的 CSS。
  // extract: false 表示 不将 CSS 提取到独立的文件中，而是将 CSS 直接注入到 JavaScript 文件中。
  // 默认情况下，Vue CLI 在生产环境中会将 CSS 提取成单独的文件（例如 .css 文件），以便优化浏览器的加载性能。
  // 设置 extract: false 后，CSS 会以 <style> 标签的形式嵌入到 HTML 中，或者以内联的方式注入到 JavaScript 打包结果中。
  css: { extract: false }
})

```

创建入口文件 src/index.js 内容如下：

```javascript
// 在自定义 Vue 组件库的项目中通常被用作插件的入口文件，主要作用是方便开发者将该组件库作为一个插件引入到 Vue 项目中，同时按需导出单个组件，供开发者选择使用。
// 这部分代码导入了两个 Vue 组件 MyComponent1 和 MyComponent2，它们位于 src/components 目录下。
import MyComponent1 from './components/MyComponent1.vue';
import MyComponent2 from './components/MyComponent2.vue';

// install 方法是 Vue 插件的约定方法，Vue.use() 会调用这个方法。
// 在这里，install 方法接收 Vue 构造函数作为参数，并通过 Vue.component 全局注册两个组件 MyComponent1 和 MyComponent2。
// 这样，当插件被安装后，这两个组件就可以在任何地方的模板中直接使用，比如 <MyComponent1 /> 和 <MyComponent2 />。
const install = function (Vue) {
    // 注册组件到 Vue 的名称，在调用时直接使用该名称作为标签
    Vue.component('MyComponent1', MyComponent1)
    Vue.component('MyComponent2', MyComponent2)
};

// 自动注册插件（针对 Vue 2.x 的浏览器环境）
// 如果该库是通过 <script> 标签直接引入的（通常用于浏览器环境），window.Vue 会存在，表示全局的 Vue 构造函数。
// 代码会自动检测到全局的 Vue 对象，并调用 install(window.Vue)，将组件自动注册到全局。
// 这使得开发者无需手动调用 Vue.use()，即可直接使用组件。
if (typeof window !== 'undefined' && window.Vue) {
    install(window.Vue);
}

// 导出组件和插件方法
// install 方法：使得该库可以作为 Vue 插件使用，通过 Vue.use() 来安装。
// MyComponent1 和 MyComponent2：单独导出组件，支持按需引入。
// 
// 开发者可以按需引入某个组件，而不是必须引入整个插件。例如：
//      import { MyComponent1 } from 'my-component-library';
//      Vue.component('MyComponent1', MyComponent1);
//
export default {
    install,
    MyComponent1,
    MyComponent2,
};

```

修改 package.json

```json
{
  "scripts": {
    "build": "vue-cli-service build --target lib --name my-component-lib src/index.js"
  },
  "main": "dist/my-component-lib.umd.min.js"
}

```

- `"build": "vue-cli-service build --target lib --name my-component-lib src/index.js"` 解析：
  - 这段配置主要用于设置自定义 Vue 组件库的打包和入口文件，以便于该组件库可以发布为一个通用的 JavaScript 库，供其他项目使用。
  - **`--target lib`**：指定打包的目标为库（library），而不是应用程序。这会生成一个适合作为第三方库分发的文件，而不是一个可直接运行的 HTML 文件。
  - **`--name my-component-lib`**：指定生成的库的名称为 `my-component-lib`。这会影响生成文件的全局变量名称（如果使用 UMD 格式）。例如：会在 dist 目录下生成以 `my-component-lib` 开头文件 my-component-lib.common.js、my-component-lib.umd.js、my-component-lib.umd.min.js。
  - **`src/index.js`**：指定打包的入口文件为 `src/index.js`。这个文件通常是组件库的入口，负责导出所有组件和插件方法。
- `"main": "dist/my-component-lib.umd.min.js"` 解析：
  - `main` 字段是 Node.js 的约定字段，用于指定模块的入口文件。
  - 当该组件库被发布到 npm 并被其他项目安装时，Node.js 会根据这个字段找到库的入口文件。
  - 在这里，`dist/my-component-lib.umd.min.js` 是打包后生成的库的最小化文件 UMD 格式（是一种通用的模块定义格式，支持在 Node.js、AMD（Asynchronous Module Definition）和浏览器环境中使用。这使得组件库可以在各种环境中使用，无论是通过 `<script>` 标签引入，还是通过模块化工具（如 Webpack、Rollup）引入。）。

编译组件

```bash
npm run build
```

自身项目调试组件

- 启用项目

  ```bash
  npm run serve
  ```

- 访问 `http://localhost:8080/` 即可。



使用测试项目借助 npm link 引用组件

>npm link 用法请参考本站 <a href="/nodejs/npm命令.html#npm-link" target="_blank">链接</a>

- 链接自定义组件库到全局 node_modules 中

  ```bash
  cd demo-vue2-cli-my-component-lib
  
  # 会使用自定义组件库目录作为 import from 'demo-vue2-cli-my-component-lib'
  npm link
  ```

- 引用自定义组件库

  创建 Vue2 项目：参考本站 <a href="/vue/脚手架创建项目.html#创建-vue2" target="_blank">链接</a>

  添加自定义组件库依赖

  ```bash
  npm link demo-vue2-cli-my-component-lib
  ```

  在 src/main.js 中注册组件库

  ```javascript
  import Vue from 'vue'
  import App from './App.vue'
  // 导入组件库
  import MyComponentPlugin from 'demo-vue2-cli-my-component-lib'
  // 单独引用组件
  import { MyComponent1 } from 'demo-vue2-cli-my-component-lib'
  
  Vue.config.productionTip = false
  // 注册组件库到 Vue 中，Vue.use 函数会自动调用组件库中的 install 函数
  Vue.use(MyComponentPlugin)
  // 通过 <my-component-1></my-component-1> 单独引用组件库
  Vue.component('my-component-1', MyComponent1)
  
  new Vue({
    render: h => h(App),
  }).$mount('#app')
  ```

  在 src/App.vue 中引用组件库中的组件

  ```vue
  <template>
    <div id="app">
      <MyComponent1></MyComponent1>
      <MyComponent2></MyComponent2>
      <my-component-1></my-component-1>
    </div>
  </template>
  
  <script>
  
  export default {
    name: 'App',
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

参考 <a href="/nodejs/README.html#发布组件到-npm-registry" target="_blank">链接</a> 发布到 npm registry



#### 使用 Vite

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-vue/demo-vue2-vite-my-component-lib)

参考本站 <a href="/vite/README.html#创建-vite-vue2-项目" target="_blank">链接</a> 创建 Vue2+Vite 项目

修改 vite.config.js

```javascript
import { defineConfig } from 'vite'
import { createVuePlugin } from 'vite-plugin-vue2';

export default defineConfig({
    plugins: [createVuePlugin()],
    build: {
        lib: {
            // 自定义组件库打包入口
            entry: './src/index.js',
            name: 'MyComponent',
            // 输出的文件名格式，例如：在 dist 目录中输出 my-component-lib.umd.js 文件
            fileName: (format) => `my-component-lib.${format}.js`,
            formats: ['es', 'umd']
        },
        rollupOptions: {
            // 确保外部化处理那些你不想打包进库的依赖
            external: ['vue'],
            output: {
                // 在 UMD 构建模式下为这些外部化的依赖提供一个全局变量
                globals: {
                    vue: 'Vue'
                }
            }
        }
    }
})

```

创建入口文件 src/index.js 内容如下：

```javascript
// 在自定义 Vue 组件库的项目中通常被用作插件的入口文件，主要作用是方便开发者将该组件库作为一个插件引入到 Vue 项目中，同时按需导出单个组件，供开发者选择使用。
// 这部分代码导入了两个 Vue 组件 MyComponent1 和 MyComponent2，它们位于 src/components 目录下。
import MyComponent1 from './components/MyComponent1.vue';
import MyComponent2 from './components/MyComponent2.vue';

// install 方法是 Vue 插件的约定方法，Vue.use() 会调用这个方法。
// 在这里，install 方法接收 Vue 构造函数作为参数，并通过 Vue.component 全局注册两个组件 MyComponent1 和 MyComponent2。
// 这样，当插件被安装后，这两个组件就可以在任何地方的模板中直接使用，比如 <MyComponent1 /> 和 <MyComponent2 />。
const install = function (Vue) {
    // 注册组件到 Vue 的名称，在调用时直接使用该名称作为标签
    Vue.component('MyComponent1', MyComponent1)
    Vue.component('MyComponent2', MyComponent2)
};

// 自动注册插件（针对 Vue 2.x 的浏览器环境）
// 如果该库是通过 <script> 标签直接引入的（通常用于浏览器环境），window.Vue 会存在，表示全局的 Vue 构造函数。
// 代码会自动检测到全局的 Vue 对象，并调用 install(window.Vue)，将组件自动注册到全局。
// 这使得开发者无需手动调用 Vue.use()，即可直接使用组件。
if (typeof window !== 'undefined' && window.Vue) {
    install(window.Vue);
}

// 导出组件和插件方法
// install 方法：使得该库可以作为 Vue 插件使用，通过 Vue.use() 来安装。
// MyComponent1 和 MyComponent2：单独导出组件，支持按需引入。
// 
// 开发者可以按需引入某个组件，而不是必须引入整个插件。例如：
//      import { MyComponent1 } from 'my-component-library';
//      Vue.component('MyComponent1', MyComponent1);
//
export default {
    install,
    MyComponent1,
    MyComponent2,
};

```

修改 package.json

```json
{
  "main": "dist/my-component-lib.umd.js"
}

```

编译组件

```bash
npm run build
```

自身项目调试组件

- 启用项目

  ```bash
  npm run dev
  ```

- 访问 `http://localhost:5174/` 即可。

使用测试项目借助 npm link 引用组件

>npm link 用法请参考本站 <a href="/nodejs/npm命令.html#npm-link" target="_blank">链接</a>

- 链接自定义组件库到全局 node_modules 中

  ```bash
  cd demo-vue2-vite-my-component-lib
  
  # 会使用自定义组件库目录作为 import from 'demo-vue2-vite-my-component-lib'
  npm link
  ```

- 引用自定义组件库

  创建 Vue2 项目：参考本站 <a href="/vue/脚手架创建项目.html#创建-vue2" target="_blank">链接</a>

  添加自定义组件库依赖

  ```bash
  npm link demo-vue2-vite-my-component-lib
  ```

  在 src/main.js 中注册组件库

  ```javascript
  import Vue from 'vue'
  import App from './App.vue'
  // 导入组件库
  import MyComponentPlugin from 'demo-vue2-vite-my-component-lib'
  // 单独引用组件
  import { MyComponent1 } from 'demo-vue2-vite-my-component-lib'
  
  Vue.config.productionTip = false
  // 注册组件库到 Vue 中，Vue.use 函数会自动调用组件库中的 install 函数
  Vue.use(MyComponentPlugin)
  // 通过 <my-component-1></my-component-1> 单独引用组件库
  Vue.component('my-component-1', MyComponent1)
  
  new Vue({
    render: h => h(App),
  }).$mount('#app')
  ```

  在 src/App.vue 中引用组件库中的组件

  ```vue
  <template>
    <div id="app">
      <MyComponent1></MyComponent1>
      <MyComponent2></MyComponent2>
      <my-component-1></my-component-1>
    </div>
  </template>
  
  <script>
  
  export default {
    name: 'App',
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

参考 <a href="/nodejs/README.html#发布组件到-npm-registry" target="_blank">链接</a> 发布到 npm registry



### 基于 Vue3

>todo 暂时不研究



## 黑暗或明亮主题切换

>dark or light mode.
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-vue/demo-vue2-dark-mode)

示例中主要通过 src/components/ThemeToggle.vue 控件中 `document.documentElement.setAttribute('data-theme', this.isDarkMode ? 'dark' : 'light')` 代码为 `<html>` 标签添加 `data-theme=dark` 或 `data-theme=light` 属性，在 src/App.vue 中根据样式条件设置 css 变量值来达到切换主题效果，根据样式条件设置 css 变量值代码如下：

```html
<style>
:root {
  --bg-color: #ffffff;
  --text-color: #2c3e50;
  --hover-bg-color: rgba(0, 0, 0, 0.1);
}

/*  表示当 HTML 元素的 data-theme 属性值为 "dark" 时，应用以下样式规则 */
[data-theme="dark"] {
  --bg-color: #1a1a1a;
  --text-color: #ffffff;
  --hover-bg-color: rgba(255, 255, 255, 0.1);
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: var(--text-color);
  background-color: var(--bg-color);
  min-height: 100vh;
  margin: 0;
  padding-top: 60px;
  transition: background-color 0.3s, color 0.3s;
}

html,
body {
  margin: 0;
  padding: 0;
}
</style>
```



## 模板语法

>[官方参考链接](https://vuejs.org/guide/essentials/template-syntax.html)

### 介绍

Vue 使用基于 HTML 的模板语法，允许您以声明方式将渲染的 DOM 绑定到底层组件实例的数据。所有 Vue 模板都是语法有效的 HTML，可由符合规范的浏览器和 HTML 解析器进行解析。

底层，Vue 将模板编译为高度优化的 JavaScript 代码。结合响应式系统，Vue 可以智能地找出需要重新渲染的最少组件数量，并在应用程序状态发生变化时应用最少的 DOM 操作。

如果您熟悉虚拟 DOM 概念并且喜欢 JavaScript 的原始功能，您也可以直接编写渲染函数而不是模板，并使用可选的 JSX 支持。但请注意，它们不能像模板一样享受相同级别的编译时优化。



### 使用 JavaScript 表达式

到目前为止，我们只在模板中绑定了简单的属性键。但 Vue 实际上支持所有数据绑定中 JavaScript 表达式的全部功能：

```vue
{{ number + 1 }}

{{ ok ? 'YES' : 'NO' }}

{{ message.split('').reverse().join('') }}

<div :id="`list-${id}`"></div>
```

这些表达式将在当前组件实例的数据范围内被评估为 JavaScript。

在 Vue 模板中，JavaScript 表达式可以在以下位置使用：

- 文本内部插值（mustaches）
- 在任何 Vue 指令的属性值中（以 v- 开头的特殊属性）

示例：

```vue
<el-tooltip class="item" :effect="darkMode?'light':'dark'" content="切换主题" placement="right">
	...
</el-tooltip>
```

示例中绑定属性 `:effect` 中使用三元运算符 `darkMode?'light':'dark'` JavaScript 表达式根据 `darkMode` 布尔值返回 `light` 或者 `dark`。

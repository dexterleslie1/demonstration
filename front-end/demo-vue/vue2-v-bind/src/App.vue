<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </nav>
    <router-view/> -->

    <hr />
    <div>绑定 HTML class - 绑定对象 - 内联字面量的形式绑定一个对象以动态切换 class</div>
    <div>
      <div :class="{ 'my-font-color': applyMyFontColor }">内容</div>
    </div>

    <hr />
    <div>绑定 HTML class - 绑定对象 - 对象中写多个字段来操作多个 class。此外，:class 指令也可以和一般的 class attribute 共存</div>
    <div>
      <div class="my-text-underline" :class="{ 'my-font-color': applyMyFontColor, 'my-font-bold': applyMyFontBold }">内容
      </div>
    </div>

    <hr />
    <div>绑定 HTML class - 绑定对象 - 非内联字面量的形式绑定一个对象以动态切换 class</div>
    <div>
      <div :class="myClass">内容</div>
    </div>

    <hr />
    <div>绑定 HTML class - 绑定对象 - 绑定一个计算属性返回的对象</div>
    <div>
      <div :class="myClassComputed">内容</div>
    </div>

    <hr />
    <div>绑定 HTML class - 绑定数组 - 绑定一个数组来渲染多个 CSS class</div>
    <div>
      <div :class="['my-font-color', 'my-font-bold']">内容</div>
    </div>

    <hr />
    <div>绑定 HTML class - 绑定数组 - 使用三元表达式在数组中有条件地渲染某个 class</div>
    <div>
      <div :class="[this.applyMyFontColor ? 'my-font-color' : '', 'my-font-bold']">内容</div>
    </div>

    <hr />
    <div>绑定 HTML class - 绑定数组 - 数组中嵌套对象有条件地渲染某个 class</div>
    <div>
      <div :class="[{ 'my-font-color': this.applyMyFontColor }, 'my-font-bold']">内容</div>
    </div>

    <hr />
    <div>绑定内嵌样式 - 绑定对象 - 内联字面量的形式绑定一个对象</div>
    <div>
      <div :style="{ 'font-weight': fontWeight }">内容</div>
    </div>

    <hr />
    <div>绑定内嵌样式 - 绑定对象 - 非内联字面量的形式绑定一个对象</div>
    <div>
      <div :style="styleObject">内容</div>
    </div>

    <hr />
    <div>绑定内嵌样式 - 绑定对象 - 绑定对象数组</div>
    <div>
      <div :style="[styleObject, styleObject2]">内容</div>
    </div>

    <hr />
    <div>绑定内嵌样式 - 三目运算符设置样式字符串</div>
    <div>
      <div :style="this.applyMyFontBold ? 'font-weight:bold;' : ''">内容</div>
    </div>

    <hr />
    <div>一次绑定多个属性</div>
    <div>
      <input v-bind="inputAttrs">
    </div>

  </div>
</template>

<script>
export default {
  data() {
    return {
      applyMyFontColor: true,
      applyMyFontBold: true,
      myClass: {
        'my-font-color': true,
        'my-font-bold': false,
      },
      fontWeight: 'bold',
      styleObject: { 'font-weight': 'bold' },
      styleObject2: { 'color': 'red' },
      inputAttrs: {
        type: 'text',
        placeholder: '请输入内容',
        disabled: false,
        class: 'form-input',
        style: { color: 'red', fontSize: '14px' }
      },
    }
  },
  computed: {
    myClassComputed() {
      return {
        'my-font-color': this.applyMyFontColor,
        'my-font-bold': !this.applyMyFontBold,
      }
    }
  }
}
</script>

<style>
.my-font-color {
  color: #42b983;
}

.my-font-bold {
  font-weight: bold;
}

.my-text-underline {
  text-decoration: underline;
}

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

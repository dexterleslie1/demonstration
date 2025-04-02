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

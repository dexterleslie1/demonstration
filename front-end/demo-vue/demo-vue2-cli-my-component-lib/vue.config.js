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
    externals: { vue: 'vue' }
  },
  // 该配置用于控制 Vue CLI 如何处理项目中的 CSS。
  // extract: false 表示 不将 CSS 提取到独立的文件中，而是将 CSS 直接注入到 JavaScript 文件中。
  // 默认情况下，Vue CLI 在生产环境中会将 CSS 提取成单独的文件（例如 .css 文件），以便优化浏览器的加载性能。
  // 设置 extract: false 后，CSS 会以 <style> 标签的形式嵌入到 HTML 中，或者以内联的方式注入到 JavaScript 打包结果中。
  css: { extract: false }
})

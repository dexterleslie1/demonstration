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

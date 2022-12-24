const { resolve } = require("path")

module.exports = {
    // 打包的入口
    entry: {
        // 多入口打包
        entry1: ["./src/entry1.js", "./src/common.js"],
        entry2: ["./src/entry2.js", "./src/common.js"]
    },

    // 指定webpack打包后bundle的输出路径和名称
    output: {
        // [name]是以上面entry1和entry2作为输出的文件名称
        filename: "[name].js",
        path: resolve(__dirname, "build")
    },

    // 因为webpack只能处理javascript资源
    // loader是让webpack能够处理除javascript意外的资源
    module: {
        rules: [

        ]
    },


    plugins: [

    ],

    // 有production和development模式
    mode: "development",
}
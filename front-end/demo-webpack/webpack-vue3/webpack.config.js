const { VueLoaderPlugin } = require('vue-loader')
const { resolve } = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')

module.exports = {
    entry: [
        './index.js'
    ],
    output: {
        path: resolve(__dirname, 'dist'),
        filename: "index.js"
    },
    module: {
        rules: [
            {
                test: /\.vue$/,
                loader: 'vue-loader'
            }
        ]
    },
    plugins: [
        new VueLoaderPlugin(),
        new HtmlWebpackPlugin()
    ],
    mode: 'development',
    devServer: {
        open: true,
        port: 9080,
        // 必须为./，否则HMR不正常工作
        static: './'
    }
};
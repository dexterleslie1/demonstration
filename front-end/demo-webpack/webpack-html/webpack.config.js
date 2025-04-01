const { resolve } = require("path")
const HtmlWebpackPlugin = require("html-webpack-plugin")
const MiniCssExtractPlugin = require("mini-css-extract-plugin")
// https://github.com/webpack-contrib/css-minimizer-webpack-plugin
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin")

module.exports = {
    // webpack 打包入口文件配置
    // 多入口配置
    entry: {
        vendor: ["./src/js/common.js", "./src/js/jquery.js"],
        index: "./src/js/index.js",
        cart: "./src/js/cart.js"
    },

    // webpack打包后输出配置
    output: {
        // 输出文件名
        filename: "[name].js",
        // 输出目录
        path: resolve(__dirname, "build")
    },

    // loader处理非javascript、json资源，例如：翻译sass为css、img
    // 配置loader
    module: {
        rules: [/*{
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },*/{
                test: /\.css$/,
                use: [MiniCssExtractPlugin.loader /* 使用MiniCssExtractPlugin在html页面中插入单独的style标签 */, 'css-loader']
            }, {
                // 处理less文件
                test: /\.less$/,
                use: [MiniCssExtractPlugin.loader, "css-loader", "less-loader"]
            }, {
                // 处理scss文件
                test: /\.scss$/,
                use: [MiniCssExtractPlugin.loader, "css-loader", "sass-loader"]
            }
        ]
    },

    plugins: [
        // 使用html-webpack-plugin插件自动引入js、css资源文件index.html
        new HtmlWebpackPlugin({
            // 指定使用的html，否则插件会自动生成一个新的html
            template: "./src/index.html",
            // 指定输出html文件名称
            filename: "index.html",
            // 指定这个页面加载index和vendor chunks
            chunks: ["index", "vendor"],
            minify: {
                // 删除html空格
                collapseWhitespace: true,
                // 删除html注释
                removeComments: true
            }
        }),
        new HtmlWebpackPlugin({
            // 指定使用的html，否则插件会自动生成一个新的html
            template: "./src/cart.html",
            // 指定输出html文件名称
            filename: "cart.html",
            // 指定这个页面加载index和vendor chunks
            chunks: ["cart", "vendor"],
            minify: {
                // 删除html空格
                collapseWhitespace: true,
                // 删除html注释
                removeComments: true
            }
        }),
        // 打包css资源到单独文件并在html head中插入style标签引用这个css文件
        new MiniCssExtractPlugin({
            filename: "./css/all.css",
        })
    ],

    optimization: {
        minimizer: [
            // 这个配置只对production模式css压缩，不对development模式压缩
            new CssMinimizerPlugin(),
        ],
        // 开启development模式css压缩
        minimize: true,
    },

    // development、开发模式 production、生产模式
    mode: "production",
    // webpack5 自动刷新浏览器
    target: "web",
    devServer: {
        open: true,
        port: 9080,
        // 必须为./，否则HMR不正常工作
        static: './'
    }
}
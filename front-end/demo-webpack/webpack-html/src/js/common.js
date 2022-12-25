// 通过require引用css，才能够使webpack通过style-loader和css-loader处理css
require("../css/index.css")
require("../css/index.less")
require("../css/index.scss")

console.log("This is a message from common.js")
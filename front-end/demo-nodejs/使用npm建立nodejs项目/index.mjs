// NOTE: 文件只有命名为index.mjs才能够使用 yarn test 运行，
// 否则报告 "Cannot use import statement outside a module" 错误

import moment from "moment";

console.log(`当前时间: ${moment().format("YYYY-MM-DD HH:mm:ss")}`)
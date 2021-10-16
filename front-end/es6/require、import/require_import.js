// 使用node执行命令：node require_import.js

const requireExport = require('./require_export')

console.log('1+3=' + requireExport.add(1, 3))
console.log('3-2=' + requireExport.sub(3, 2))

const addFunction = require('./require_export').add

console.log('3+5=' + addFunction(3, 5))
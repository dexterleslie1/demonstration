let myFunctionExport = require('./require_function_export')

let a = 1
let b = 5
console.log(`${a}+${b}=${myFunctionExport.add(a, b)}`)

let myAdd = require('./require_function_export').add
a = 5
b = 9
console.log(`${a}+${b}=${myAdd(a, b)}`)
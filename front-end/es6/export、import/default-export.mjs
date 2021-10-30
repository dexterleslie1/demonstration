// 使用import命令的时候，用户需要知道所要加载的变量名或函数名，否则无法加载。但是，用户肯定希望快速上手，未必愿意阅读文档，去了解模块有哪些属性和方法。
// 为了给用户提供方便，让他们不用阅读文档就能加载模块，就要用到export default命令

export default function(a, b) {
    return a+b
}

// // 或者上面export default function...等价写法
// function add(a, b) {
//     return a+b
// }
// export default add
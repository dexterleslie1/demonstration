// 使用module.exports导出变量、函数、类
// https://www.tutorialsteacher.com/nodejs/nodejs-module-exports

// 导出类
module.exports.Person = function(name, age) {
    this.name = name
    this.age = age

    this.display = function() {
        console.log(`name=${this.name},age=${this.age}`)
    }
}

// 导出函数
module.exports.add = function(a, b) {
    return a+b
}

// 导出命名变量
module.exports.HelloMessage = 'Hello Dexterleslie'

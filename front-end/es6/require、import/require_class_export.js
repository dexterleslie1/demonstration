// 演示导出类

module.exports = function(name, age) {
    this.name = name
    this.age = age

    this.display = function() {
        console.log(`name=${this.name},age=${this.age}`)
    }
}
// 命名导出

// 导出常量
export const ConstVariable1 = '常量1'

// // 或者上面常量导出的等价写法
// const ConstVariable1 = '常量1'
// export {ConstVariable1}

// 导出变量
export let variable2 = '变量2'

// 导出函数
export function add(a, b) {
    return a+b;
}

// 导出构造函数
export function Person(name, age) {
    this.name = name
    this.age = age

    this.display = function() {
        console.log(`name=${this.name},age=${this.age}`)
    }
}

// 导出对象
export let ObjectVariable = {
    prop1: 'prop value1',
    prop2: 'prop value2',
}
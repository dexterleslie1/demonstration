import MyAddFunction from 'my-package'

console.log(`1+5=${MyAddFunction(1, 5)}`)

// 命令导入

// ES6导入模块错误Cannot use import statement outside a module
// https://www.jianshu.com/p/b802982bad8c

// Module 的语法
// https://es6.ruanyifeng.com/#docs/module

// export用法
// https://developer.mozilla.org/en-US/docs/web/javascript/reference/statements/export

import { 
    ConstVariable1,
    variable2,
    add,
    Person,
    ObjectVariable
} from "my-package";

// 所有export一次import加载写法
import * as allExport from 'my-package'

console.log(`ConstVariable1=${ConstVariable1}`)
console.log(`variable2=${variable2}`)
console.log(`1+2=${add(1, 2)}`)
let person = new Person('Dexterleslie', 30)
person.display()
console.log(`ObjectVariable.prop1=${ObjectVariable.prop1},ObjectVariable.prop2=${ObjectVariable.prop2}`)

console.log(`ConstVariable1=${allExport.ConstVariable1}`)
console.log(`variable2=${allExport.variable2}`)
console.log(`1+2=${allExport.add(1, 2)}`)
person = new allExport.Person('Dexterleslie', 30)
person.display()
console.log(`ObjectVariable.prop1=${allExport.ObjectVariable.prop1},ObjectVariable.prop2=${allExport.ObjectVariable.prop2}`)

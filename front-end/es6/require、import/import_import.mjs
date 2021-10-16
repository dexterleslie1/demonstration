// 使用node执行命令：node import_import.mjs

import {functionTest} from "./import_export.mjs"
import defaultObject from "./import_export.mjs"

// ES6导入模块错误Cannot use import statement outside a module
// https://www.jianshu.com/p/b802982bad8c

// Module 的语法
// https://es6.ruanyifeng.com/#docs/module

functionTest();
functionTest({a: 101});

console.log("默认export变量key1=" + defaultObject.key1 + ",key2=" + defaultObject.key2);
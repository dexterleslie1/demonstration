# JavaScript



## 模板字符串

JavaScript 的**模板字符串**（Template Literals）是 ES6（ECMAScript 2015）引入的一种新特性，用于更方便地创建和操作字符串。它通过反引号（```）包裹字符串，并提供了一些强大的功能，比如字符串插值、多行字符串和嵌套表达式。

### 模板字符串的基本语法

1. 使用反引号：
   - 模板字符串使用反引号（```）而不是单引号（`'`）或双引号（`"`）来定义字符串。
2. 字符串插值：
   - 在模板字符串中，可以通过 `${expression}` 的形式嵌入表达式，表达式会被计算并替换为结果。

### 示例代码

```javascript
// 基本用法
const name = "Alice";
const greeting = `Hello, ${name}!`;
console.log(greeting); // 输出: Hello, Alice!
 
// 嵌入表达式
const a = 5;
const b = 10;
console.log(`The sum of ${a} and ${b} is ${a + b}.`); // 输出: The sum of 5 and 10 is 15.
 
// 多行字符串
const multiLineString = `This is a
multi-line
string.`;
console.log(multiLineString);
/*
输出:
This is a
multi-line
string.
*/
 
// 嵌套模板字符串
const firstName = "Bob";
const lastName = "Smith";
const fullName = `${firstName} ${lastName}`;
const message = `Hello, my name is ${fullName}.`;
console.log(message); // 输出: Hello, my name is Bob Smith.
```

### 模板字符串的优势

1. 可读性：
   - 模板字符串使字符串插值和多行字符串的书写更加直观和易读。
2. 灵活性：
   - 可以在字符串中嵌入任意有效的 JavaScript 表达式，而不仅仅是变量。
3. 避免转义：
   - 在模板字符串中，不需要像传统字符串那样使用反斜杠（`\`）来转义换行符或引号。

### 注意事项

- 模板字符串中的表达式会被计算并替换为结果，因此表达式中不应包含会引发副作用的代码（如修改全局变量），除非这是你期望的行为。
- 模板字符串本身并不会自动进行 HTML 转义。如果你需要在模板字符串中插入用户输入的内容以防止 XSS 攻击，需要手动进行转义。

模板字符串是现代 JavaScript 开发中非常实用的一个特性，它大大简化了字符串的创建和操作，提高了代码的可读性和可维护性。



## prototype

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/js-prototype)

特点如下：

- 任何函数都有 prototype 属性，并且 prototype 是 Object 类型的一个实例
- prototype 对象属性 constructor 指向该函数本身
- 给 prototype 对象添加属性/方法，函数所有实例对象自动拥有 prototype 中的属性/方法

示例：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <script type="text/javascript">
        // 任何函数都有 prototype 属性，并且 prototype 是 Object 类型的一个实例
        console.log(`typeof Date: ${typeof Date}`)
        console.log(`typeof Date.prototype: ${typeof Date.prototype}`)

        function MyFunction() {

        }
        console.log(`typeof MyFunction: ${typeof MyFunction}`)
        console.log(`typeof MyFunction.prototype: ${typeof MyFunction.prototype}`)

        // constructor
        // prototype 对象属性 constructor 指向该函数本身
        console.log(`Date.prototype.constructor===Date: ${Date.prototype.constructor===Date}`)
        console.log(`MyFunction.prototype.constructor===MyFunction: ${MyFunction.prototype.constructor===MyFunction}`)
    
        // prototype添加方法
        // 给 prototype 对象添加属性/方法，函数所有实例对象自动拥有 prototype 中的属性/方法
        MyFunction.prototype.sayHello = function() {
            console.log('Hello Dexterleslie')
        }
        let myFunction = new MyFunction()
        myFunction.sayHello()
    </script>
</head>
<body>
    
</body>
</html>
```


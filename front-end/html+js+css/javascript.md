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



## `prototype`

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



## `var` 和 `const` 区别

在 JavaScript 中，`var` 和 `const` 都是用于声明变量的关键字，但它们在作用域、可变性、提升行为等方面有显著差异。以下是核心区别的详细对比：


### 1. **作用域（Scope）**
- **`var`**：函数作用域（Function-scoped）。  
  变量在声明它的函数内有效，若在全局作用域声明则属于全局对象（如 `window`）。  
  示例：  
  ```javascript
  function example() {
    if (true) {
      var x = 10; // 函数作用域，x 在整个函数内有效
    }
    console.log(x); // 输出 10（可以访问）
  }
  ```

- **`const`**：块级作用域（Block-scoped）。  
  变量仅在声明它的块（`{}` 包裹的区域，如 `if`、`for`、`while` 等）内有效。  
  示例：  
  ```javascript
  function example() {
    if (true) {
      const y = 20; // 块级作用域，仅 if 块内有效
    }
    console.log(y); // 报错：ReferenceError: y is not defined
  }
  ```


### 2. **变量提升（Hoisting）**
- **`var`**：支持变量提升，但仅提升声明，赋值保留在原处。  
  声明前访问会得到 `undefined`（不会报错）。  
  示例：  
  ```javascript
  console.log(a); // 输出 undefined（声明被提升到顶部）
  var a = 1;
  ```

- **`const`**：同样存在提升（所有声明都会提升），但由于**必须初始化**且存在“暂时性死区”（Temporal Dead Zone, TDZ），声明前访问会直接报错。  
  示例：  
  ```javascript
  console.log(b); // 报错：ReferenceError（TDZ 内访问）
  const b = 2;
  ```


### 3. **重复声明（Redeclaration）**
- **`var`**：允许重复声明同一变量（后声明的覆盖先声明的），不会报错。  
  示例：  
  ```javascript
  var c = 3;
  var c = 4; // 不报错，c 最终为 4
  ```

- **`const`**：禁止重复声明同一变量（同一作用域内），否则报错。  
  示例：  
  ```javascript
  const d = 5;
  const d = 6; // 报错：SyntaxError（重复声明）
  ```


### 4. **可变性（Immutability）**
- **`var`**：声明的变量可以**重新赋值**或**修改值**（无限制）。  
  示例：  
  ```javascript
  var e = 10;
  e = 20; // 允许重新赋值
  e.name = "test"; // 若 e 是对象，可修改属性（允许）
  ```

- **`const`**：声明的变量**不能重新赋值**（绑定不可变），但如果是对象/数组，其内部属性/元素可以修改（值可变）。  
  示例：  
  ```javascript
  const f = 30;
  f = 40; // 报错：TypeError（不能重新赋值）
  
  const obj = { name: "Alice" };
  obj.name = "Bob"; // 允许修改对象属性
  obj = { age: 18 }; // 报错：不能重新赋值绑定
  
  const arr = [1, 2];
  arr.push(3); // 允许修改数组内容
  arr = [4, 5]; // 报错：不能重新赋值绑定
  ```


### 5. **初始化要求**
- **`var`**：声明时**可选初始化**（未初始化时默认值为 `undefined`）。  
  示例：  
  ```javascript
  var g; // 合法，g 初始值为 undefined
  ```

- **`const`**：声明时**必须初始化**（否则语法错误）。  
  示例：  
  ```javascript
  const h; // 报错：SyntaxError（缺少初始化）
  ```


### 6. **全局作用域行为**
- **`var`**：在全局作用域声明的变量会成为 `window` 对象的属性（浏览器环境）。  
  示例：  
  ```javascript
  var globalVar = "hello";
  console.log(window.globalVar); // 输出 "hello"
  ```

- **`const`**：在全局作用域声明的变量**不会成为 `window` 对象的属性**（更安全，避免全局污染）。  
  示例：  
  ```javascript
  const globalConst = "world";
  console.log(window.globalConst); // 输出 undefined（非严格模式）
  ```


### 总结：如何选择？
- 优先使用 `const`：当变量不需要重新赋值时（如固定配置、对象/数组引用），用 `const` 声明，确保不可变性，减少意外修改。  
- 其次使用 `let`：当需要变量重新赋值时（如循环计数器），用 `let` 声明（块级作用域更安全）。  
- 避免使用 `var`：除非需要兼容旧代码或明确需要函数作用域（现代 JS 中已很少需要）。


**一句话记忆**：`const` 是“常量绑定”（绑定不可变），`var` 是“函数作用域变量”（灵活但易出错）。

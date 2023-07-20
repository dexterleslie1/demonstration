## 创建并运行第一个jest项目

> https://jestjs.io/docs/getting-started

```
# 初始化项目
npm init

# 添加jest依赖
npm install jest --save-dev

# 创建sum.js文件
function sum(a, b) {
  return a + b;
}
module.exports = sum;

# 创建sum.test.js
const sum = require('./sum');

test('adds 1 + 2 to equal 3', () => {
  expect(sum(1, 2)).toBe(3);
});

# package.json添加
{
  "scripts": {
    "test": "jest"
  }
}

# 运行测试
npm run test
```


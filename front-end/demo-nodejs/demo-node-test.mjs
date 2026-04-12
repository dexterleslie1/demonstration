// 运行方式：node --test demo-node-test.mjs

import test from 'node:test';
import assert from 'node:assert/strict';

// 基本断言示例
test('基本数学运算', () => {
  assert.strictEqual(1 + 1, 2);
  assert.notStrictEqual(1 + 1, 3);
});

// 对象深度比较
test('深度对象比较', () => {
  const actual = { name: 'node', versions: { node: '18', npm: '9' } };
  const expected = { name: 'node', versions: { node: '18', npm: '9' } };
  assert.deepStrictEqual(actual, expected);
});

// 异常抛出测试
function throwError() {
  throw new Error('测试错误');
}

test('断言函数会抛出错误', () => {
  assert.throws(
    () => throwError(),
    {
      name: 'Error',
      message: '测试错误',
    }
  );
});

// 异步测试示例
// Node.js 中很多操作是异步的，例如网络请求、文件 I/O、数据库访问、定时任务
// 以及 Promise 或 async/await 风格的 API。使用 node:test 可以直接测试异步代码，
// 并且当测试函数返回 Promise 时，框架会等待这个 Promise 完成。
// 这样可以确保测试在异步操作完成后才结束，否则可能出现“测试提前结束”或“断言未执行”的问题。

test('异步函数测试', async () => {
  // 这个示例使用 Promise.resolve 模拟异步结果。
  const result = await Promise.resolve('成功');
  assert.strictEqual(result, '成功');

  // 失败情况
  // await new Promise((_, reject) => reject(new Error('异步错误')))

  // 成功情况
  // await new Promise((resolve) => resolve('成功'))
});

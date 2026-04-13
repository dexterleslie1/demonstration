'use strict';

/**
 * demo-signal-server.js 集成测试：随机端口起服务，验证 HTTP 提示、路径约束与广播语义。
 * 运行：npm test 或 node --test demo-signal-server.test.js
 */

const { test } = require('node:test');
const assert = require('node:assert');
const http = require('http');
const { WebSocket } = require('ws');

const { server } = require('./demo-signal-server');

/**
 * @param {number} port
 * @param {string} path
 * @returns {Promise<import('ws').WebSocket>}
 */
function connectWs(port, path) {
  return new Promise((resolve, reject) => {
    const ws = new WebSocket(`ws://127.0.0.1:${port}${path}`);
    ws.once('open', () => resolve(ws));
    ws.once('error', reject);
  });
}

/**
 * @param {import('ws').WebSocket} ws
 * @returns {Promise<Buffer>}
 */
function nextMessage(ws) {
  return new Promise((resolve, reject) => {
    ws.once('message', (data) => resolve(Buffer.isBuffer(data) ? data : Buffer.from(data)));
    ws.once('error', reject);
    ws.once('close', () => reject(new Error('closed before message')));
  });
}

test('demo-signal-server', async (t) => {
  await new Promise((resolve, reject) => {
    server.listen(0, (err) => (err ? reject(err) : resolve()));
  });
  const addr = server.address();
  assert.ok(addr && typeof addr === 'object' && 'port' in addr);
  const port = /** @type {import('net').AddressInfo} */ (addr).port;

  try {
    await t.test('GET / 返回说明且提示 /connector', async () => {
      const body = await new Promise((resolve, reject) => {
        http
          .get({ hostname: '127.0.0.1', port, path: '/' }, (res) => {
            assert.strictEqual(res.statusCode, 200);
            const chunks = [];
            res.on('data', (c) => chunks.push(c));
            res.on('end', () => resolve(Buffer.concat(chunks).toString('utf8')));
            res.on('error', reject);
          })
          .on('error', reject);
      });
      assert.ok(body.includes('connector'));
    });

    await t.test('非 /connector 路径无法完成 WebSocket 握手', async () => {
      await new Promise((resolve, reject) => {
        const ws = new WebSocket(`ws://127.0.0.1:${port}/`);
        const timer = setTimeout(() => {
          ws.terminate();
          reject(new Error('expected handshake failure within timeout'));
        }, 3000);
        ws.once('error', () => {
          clearTimeout(timer);
          resolve(undefined);
        });
        ws.once('open', () => {
          clearTimeout(timer);
          ws.close();
          reject(new Error('should not open on wrong path'));
        });
      });
    });

    await t.test('两客户端：文本只广播给对端，发送者不收到', async () => {
      const a = await connectWs(port, '/connector');
      const b = await connectWs(port, '/connector');

      const aGot = new Promise((resolve) => {
        a.once('message', () => resolve(true));
        setTimeout(() => resolve(false), 500);
      });
      const bGot = nextMessage(b);

      a.send('ping-from-a');

      const [aReceived, buf] = await Promise.all([aGot, bGot]);
      assert.strictEqual(aReceived, false);
      assert.strictEqual(buf.toString('utf8'), 'ping-from-a');

      a.close();
      b.close();
      // close() 只发起关闭流程：底层 TCP/WebSocket 的 FIN、对端 ACK、以及服务端
      // ws 库的「连接关闭、从广播集合里移除」等回调都会在后续事件循环阶段执行。
      // 若立刻进入下一个子测试并新建连接，可能与尚未跑完的关闭回调交错，出现偶发竞态。
      // 用 setImmediate 把当前 Promise 微任务之后、I/O 与 close 相关回调先跑完一轮，
      // 再结束本用例，降低与后续用例之间的相互干扰。
      await new Promise((r) => setImmediate(r));
    });

    await t.test('两客户端：二进制帧原样广播且仍为 binary', async () => {
      const a = await connectWs(port, '/connector');
      const b = await connectWs(port, '/connector');

      const recv = new Promise((resolve, reject) => {
        b.once('message', (data, isBinary) => {
          resolve({ data: Buffer.from(data), isBinary });
        });
        b.once('error', reject);
      });

      a.send(Buffer.from([0, 1, 2, 255]), { binary: true });

      const { data, isBinary } = await recv;
      assert.deepStrictEqual(Array.from(data), [0, 1, 2, 255]);
      assert.strictEqual(isBinary, true);

      a.close();
      b.close();
      await new Promise((r) => setImmediate(r));
    });
  } finally {
    await new Promise((resolve, reject) => {
      server.close((err) => (err ? reject(err) : resolve()));
    });
  }
});

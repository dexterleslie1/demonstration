'use strict';

/**
 * WebSocket 信令服务器（用于 WebRTC / libnice 等场景的 SDP、ICE candidate 交换）。
 *
 * 职责：
 *   - 接受多个客户端的长连接；
 *   - 将某一客户端发来的消息原样广播给「其他」客户端（不包含发送者本人），
 *     避免信令回环；文本与二进制帧均支持。
 *
 * 依赖：npm install ws
 * 运行：node demo-signal-server.js
 * 端口：环境变量 PORT，未设置时默认 8765
 * 路径：仅接受 ws://<host>:<port>/connector（根路径等其他 URL 不会完成 WebSocket 握手）
 */

const http = require('http');
const { WebSocketServer, WebSocket } = require('ws');

// 监听端口：优先读取环境变量，便于部署时用 PORT=9000 node ... 覆盖
const PORT = Number.parseInt(process.env.PORT || '8765', 10);

// 底层 HTTP 服务器：WebSocket 握手建立在 HTTP Upgrade 之上，因此需要 http.Server。
// 对普通浏览器 HTTP 请求返回简短说明，便于确认进程已启动（非 WebSocket 客户端也可 curl）。
const server = http.createServer((_req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/plain; charset=utf-8' });
  res.end('WebSocket signal server — connect with ws://<host>:<port>/connector\n');
});

// 将 WebSocket 服务挂到同一端口，且仅 path 为 /connector 的请求会升级为 WebSocket
const wss = new WebSocketServer({ server, path: '/connector' });

// 当前所有已连接、尚未关闭的 WebSocket 实例，用于遍历广播。
/** @type {Set<import('ws').WebSocket>} */
const clients = new Set();

/**
 * 向除发送者以外的所有在线客户端转发同一条消息。
 *
 * @param {import('ws').WebSocket} from  当前发送方，不会收到自己的消息
 * @param {Buffer|string|ArrayBuffer|Buffer[]} data  与 ws 收到的 payload 一致，原样转发
 * @param {boolean} isBinary  是否为二进制帧；必须与原始 message 事件一致，否则对端解析会错
 */
function broadcast(from, data, isBinary) {
  for (const client of clients) {
    // 跳过发送者：信令通常只需对端处理，避免本端重复处理同一条 SDP/ICE
    if (client === from) continue;
    // 连接可能正在关闭，仅向 OPEN 状态发送，避免抛错或无效发送
    if (client.readyState !== WebSocket.OPEN) continue;
    client.send(data, { binary: isBinary });
  }
}

wss.on('connection', (ws) => {
  clients.add(ws);

  // 任意客户端发来一帧（文本或二进制），即广播给其他所有人
  ws.on('message', (data, isBinary) => {
    broadcast(ws, data, isBinary);
  });

  // 正常关闭：从集合移除，后续广播不再包含此连接
  ws.on('close', () => {
    clients.delete(ws);
  });

  // 异常断开（网络错误等）：同样移除，防止 Set 泄漏已死连接
  ws.on('error', () => {
    clients.delete(ws);
  });
});

// 作为模块被 require 时不监听端口，便于测试里 server.listen(0) 绑定随机端口
module.exports = { server, wss };

if (require.main === module) {
  // 0.0.0.0 表示监听所有网卡，便于局域网其他机器或容器访问
  server.listen(PORT, () => {
    console.log(`WebSocket signal server listening on ws://0.0.0.0:${PORT}/connector`);
  });
}

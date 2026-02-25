const express = require('express');
// 创建应用
const app = express();
const PORT = 3000;

// 中间件：express.json() 解析 JSON 请求体
app.use(express.json());

// GET 根路径
app.get('/', (req, res) => {
  res.send('Hello Express');
});

// GET 带路径参数
app.get('/user/:id', (req, res) => {
  res.json({ id: req.params.id, name: '示例用户' });
});

// POST 接收 JSON
app.post('/echo', (req, res) => {
  res.json(req.body);
});

// 监听 3000
app.listen(PORT, () => {
  console.log(`服务已启动: http://localhost:${PORT}`);
});

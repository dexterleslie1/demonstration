# HTTP/1.1 vs HTTP/2 浏览器性能对比测试

## 简介

这个demo通过浏览器测试对比HTTP/1.1和HTTP/2的性能差异，更接近真实用户环境。

## 快速开始

### 1. 生成SSL证书

**Linux/Mac:**
```bash
chmod +x generate-cert.sh
./generate-cert.sh
```

**Windows:**
```bash
generate-cert.bat
```

或者手动生成：
```bash
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout key.pem \
    -out cert.crt \
    -subj "/C=CN/ST=State/L=City/O=Organization/CN=localhost"
```

### 2. 启动服务

```bash
docker-compose up -d
```

### 3. 浏览器测试

#### 测试HTTP/1.1
1. 打开浏览器，访问: `http://localhost:8080`
2. 打开开发者工具（F12）
3. 切换到 **Network** 标签
4. 刷新页面（Ctrl+R 或 Cmd+R）
5. 观察：
   - 协议列显示 `http/1.1`
   - 连接数（通常看到6个并发连接）
   - 资源加载时间线
   - 页面底部的性能指标

#### 测试HTTP/2
1. 访问: `https://localhost:8443`（首次访问需要接受自签名证书警告）
2. 打开开发者工具（F12）
3. 切换到 **Network** 标签
4. 刷新页面
5. 观察：
   - 协议列显示 `h2`（HTTP/2）
   - **所有资源使用同一个连接**
   - 资源并行加载（多路复用）
   - 更快的加载时间

## 浏览器开发者工具使用技巧

### Chrome/Edge
1. **Network标签**：
   - 查看"Protocol"列确认协议版本
   - 查看"Connection ID"列（HTTP/2应该都是同一个ID）
   - 使用"Waterfall"视图查看资源加载时间线
   - 右键点击列标题可以添加"Connection ID"列

2. **Performance标签**：
   - 点击录制按钮
   - 刷新页面
   - 停止录制
   - 查看详细的加载时间线

3. **Console标签**：
   - 查看页面输出的性能分析数据
   - 可以看到资源加载的详细信息

### Firefox
1. **Network标签**：
   - 查看"Protocol"列
   - 使用"Waterfall"视图
   - 点击资源可以查看详细信息

2. **Performance标签**：
   - 录制性能数据
   - 分析加载时间

## 关键对比指标

| 指标 | HTTP/1.1 | HTTP/2 |
|------|----------|--------|
| 连接数 | 6个（浏览器限制） | 1个 |
| 资源加载方式 | 串行（受连接限制） | 并行（多路复用） |
| 头部大小 | 未压缩 | HPACK压缩 |
| 加载时间 | 较慢 | 更快 |

## 预期结果

- **HTTP/1.1**: 
  - 资源按连接数限制串行加载
  - 总加载时间较长
  - Network标签中看到多个连接ID
  - Waterfall视图中可以看到资源排队等待

- **HTTP/2**:
  - 所有资源在单连接上并行加载
  - 总加载时间明显更短
  - Network标签中所有资源使用同一个连接ID
  - Waterfall视图中资源几乎同时开始加载

## 测试场景说明

本测试页面包含：
- 3个CSS文件
- 3个JavaScript文件
- 15个图片文件

这些资源模拟了真实网页的加载场景，可以清晰地展示HTTP/2多路复用的优势。

## 注意事项

1. **清除缓存**: 每次测试前建议清除浏览器缓存（Ctrl+Shift+Delete）
2. **禁用扩展**: 某些浏览器扩展可能影响测试结果
3. **网络条件**: 可以在开发者工具的Network标签中模拟慢速网络（Throttling）
4. **多次测试**: 建议多次刷新取平均值
5. **SSL证书警告**: HTTP/2使用HTTPS，首次访问需要接受自签名证书警告

## 性能分析

页面内置了性能监控脚本，会自动记录：
- DOMContentLoaded时间
- 页面完全加载时间
- 资源总数
- 总传输大小

打开浏览器控制台（Console）可以查看详细的性能分析数据。

## 清理

```bash
docker-compose down
```

## 故障排查

### 证书错误
如果遇到SSL证书错误，确保：
1. `cert.crt` 和 `key.pem` 文件已生成
2. 文件路径正确
3. 浏览器中点击"高级" -> "继续访问"（自签名证书）

### 端口冲突
如果8080或8443端口被占用，修改 `docker-compose.yml` 中的端口映射。

### 资源404错误
确保所有资源文件都已创建：
- `index.html`
- `assets/css/style1.css`, `style2.css`, `style3.css`
- `assets/js/script1.js`, `script2.js`, `script3.js`
- `assets/images/img1.jpg` 到 `img15.jpg`


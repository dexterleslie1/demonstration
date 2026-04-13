## libnice库是什么呢？

libnice 是一个开源的 C 语言库，它实现了 ICE（交互式连接建立）协议，旨在帮助应用程序在复杂的网络环境下（如存在 NAT 或防火墙）建立点对点的网络连接。

简单来说，当两个位于不同内网的设备想要直接通信时，NAT 设备会成为一个障碍。libnice 通过协调 STUN 和 TURN 协议来解决这个“NAT 穿透”问题。

### 核心功能与工作原理

libnice 的核心是 ICE 协议，它通过一个智能的流程来寻找最佳的连接路径：

1.  **收集候选地址 (Gathering Candidates)**
    libnice 会收集所有可能的连接地址，包括：
    *   **主机候选地址 (Host Candidate):** 设备自身的本地 IP 地址。
    *   **服务器反射候选地址 (Server-Reflexive Candidate):** 通过查询 STUN 服务器获取的公网 IP 地址和端口。
    *   **中继候选地址 (Relayed Candidate):** 当直接连接无法建立时，通过 TURN 服务器获取的中继地址。

2.  **连通性检查 (Connectivity Checks)**
    在收集完候选地址后，libnice 会与通信对端交换这些信息（通常通过 SDP 协议描述），然后开始尝试所有可能的地址组合，以找到一条能够成功通信的路径。

3.  **建立连接**
    一旦找到可行的路径，连接便建立成功。ICE 协议会优先选择最直接的连接方式（如 P2P 直连），只有在直连失败时才会使用 TURN 服务器进行数据中继，从而在保证连通性的同时优化性能。

### 主要特性

*   **协议支持:** 完整实现了 ICE、STUN 和 TURN 协议，支持加密通道。
*   **跨平台:** 基于 GLib 库开发，具有良好的可移植性，可以在 Linux、Windows 等多种操作系统上运行。
*   **多传输协议:** 支持 UDP、TCP 和 WebSocket 等多种传输层协议。
*   **易于集成:** 提供了丰富的 API，并支持 C、C++ 等多种语言绑定，方便开发者集成到项目中。
*   **事件驱动:** 采用事件驱动的 API 设计，能高效处理并发连接和异步操作。

### 应用场景

libnice 广泛应用于需要稳定、高效网络通信的实时应用，特别是 WebRTC 相关的场景，例如：

*   **实时音视频通信 (VoIP, 视频会议)**
*   **在线游戏**
*   **文件传输**
*   **任何需要 P2P 连接的应用**

许多知名的开源项目，如 Janus 和 Licode 等 WebRTC 服务器，都使用 libnice 来处理底层的网络连接。

### 与相关技术的区别

*   **libnice vs. coturn:**
    *   **libnice** 是一个 **客户端库**，开发者将其集成到自己的应用中，用于发起和建立连接。
    *   **coturn** 是一个 **服务器软件**，它实现了 STUN 和 TURN 服务器，为 libnice 这样的客户端提供网络地址查询和数据中继服务。
*   **libnice vs. Libjingle:**
    *   两者都是实现 ICE 协议的库。Libjingle 是 WebRTC 项目的一部分，功能强大但集成相对复杂。libnice 作为一个独立的库，使用上更为灵活和方便。

### 示例

## libnice怎么p2p连接呢？

`libnice` 实现 P2P 连接的核心是遵循 **ICE (Interactive Connectivity Establishment)** 协议。这个过程可以想象成两个人（我们称他们为 A 和 B）想要直接通话，但都身处不同的公司内网，无法直接联系。

整个 P2P 连接过程主要分为以下四个关键步骤：

### 第一步：收集候选地址

首先，通信双方（A 和 B）各自使用 `libnice` 库来收集自己所有可能的网络地址，这些地址被称为 **ICE 候选地址 (ICE Candidates)**。这些地址通常包括：

*   **主机地址 (Host Candidate)**：设备在内网中的真实 IP 地址，例如 `192.168.1.100`。
*   **服务器反射地址 (Server Reflexive Candidate)**：这是设备通过查询公网上的 **STUN 服务器** 获得的、在公网上的“门牌号”（即 NAT 映射后的公网 IP 和端口）。
*   **中继地址 (Relay Candidate)**：如果直接连接失败，作为备选方案，设备会向 **TURN 服务器** 申请一个中继地址。所有数据将通过这个服务器转发。

### 第二步：交换候选信息

A 和 B 无法直接通信，所以他们需要一个“中间人”来传递信息。这个中间人就是 **信令服务器 (Signaling Server)**。

*   信令服务器的作用非常简单，它只负责转发 A 和 B 的 ICE 候选地址列表，本身不参与数据传输。
*   A 将自己收集到的所有候选地址通过信令服务器发送给 B，B 也同样将自己的候选地址列表发给 A。

### 第三步：连通性检查与“打洞”

当 A 和 B 都拿到对方的候选地址列表后，`libnice` 会自动开始 **连通性检查 (Connectivity Checks)**，这个过程也常被称为“打洞”。

1.  **配对尝试**：A 会从自己的候选地址列表中取出一个，再与 B 的候选地址列表中的每一个进行配对，然后向 B 的每个地址发送一个简短的 STUN 请求包。B 也在做同样的事情。
2.  **创建映射**：当 A 向 B 的公网地址发送数据包时，A 所在的路由器（NAT）会记录下这次“外出”，并临时允许来自 B 地址的数据包返回。这就好像在 A 的防火墙上为 B 开了一个临时的“洞”。
3.  **建立连接**：如果 B 发出的数据包恰好也穿过了 B 的“洞”，并成功到达 A，同时 A 的数据包也到达 B，那么一条直接的 P2P 连接就建立成功了。`libnice` 会从所有成功的连接中选择一个最优的路径。

### 第四步：建立连接或启用中继

*   **P2P 直连成功**：如果“打洞”成功，A 和 B 就可以通过这个最优的直接路径开始传输数据（如音视频流）。
*   **P2P 直连失败**：在某些复杂的网络环境下（例如对称型 NAT），直接连接可能无法建立。这时，`libnice` 会自动降级，使用之前收集好的 **中继地址 (Relay Candidate)**。双方将所有数据都发送到 TURN 服务器，由 TURN 服务器进行转发，从而保证通信的可靠性，但这会增加延迟和服务器带宽成本。

总而言之，`libnice` 通过 ICE 协议，自动化地完成了从地址发现、信息交换、路径探测到最终建立最优连接（或启用备用中继）的全过程，从而实现了在复杂网络环境下的 P2P 通信。

## libnice使用STUN功能收集候选人地址

详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-computer-network-principle/demo-libnice-stun.c

安装依赖

```sh
$ sudo apt install libnice-dev
```

编译并运行

```sh
$ gcc -o main demo-libnice-stun.c $(pkg-config --cflags --libs nice)

$ ./main
host    fe80::42:3dff:fe6d:af79:55326
host    fe80::42:3dff:fe6d:af79:0
host    fe80::42:3dff:fe6d:af79:47863
host    fe80::250:56ff:fe9d:26a3:42989
host    fe80::250:56ff:fe9d:26a3:0
host    fe80::250:56ff:fe9d:26a3:44791
host    172.20.0.1:47148
host    172.20.0.1:0
host    172.20.0.1:56977
host    172.20.2.1:35609
host    172.20.2.1:0
host    172.20.2.1:53055
host    172.20.9.1:57774
host    172.20.9.1:0
host    172.20.9.1:48629
host    192.168.1.181:41444
host    192.168.1.181:0
host    192.168.1.181:39117
srflx   14.19.x.x:47148
srflx   14.19.x.x:0
srflx   14.19.x.x:56977
srflx   14.19.x.x:41444
srflx   14.19.x.x:0
srflx   14.19.x.x:39117
srflx   14.19.x.x:57774
srflx   14.19.x.x:0
srflx   14.19.x.x:48629
srflx   14.19.x.x:35609
srflx   14.19.x.x:0
srflx   14.19.x.x:53055
```

## libnice通过websocket交换sdp信令流程？

`libnice` 本身只是一个处理 ICE 协议（网络穿透）的 C 语言库，它并不直接包含 WebSocket 功能。因此，要实现通过 WebSocket 交换 SDP 信令，通常是在你的应用程序中**结合使用 `libnice` 和 WebSocket 库**（如 `libwebsocket`、`libcurl` 或 C++ 的 `websocketpp` 等）。

结合你提到的 `libnice` 和 WebRTC 通用架构，以下是基于 C/C++ 环境下，利用 WebSocket 交换 SDP 信令的标准流程：

### 核心架构图解

在开始流程前，你需要明确数据流向：
*   **本地应用**：运行 `libnice` 逻辑，生成/接收 SDP。
*   **WebSocket 客户端**：嵌入在你的应用中，负责将 SDP 字符串打包成 JSON 发送给信令服务器。
*   **信令服务器**：通常是一个 Node.js (Socket.io/ws)、Python 或 Go 服务，负责“透传”消息（把 A 的消息转发给 B）。

---

### 详细交互流程（以“Offer-Answer”模式为例）

假设 **客户端 A（libnice）** 想要连接 **客户端 B**。

#### 第一阶段：建立 WebSocket 连接
1.  **连接信令服务器**：
    *   客户端 A 和 B 启动后，首先分别建立与信令服务器的 **WebSocket 长连接**。
    *   这一步通常使用 WebSocket 库完成，与 `libnice` 无关。

#### 第二阶段：发起连接与生成 SDP (Offer)
2.  **初始化 libnice (A)**：
    *   A 创建 `NiceAgent`。
    *   A 设置 `libnice` 的回调函数，特别是 `candidate-gathered`（收集到候选项）和 `component-state-changed`。
    *   A 调用 `nice_agent_gather_candidates()` 开始收集本地 IP 和 STUN/TURN 候选项。
3.  **生成 Offer SDP**：
    *   A 的应用层代码根据 `libnice` 收集到的信息（IP、端口、ufrag、pwd）以及媒体信息（H264/OPUS等），组装成一个 **SDP Offer 字符串**。
    *   *注意：`libnice` 只负责提供 ICE 相关的属性（`a=candidate`, `a=ice-ufrag` 等），媒体行（m=）通常需要你自己构造或配合 GStreamer/FFmpeg 生成。*
4.  **通过 WebSocket 发送 Offer**：
    *   A 将 SDP Offer 封装在 JSON 消息中（例如 `{"type": "offer", "sdp": "..."}`）。
    *   A 通过 WebSocket 连接发送给信令服务器，服务器转发给 B。

#### 第三阶段：接收 Offer 与回复 Answer
5.  **接收 Offer (B)**：
    *   B 通过 WebSocket 收到 A 的 Offer 消息。
    *   B 解析 JSON，提取 SDP 字符串。
6.  **初始化 libnice (B) 并设置远端参数**：
    *   B 创建 `NiceAgent`。
    *   B 解析 SDP 中的 `a=candidate` 行，调用 `nice_agent_add_remote_candidates()` 将 A 的候选项告诉 `libnice`。
    *   B 解析 SDP 中的 `a=ice-ufrag` 和 `a=ice-pwd`，调用 `nice_agent_set_remote_credentials()` 设置 A 的认证信息。
7.  **生成 Answer SDP**：
    *   B 同样收集自己的候选项（或复用已有的）。
    *   B 组装 **SDP Answer 字符串**，其中包含 B 的 ICE 候选项和媒体响应。
8.  **通过 WebSocket 发送 Answer**：
    *   B 将 Answer 封装成 JSON（`{"type": "answer", "sdp": "..."}`），通过 WebSocket 发回给 A。

#### 第四阶段：连接建立与 Trickle ICE (可选但推荐)
9.  **A 接收 Answer**：
    *   A 收到 Answer，解析并调用 `nice_agent_set_remote_credentials()` 和 `nice_agent_add_remote_candidates()` 设置 B 的信息。
10. **连通性检查**：
    *   此时，`libnice` 会在后台自动开始 STUN 连通性检查。
    *   一旦连接成功，`libnice` 会触发 `component-state-changed` 信号，状态变为 `NICE_COMPONENT_STATE_READY`。

---

### 关键技术点与代码逻辑

在使用 `libnice` 配合 WebSocket 时，有几个特殊的细节需要注意：

#### 1. 信令消息格式 (JSON)
WebSocket 传输的是文本，通常使用 JSON 格式包裹 SDP。
**示例消息结构：**
```json
{
  "to": "user_B",
  "type": "offer",
  "sdp": "v=0\r\no=- 123456 ...\r\nm=video 9 RTP/SAVPF 96\r\n..."
}
```

#### 2. 处理 Trickle ICE (动态候选项)
虽然可以在 SDP 中一次性包含所有候选项，但 `libnice` 支持 **Trickle ICE**（即连接建立后陆续发现新路径）。
*   **流程**：如果 `libnice` 在连接建立后又发现了新的候选项（例如连上了 TURN 服务器），它会触发 `candidate-gathered` 回调。
*   **动作**：你的代码需要在回调中，立即通过 WebSocket 发送一个单独的 `{"type": "candidate", "candidate": "..."}` 消息给对方，而不是重新发送整个 SDP。
*   **接收端**：收到此类消息后，调用 `nice_agent_add_remote_candidates()` 动态添加。

#### 3. SDP 的构造与解析
`libnice` **不会**自动生成完整的 SDP 字符串，它只提供 ICE 相关的参数。你需要自己处理字符串拼接：
*   **构造**：你需要编写函数，将 `nice_agent_get_local_credentials()` 获取的 ufrag/pwd，以及 `nice_agent_get_selected_candidate_pair()` 获取的 IP/Port，填入 SDP 模板中。
*   **解析**：你需要编写解析器（或使用正则表达式），从接收到的 SDP 字符串中提取 `a=ice-ufrag`、`a=ice-pwd` 和 `a=candidate` 行，以便传给 `libnice` API。

### 总结
`libnice` 通过 WebSocket 交换信令的本质是：
1.  **libnice** 负责算出“我能通过哪些路（IP/端口）到达你”。
2.  **你的代码** 把这些路的信息写成“信（SDP）”。
3.  **WebSocket** 负责把这封“信”寄给信令服务器，再转交给对方。

## libnice生成sdp

>说明：这个示例强制在sdp中删除host和srflx类型的candidate，只保留relay类型的candidate。

详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-computer-network-principle/demo-libnice-sdp.c

运行示例前先运行Coturn服务器：https://gitee.com/dexterleslie/demonstration/tree/main/demo-computer-network-principle/demo-coturn/docker-compose.yaml

```sh
docker compose up -d
```

安装依赖

```sh
$ sudo apt install libnice-dev
```

编译并运行

```sh
$ gcc -o main demo-libnice-sdp.c $(pkg-config --cflags --libs nice)

$ ./main
----- A 端 offer（已去掉 typ host / typ srflx 的 candidate 行）-----
m=audio 49182 ICE/SDP
c=IN IP4 172.20.21.2
a=ice-ufrag:NVZy
a=ice-pwd:2UMC2yabnYTwypNfhlOmgw
a=candidate:31 1 UDP 503316991 172.20.21.2 49182 typ relay raddr 192.168.1.181 rport 60526
----- 结束 -----
```

## libnice交换sdp并建立udp通信

>说明：这个示例使用先收集host、srflx、stun、turn候选ip地址，然后通过Websocket服务两个客户端交换sdp，然后两个客户端使用sdp建立通信。

详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-computer-network-principle/demo-libnice/demo-libnice-exchange-sdp.c

运行示例前先运行Coturn服务器：https://gitee.com/dexterleslie/demonstration/tree/main/demo-computer-network-principle/demo-coturn/docker-compose.yaml

```sh
docker compose up -d
```

运行示例前先运行Websocket服务器：https://gitee.com/dexterleslie/demonstration/tree/main/demo-computer-network-principle/demo-libnice/demo-signal-server.js

```sh
npm install
node demo-signal-server.js
```

安装依赖

```sh
# 在开发机中安装libnice-dev
$ sudo apt install libnice-dev

# 在发布环境中安装libnice
sudo apt install libnice10
```

编译并运行

```sh
$ gcc -o main demo-libnice-exchange-sdp.c $(pkg-config --cflags --libs nice libsoup-2.4)

# 运行offer客户端
$ ./main offer

# 运行answer客户端
$ ./main answer
```

## 
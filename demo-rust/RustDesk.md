## RustDesk工作原理

>官方参考：https://github.com/rustdesk/rustdesk/wiki/How-does-RustDesk-work%3F  
>架构示意图见上述 Wiki 页面中的图片（由 asur4s 绘制，见 [Issue #594](https://github.com/rustdesk/rustdesk/issues/594#issuecomment-1138342668)）。

RustDesk 采用**会合协议（Rendezvous Protocol）**实现远程连接，整体流程如下。

### 核心角色

- **Rendezvous Mediator（会合中介）**  
  各客户端内实现的是会合协议的**客户端侧**，用于与 Rendezvous Server 通信，完成发现与握手，不参与实际桌面数据的转发。  
  参考：[Rendezvous protocol - Wikipedia](https://en.wikipedia.org/wiki/Rendezvous_protocol)

- **Rendezvous Server（会合服务器）**  
  负责客户端发现、ID 解析和连接协调；仅做信令与协调，不转发桌面画面或敏感凭证。

- **Relay Server（中继服务器）**  
  当两台机器无法直连（如 NAT/防火墙限制）时，桌面数据经中继转发；会合服务器与中继服务器角色分离。

### 连接建立流程（对应 Wiki 架构图）

1. **发现与协调**  
   控制端与被控端均通过本机的 Rendezvous Mediator 连接 Rendezvous Server，完成身份/ID 交换与连接参数协商。

2. **尝试直连（NAT 穿透）**  
   在会合服务器协调下，两端尝试建立点对点（P2P）直连；若成功，后续画面与输入数据直接在两台机器之间传输，不经过中继。

3. **中继回退**  
   若直连失败（如对称型 NAT、严格防火墙），则改用 Relay Server 转发桌面数据，保证仍能连接。

4. **凭证与安全**  
   ID/密码等凭证在本地生成与校验；建立连接时，凭证要么直接发给对方（直连），要么经中继加密传输，**不会交给会合服务器**，以降低隐私与泄露风险。

### 小结

| 组件/阶段           | 作用                         |
| ------------------- | ---------------------------- |
| Rendezvous Server   | 发现、ID 解析、连接协调     |
| Rendezvous Mediator | 客户端侧会合协议实现        |
| 直连（P2P）         | 优先路径，无中继带宽占用     |
| Relay Server        | 直连失败时的数据中继         |

因此，会合服务器主要负责“牵线”，实际数据走直连或中继；服务端负载以连接协调和中继带宽为主，对算力要求相对较低。

## API Server是什么呢？

RustDesk API Server 是 RustDesk 专业版（RustDesk Pro）中的一个组件，它主要用于为 RustDesk 提供 Web API 接口，方便第三方系统或内部管理平台与 RustDesk 服务进行集成和自动化管理。

以下是关于 RustDesk API Server 的详细介绍：

一、作用与定位

RustDesk API Server 是一个 RESTful API 服务，运行在独立的 HTTP 服务端口上，通常配合 RustDesk 的 ID 注册服务器（hbbs）和中继服务器（hbbr）一起部署。它允许你通过 HTTP 请求实现以下功能：

- 管理用户账户（创建、删除、修改）
- 管理设备（查看在线状态、绑定/解绑用户）
- 查询连接记录、审计日志
- 控制访问权限、设置策略
- 实现单点登录（SSO）集成
- 与企业内部系统（如 AD、LDAP、工单系统等）对接

二、核心功能

1. 用户与设备管理
   - 创建/删除用户账号
   - 分配设备给特定用户
   - 查询设备在线状态
   - 获取设备列表及详细信息

2. 审计与日志
   - 查询远程连接历史
   - 记录操作日志（如登录、连接、配置变更）
   - 支持导出日志用于合规审计

3. 权限与策略控制
   - 定义用户角色（管理员、普通用户等）
   - 设置访问控制策略（如限制某些 IP 或时间段）
   - 支持多租户隔离（适用于 SaaS 或企业多部门）

4. 集成与自动化
   - 提供标准 REST API，支持 JSON 数据格式
   - 可与现有 IT 管理系统（如 Jira、ServiceNow、Zabbix 等）集成
   - 支持通过脚本或自动化工具调用 API 实现批量操作

5. 安全与认证
   - 支持 Token 或 OAuth2 认证方式
   - 可配置 HTTPS 加密通信
   - 细粒度的 API 权限控制

三、部署方式

- 通常随 RustDesk Pro 套件一起安装
- 可独立部署在公网或内网，通过反向代理（如 Nginx）暴露 API 接口
- 与 hbbs/hbbr 服务配合使用，API 本身不处理远程连接，只做管理控制

四、使用场景示例

- 企业 IT 部门通过 API 自动将新员工的设备加入 RustDesk 管理
- 安全团队通过 API 拉取连接日志，进行行为分析
- 运维平台集成 RustDesk，实现“点击按钮即可远程连接”
- 支持 SSO 登录，用户无需单独注册 RustDesk 账号

五、与 OSS 版的关系

- OSS 版（开源版）不包含 API Server
- 若需使用 API 功能，必须部署 RustDesk Pro 版
- 开源版仅提供基础的中继和 ID 注册服务，无管理接口

六、简单示例（假设已部署并启用 API）

```bash
# 获取所有用户
GET https://your-api-server/api/v1/users
Headers: Authorization: Bearer <your-token>

# 创建新用户
POST https://your-api-server/api/v1/users
Body: { "username": "alice", "password": "123456" }
```

七、总结

| 项目     | 说明                                                 |
| -------- | ---------------------------------------------------- |
| 名称     | RustDesk API Server                                  |
| 所属版本 | 仅限 RustDesk Pro 专业版                             |
| 主要功能 | 提供 REST API 管理用户、设备、连接、权限、日志等     |
| 使用场景 | 企业集成、自动化运维、安全审计、SSO 登录、多系统联动 |
| 是否开源 | 否，闭源组件                                         |
| 依赖服务 | 需与 hbbs（ID 注册）、hbbr（中继）服务配合使用       |

## Ubuntu20.4编译RustDesk

>提示：使用Ubuntu20.4 Server编译RustDesk（在Ubuntu20.4 Desktop中安装编译RustDesk编译的依赖会报告包冲突错误）。

步骤：

1. 在Ubuntu安装vcpkg

2. 安装依赖库

   ```sh
   vcpkg install libvpx libyuv opus aom
   
   sudo apt install libglib2.0-dev
   sudo apt install libgstreamer1.0-dev
   sudo apt install libgstreamer-plugins-base1.0-dev
   sudo apt install libgtk-3-dev
   sudo apt install libssl-dev
   sudo apt install libclang-dev
   sudo apt install libpam0g-dev
   sudo apt install libxcb-randr0-dev
   sudo apt install libpulse-dev
   ```

3. 安装Rust环境

4. 下载RustDesk代码，https://github.com/rustdesk/rustdesk

5. 编译RustDesk

   ```sh
   cd ~/workspace-git/rustdesk
   cargo build
   ```

6. 等待编译完毕会在target目录生成二进制文件

   ```sh
   $ tree target -L 2
   target
   ├── CACHEDIR.TAG
   └── debug
       ├── build
       ├── deps
       ├── examples
       ├── incremental
       ├── liblibrustdesk.a
       ├── liblibrustdesk.d
       ├── liblibrustdesk.rlib
       ├── liblibrustdesk.so
       ├── naming
       ├── naming.d
       ├── rustdesk
       ├── rustdesk.d
       ├── service
       └── service.d
   
   ```


## RustDesk Server OSS和Pro的区别

RustDesk 是一款开源的远程桌面软件，支持自托管服务器。RustDesk 提供了两个版本的服务端程序：OSS（Open Source Software，开源版）和 Pro（专业版）。这两个版本在功能、性能、支持服务等方面存在一定差异，主要如下：

1. 开源性

- OSS 版：
  - 完全开源，代码可在 GitHub 上获取。
  - 可自由部署、修改、二次开发。
  - 社区驱动，无官方商业支持。

- Pro 版：
  - 非完全开源，部分功能为闭源。
  - 由 RustDesk 团队提供商业支持和服务。
  - 适合需要企业级保障的用户。

2. 功能差异

- OSS 版：
  - 提供基本的远程控制功能，如连接、文件传输、远程终端等。
  - 支持自建 ID 注册服务器、中继服务器。
  - 不支持部分高级管理功能，如设备分组、用户权限、审计日志等。

- Pro 版：
  - 包含 OSS 所有功能，并添加企业级功能，如：
    - 多租户支持
    - 用户与权限管理
    - 连接日志与审计
    - 自定义品牌/Logo
    - API 接口支持
    - 高可用部署方案
  - 更适合企业、机构或大规模部署场景。

3. 部署与维护

- OSS 版：
  - 用户自行部署和维护，需具备一定的技术能力。
  - 社区论坛、GitHub Issues 提供支持。

- Pro 版：
  - 提供官方部署指导、技术支持、自动更新服务。
  - 可购买商业支持服务，获得更快的问题响应。

4. 许可证与费用

- OSS 版：
  - 使用 AGPLv3 协议，免费使用。
  - 可自由用于个人或商业用途，但需遵守开源协议。

- Pro 版：
  - 属于商业产品，需付费授权。
  - 提供不同套餐，根据节点数量、功能模块等定价。

5. 适用场景

- OSS 版：
  - 个人用户、小型团队、技术爱好者。
  - 对成本敏感，愿意自行维护系统。

- Pro 版：
  - 中大型企业、教育机构、IT 运维团队。
  - 需要稳定、安全、可管理的远程控制平台。

总结：

| 特性       | OSS 版               | Pro 版                     |
| ---------- | -------------------- | -------------------------- |
| 是否开源   | 是（AGPLv3）         | 否（部分闭源）             |
| 功能完整性 | 基础功能             | 增强功能 + 企业级管理      |
| 技术支持   | 社区支持             | 官方商业支持               |
| 部署难度   | 较高（需自行维护）   | 较低（提供部署工具与支持） |
| 成本       | 免费                 | 收费（按套餐）             |
| 适用对象   | 个人、开发者、小团队 | 企业、组织、大型部署       |

如果你希望完全掌控代码和数据，并且有能力自行部署和维护，OSS 版是不错的选择；如果你需要稳定可靠的企业级功能和技术支持，Pro 版更适合你。

## RustDesk自托管安装

### 服务端安装

>官方参考：https://rustdesk.com/docs/en/self-host/rustdesk-server-oss/docker/#docker-compose-examples

使用Docker Compose安装hbbs和hbbr服务

```yaml
services:
  hbbs:
    container_name: hbbs
    image: rustdesk/rustdesk-server:latest
    command: hbbs
    volumes:
      - ./data:/root
    network_mode: "host"
    depends_on:
      - hbbr
    restart: unless-stopped

  hbbr:
    container_name: hbbr
    image: rustdesk/rustdesk-server:latest
    command: hbbr
    volumes:
      - ./data:/root
    network_mode: "host"
    restart: unless-stopped
```

### Windows11客户端安装和配置

>参考资料：
>
>- https://rustdesk.com/docs/en/self-host/client-configuration/
>- https://rustdesk.com/docs/en/client/

下载Windows版本MSI https://github.com/rustdesk/rustdesk/releases/tag/1.4.6 并根据提示安装

安装完毕后运行客户端，点击设置>网络>ID/中继服务器并填写信息如下：

- ID服务器：填写hbbs服务所在的ip地址
- 中继服务器：留空
- API服务器：留空
- Key：到hbbs服务的控制台中复制。

这样配置后客户端就连接到自托管服务器了。

### Android客户端安装和配置

>提示：如果需要远程控制Android需要授权输入控制权限。

下载ARM64版本的APK https://github.com/rustdesk/rustdesk/releases/tag/1.4.6 并使用mumu模拟器安装

安装完毕后运行客户端，点击设置>ID/中继服务器并填写信息如下：

- ID服务器：填写hbbs服务所在的ip地址
- 中继服务器：留空
- API服务器：留空
- Key：到hbbs服务的控制台中复制。

点击共享屏幕>启动服务以启动RustDesk服务。

这样配置后客户端就连接到自托管服务器了。

### Ubuntu20.4客户端安装

在 https://github.com/rustdesk/rustdesk/releases/tag/1.4.6 下载 Ubuntu20.4 deb安装包。

登录Ubuntu UI界面使用命令安装客户端

```sh
sudo apt install -fy ./rustdesk-1.4.6-x86_64.deb
```

让系统默认用 X11 登录，否则会报告“不支持使用 Wayland 登录界面”错误

1. 打开终端，编辑GDM配置文件/etc/gdm3/custom.conf
2. 找到#WaylandEnable=false取消注释
3. 重新启动操作系统。

安装完毕后运行客户端，点击设置>网络>ID/中继服务器并填写信息如下：

- ID服务器：填写hbbs服务所在的ip地址
- 中继服务器：留空
- API服务器：留空
- Key：到hbbs服务的控制台中复制。

这样配置后客户端就连接到自托管服务器了。

## RustDesk局域网直连

>提示：不需要RustDesk服务端安装。

安装Windows11客户端后，勾选设置>安全>允许IP直接访问，这样就可以支持直接连接。

使用另外一个客户端直接输入IP地址点击连接即可。


# CloudFlare



## Pages



### 介绍

**Cloudflare Pages**是Cloudflare公司推出的一个静态网站托管服务，专为开发者提供构建和部署现代化、高性能网站的解决方案。以下是关于Cloudflare Pages的详细介绍：

**一、核心特点**

1. **与GitHub无缝集成**：
   - 支持自动从GitHub仓库获取代码，并触发构建和部署。
   - 开发者只需将代码推送到GitHub仓库，Cloudflare Pages即可自动处理静态内容的生成、构建和部署。
2. **免费SSL和全球CDN加速**：
   - 提供免费的SSL证书，确保网站的安全性。
   - 利用Cloudflare遍布全球的CDN网络，实现网站的快速分发和访问，提升用户访问速度。
3. **JAMstack架构**：
   - 采用JAMstack（JavaScript、APIs、Markup）架构，兼具静态网站的高性能和动态网站的灵活性。
   - 支持部署各种类型的应用，包括纯静态网站、动态网页应用和API等。
4. **无限带宽和请求**：
   - 所有计划均提供无限站点、席位、请求和带宽，满足各种规模项目的需求。
5. **高级协作功能**：
   - 内置高级协作功能，支持团队成员之间的协作和部署。
   - 为每次提交自动生成唯一、受保护的链接，方便团队成员预览和反馈。
6. **隐私优先的分析工具**：
   - 提供隐私优先的Web分析工具，帮助开发者获取对页面的实时洞察。

**二、使用步骤**

使用Cloudflare Pages创建静态网站的基本步骤如下：

1. **注册Cloudflare帐户**：
   - 前往Cloudflare Pages的官方网站，注册并登录Cloudflare帐户。
2. **创建新项目**：
   - 在Cloudflare Pages的控制台中，点击“创建项目”按钮。
   - 选择GitHub作为代码源，并连接您的GitHub账户。
3. **选择代码仓库**：
   - 在您的GitHub存储库列表中，选择您要部署的存储库。
   - 配置构建脚本和输出目录，设置项目的构建命令和存放构建的页面的目录。
4. **触发构建和部署**：
   - 点击“保存并部署”按钮，Cloudflare Pages将自动从GitHub仓库拉取代码并构建应用程序。
   - 构建完成后，应用程序将被部署到Cloudflare的CDN网络中，并通过指定的域名进行访问。
5. **绑定自定义域名（可选）**：
   - 如果您希望使用自己的域名，可以在域名管理后台添加一条CNAME记录，指向默认的“pages.dev”域名。

**三、定价方案**

Cloudflare Pages提供多种定价方案，以满足不同用户的需求：

- **免费计划**：
  - 提供基本功能和无限带宽，适合个人项目或小型网站。
- **Pro计划（20/月或25/年）**：
  - 提供更多的资源额度和功能，适合专业开发者和团队。
  - 支持5个并发构建，每月5000次构建，每个项目250个自定义域名。
- **Business计划（200/月或250/年）**：
  - 提供企业级高级功能和优先支持。
  - 支持20个并发构建，每月20000次构建，每个项目500个自定义域名。

**四、优势分析**

1. **性能出色**：
   - 利用Cloudflare遍布全球的CDN网络，实现网站的快速分发和访问。
   - 支持HTTP/3、QUIC等最新Web标准，提供更快的加载速度和更好的用户体验。
2. **安全可靠**：
   - 提供免费的SSL证书，确保网站的安全性。
   - 具备DDoS防护、内容安全策略（CSP）等多种安全特性，保护网站免受网络攻击和数据泄露等威胁。
3. **灵活定价**：
   - 提供多种定价方案，满足不同用户的需求和预算。
   - 免费计划即包含无限带宽和请求，对成本敏感的中小项目尤为友好。
4. **易用性强**：
   - 与GitHub无缝集成，支持自动构建和部署，简化开发流程。
   - 提供丰富的设置选项和文档支持，帮助开发者快速上手。

**五、适用场景**

- **个人博客与作品集**：
  - 开发者可以轻松搭建个人博客或作品集网站，展示自己的作品和想法。
- **静态网站与Web应用**：
  - 对于需要快速构建和部署静态网站或Web应用的场景，Cloudflare Pages是一个理想的选择。
- **API与微服务**：
  - 通过与Cloudflare Workers的结合，开发者也可以轻松构建API和微服务，满足各种复杂的应用场景需求。

**六、总结**

Cloudflare Pages是一个功能强大、性能出色、安全可靠的静态网站托管服务。凭借与GitHub的无缝集成、免费SSL和全球CDN加速等核心特点，以及灵活定价和易用性强等优势，Cloudflare Pages成为许多开发者的首选。无论是个人项目还是中小型企业网站，Cloudflare Pages都能提供出色的托管服务。



### 发布 VitePress

提示：

- 每次提交变更到主干中都会自动触发 Pages 自动构建和部署，不需要人工干预。



步骤如下：

1. 访问 `https://dash.cloudflare.com` 登录 Cloudflare 控制台
2. 导航到 Pages 产品后，点击 `Create` 按钮以创建 Application
3. 点击切换到 Pages 标签页，点击 `Connect to Git` 按钮跳转到 GitHub/GitLab 信息配置页面
4. 点击 `Add account` 按钮添加 GitHub 帐号，填写信息如下：
   - GitHub account 选择 `刚刚授权的 GitHub 帐号`
   - Select a repository 选择 `刚刚授权的 GitHub 仓库`
5. 点击 `Begin setup` 按钮跳转到构建和部署设置界面，填写信息如下：
   - Project name 为不需要填写使用 GitHub 仓库名称即可
   - Production branch 为 GitHub 仓库需要构建和部署的分支（Cloudflare 会自动选择主干分支）
   - Framework preset 选择 `VitePress`
   - Build command 使用默认值（Cloudflare 会自动生成）。如果 VitePress 项目配置使用 docs 目录则填写 `npx vitepress build docs` 命令
   - Build output directory 使用默认值（Cloudflare 会自动生成）。如果 VitePress 项目配置使用 docs 目录则填写 `docs/.vitepress/dist`
   - Root directory 不填写。如果 VitePress 项目在 GitHub 仓库中不在根目录中，则需要指定 VitePress 项目在 GitHub 仓库中所在的路径，例如：publisher/vitepress
6. 点击 `Save and Deploy` 按钮会自动构建和部署 VitePress 项目
7. 等自动构建和部署完毕后，访问 `xxx.pages.dev` 即可



### 绑定 CloudFlare 域名到 Pages

默认情况下 Pages 会自动生成 xxx.pages.dev 的域名，但此域名在国内访问不稳定。下面介绍如何申请 CloudFlare 域名并绑定到 Pages 中。

步骤如下：

1. 参考 <a href="/cloudflare/README#申请域名" target="_blank">CloudFlare 申请域名</a> 先申请域名
2. 登录 CloudFlare 控制台并导航到对应的 Pages 项目中，切换到 ` Custom domains` 标签页
3. 点击 `Set up a custom domain` 按钮跳转到自定义域名信息填写页面，填写信息如下：
  - Domain 为 `上面步骤 1 申请到的域名`
4. 点击 `Continue` 按钮跳转到 `Confirm new DNS record` 界面，确认信息无误后点击 `Activate domain` 按钮
5. 等待片刻后以上添加的域名会自动变化为 `Active` 状态。
6. 使用浏览器访问 `https://域名` 即可访问 Pages 发布的内容



### 绑定非 CloudFlare 外部域名到 Pages

默认情况下 Pages 会自动生成 xxx.pages.dev 的域名，但此域名在国内访问不稳定。下面介绍如何申请非 CloudFlare 外部域名并绑定到 Pages 中。

步骤如下：

1. 参考 <a href="/cloudflare/README#添加外部域名到-cloudflare" target="_blank">添加外部域名到 CloudFlare</a> 先添加外部域名到 CloudFlare
7. 登录 CloudFlare 控制台并导航到对应的 Pages 项目中，切换到 ` Custom domains` 标签页
8. 点击 `Set up a custom domain` 按钮跳转到自定义域名信息填写页面，填写信息如下：
   - Domain 为 `上面步骤 1 申请到的域名`
9. 点击 `Continue` 按钮跳转到 `Confirm new DNS record` 界面，确认信息无误后点击 `Activate domain` 按钮
10. 等待片刻后以上添加的域名会自动变化为 `Active` 状态。
11. 使用浏览器访问 `https://域名` 即可访问 Pages 发布的内容



## DNS

### 代理状态（Proxy status）

>[CloudFlare 官方文档解析 Proxy Status](https://developers.cloudflare.com/dns/proxy-status/)

虽然您的 DNS 记录使您的网站或应用程序可供访问者和其他 Web 服务使用，但 DNS 记录的代理状态定义了 Cloudflare 如何处理该记录的传入 DNS 查询。

例子如下：

**example.com** 的 DNS 管理：

| Type | Name   | Content     | Proxy status | TTL  |
| ---- | ------ | ----------- | ------------ | ---- |
| A    | `blog` | `192.0.2.1` | Proxied      | Auto |
| A    | `shop` | `192.0.2.2` | DNS only     | Auto |

在上面的示例 DNS 表中，有两个 DNS 记录。名为 blog 的记录已启用代理，而名为 shop 的记录已禁用代理（即仅 DNS）。

这意味着：

- 对代理记录 blog.example.com 的 DNS 查询将使用 Cloudflare 任播 IP 地址而不是 192.0.2.1 进行应答。这可确保针对此名称的 HTTP/HTTPS 请求将发送到 Cloudflare 的网络并可进行代理，从而实现上述优势。
- 对仅 DNS 记录 shop.example.com 的 DNS 查询将使用实际原始 IP 地址 192.0.2.2 进行应答。除了暴露您的原始 IP 地址并且无法从多项功能中受益之外，Cloudflare 无法对这些请求提供 HTTP/HTTPS 分析（仅提供 DNS 分析）。



## CDN

### 介绍

CloudFlare CDN，即Cloudflare提供的内容分发网络（Content Delivery Network）服务，是一种基于反向代理的技术，旨在优化网站的请求处理机制，提升访问速度和可靠性。以下是对CloudFlare CDN的详细介绍：

一、定义与工作原理

CloudFlare CDN通过在全球多个数据中心分布服务器，使用户能够从最近的服务器获取网站内容。这种分布式架构有助于减少网络延迟，提高内容传输速度。当用户请求访问某个网站时，CloudFlare的CDN会首先接收请求，并根据用户的位置和请求的内容，从最近的节点提供缓存的内容或回源服务器获取内容后缓存并提供给用户。

二、功能与优势

1. **加速网站访问**：CloudFlare CDN通过缓存静态和动态内容，减少了对源服务器的请求，从而加快了网站的加载速度。
2. **提高安全性**：CloudFlare提供了包括SSL/TLS加密、DDoS保护、WAF（Web应用防火墙）以及针对SQL注入、XSS等常见攻击的防护在内的安全防护功能，有效保护网站免受恶意攻击和数据泄露的风险。
3. **优化性能**：CloudFlare CDN能够智能地根据设备、浏览器和带宽需求优化内容传输，提供更佳的用户体验。
4. **降低成本**：通过减少对源服务器的请求和最小化带宽消耗，CloudFlare CDN有助于降低网站的托管费用。
5. **易于配置和使用**：CloudFlare提供了简单直观的界面和丰富的配置选项，使得即使是非技术背景的用户也能轻松上手。

三、免费与付费服务

CloudFlare提供了免费的CDN服务，这对于预算有限的小型网站或初创企业来说是一个巨大的福音。尽管是免费的，但其功能并不逊色，包括上述的加速、安全防护和优化性能等功能。然而，对于需要更高性能、更多功能和更专业支持的用户来说，CloudFlare也提供了多种付费计划供选择。

四、使用步骤与注意事项

1. **注册与登录**：访问[CloudFlare官网](https://www.cloudflare.com/)，注册一个账号并登录到管理后台。
2. **添加站点**：在管理后台中点击“添加站点”按钮，并输入想要加速的网站的域名。CloudFlare会自动扫描该域名的DNS记录，并将其导入到系统中。
3. **选择套餐计划**：CloudFlare提供了多种套餐计划，包括免费的计划。对于大多数小型网站或初创企业来说，选择免费计划即可满足需求。
4. **更改DNS服务器**：为了将网站接入CloudFlare的CDN服务，需要将域名的DNS服务器更改为CloudFlare提供的DNS服务器。这通常涉及到在域名注册商处修改DNS设置。
5. **配置CDN设置**：一旦DNS服务器更改生效，就可以开始配置CloudFlare的CDN设置了。这包括开启HTTPS、配置缓存规则、设置防火墙规则等。CloudFlare提供了详细的配置指南和帮助文档，可以帮助用户顺利完成这些设置。
6. **验证效果**：完成所有设置后，可以使用站长工具或Ping命令来测试网站是否已经成功接入CloudFlare的CDN服务，并观察网站的加载速度是否有所提升。

在使用CloudFlare CDN时，需要注意以下几点：

- 对于国内域名，若网站主要访问来自国内，使用国外的CDN可能并非必要。但若网站需频繁接收国外访问，则非常推荐使用CloudFlare CDN，且付费版本可能效果更佳。
- CloudFlare的WAF规则相当灵活自由和强大，因此需要经常性地根据自己站点的情况调整WAF规则，以减小误拦截。

综上所述，CloudFlare CDN是一种功能强大且易于使用的服务，能够帮助网站提升访问速度、增强安全性并提供详细的性能分析报告。无论是对于小型网站还是大型网站来说，都是一个值得考虑的选择。



### 为站点配置 CDN

步骤如下：

1. 参考 <a href="/cloudflare/README#添加外部域名到-cloudflare" target="_blank">添加外部域名到 CloudFlare</a> 先添加外部域名到 CloudFlare
2. 如果使用 CloudFlare 域名则参考 <a href="/cloudflare/README#申请域名" target="_blank">CloudFlare 申请域名</a> 先申请域名
3. 在 DNS records 中添加类型为 A 的 DNS 记录，信息如下：
   - Type 为 A
   - Name 为子域名或者原来的域名，例如：子域名 www.xxx.com、或者 xxx.com
   - Content 为源站点的 IP 地址
   - Proxy status 为启用状态，关于 Proxy Status 的用法请参考 <a href="/cloudflare/README.html#代理状态-proxy-status" target="_blank">链接</a>
   - TTL 为 Auto
4. 如果源站点为非 80 端口，可以通过新增 Rules > Origin Rules 对目标端口重写，Origin Rules 信息如下：
   - Rule name 填写 port-rewriting
   - If incoming request match ... 选择 All incoming requests
   - Destination port 选择 Rewrite to ... 并填写对应的端口
5. 点击 `Deploy` 按钮 Origin Rules 即可生效
6. 通过 `https://www.xxx.com` 访问 CDN 服务。



## Domain Registration

### 申请域名

步骤如下：

1. 访问 `https://dash.cloudflare.com` 登录 Cloudflare 控制台
2. 导航到 `Domain Registration` > `Register Domains` 功能，填写信息如下：
   - Search for a domain name 填写 `将要申请的域名`
3. 点击 `Search` 按钮搜索域名的价格和相关推荐域名的价格
4. 选中其中需要购买的域名点击 `Confirm` 按钮
5. 填写域名需要绑定的相关信息和信用卡信息后点击 `Complete purchase` 按钮购买即可。



### 管理域名

>[CloudFlare 域名管理官方文档](https://developers.cloudflare.com/registrar/account-options/domain-management/)



### 添加外部域名到 CloudFlare

步骤如下：

1. 登录 `https://namecheap.com` 并申请域名
2. 登录 CloudFlare 控制台并导航到 `Account Home` 功能，点击 `Add a domain` 按钮跳转到域名信息界面，填写信息如下：
   - Enter an existing domain 为 `需要新增的外部域名`
   - 选择 Quick scan for DNS records 选项 CloudFlare 会自动导入域名已存在的 DNS 记录。
3. 点击 `Continue` 按钮跳转到 Select a plan for `外部域名` 并选择 Free 计划后点击 `Continue` 按钮跳转到 `Review your DNS records` 界面
4. 在外部域名提供商的控制台中填写 CloudFlare 提供的两个 NameServers（让外部域名 NS 记录指向 CloudFlare NameServers），如：xxx.ns.cloudflare.com
5. 上面步骤填写完毕后不需要修改 DNS records 任何信息点击 `Continue to activation` 按钮
6. 稍等 30 分钟左右新增的外部域名会自动变为 Active 状态。

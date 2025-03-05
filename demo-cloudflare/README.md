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

- 参考 [CloudFlare 申请域名]() 先申请域名
- 登录 CloudFlare 控制台并导航到对应的 Pages 项目中，切换到 ` Custom domains` 标签页
- 点击 `Set up a custom domain` 按钮跳转到自定义域名信息填写页面，填写信息如下：
  - Domain 为 `上面步骤 1 申请到的域名`
- 点击 `Continue` 按钮跳转到 `Confirm new DNS record` 界面，确认信息无误后点击 `Activate domain` 按钮
- 等待片刻后以上添加的域名会自动变化为 `Active` 状态。



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

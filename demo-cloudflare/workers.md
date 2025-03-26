# `Workers`



## 使用 `cli` 创建和发布 `Workers`

>[官方文档参考](https://developers.cloudflare.com/workers/get-started/guide/)

参考 <a href="/dcli/README.html#安装" target="_blank">链接</a> 安装 `dcli`

使用 `dcli` 安装 `nodejs v20.12.2`

```bash
sudo dcli nodejs install
```

设置 `registry` 为 `https://registry.npmmirror.com`

```bash
npm config set registry https://registry.npmmirror.com
```

使用 `create-cloudflare-cli` 创建 `Woker` 项目

```bash
npm create cloudflare@latest -- my-first-worker
```

创建 `worker` 项目的配置信息如下：

- 对于您想从哪里开始？，选择 Hello World 示例。 
- 对于您想使用哪个模板？，选择 Hello World Worker。 
- 对于您想使用哪种语言？，选择 JavaScript。 
- 对于您想使用 git 进行版本控制？，选择 否。 
- 对于您想部署您的应用程序？，选择否（我们将在部署之前进行一些更改）。

切换到 `my-first-worker` 目录

```bash
cd my-first-worker
```

`create-cloudflare-cli` 默认在 `Workers` 项目中安装 `Workers` 命令行界面 `Wrangler`。`Wrangler `可让您创建、测试和部署 `Workers` 项目。

创建第一个 `Worker` 后，在项目目录中运行 `wrangler dev` 命令以启动本地服务器来开发 `Worker`。这将允许您在开发过程中在本地预览 `Worker`。

```bash
npx wrangler dev
```

- 注意：Ubuntu20.04 中上面命令报错（因为新版本的 workerd 依赖于 GLIBC_2.32，但 Ubuntu20.04 不提供），解决办法如下：

  >[参考链接](https://github.com/cloudflare/workerd/issues/3411)

  使用容器运行 Worker 本地开发环境

  ```bash
  docker run --network host -it --rm -v $(pwd):/app -w /app node:20 bash
  ```

  在容器内启动 Worker

  ```bash
  npx wrangler dev
  ```

如果您以前从未使用过 `Wrangler`，它将打开您的网络浏览器，以便您登录到您的 `Cloudflare` 帐户。

转到 `http://localhost:8787` 查看您的 `Worker`。

通过 `Wrangler` 部署你的 `Worker`

```bash
npx wrangler deploy
```



## `Service Worker` 和 `ES` 语法编写 `Worker`

>[migrate a worker](https://developers.cloudflare.com/workers/reference/migrate-to-module-workers/#migrate-a-worker)
>
>[JavaScript modules are now supported on Cloudflare Workers](https://blog.cloudflare.com/workers-javascript-modules/)
>
>[Service Worker API](https://developer.mozilla.org/en-US/docs/Web/API/Service_Worker_API)

### 使用 `Service Worker` 语法

```javascript
async function handler(request) {
  return new Response("Hello world!");
}

// Initialize Worker
addEventListener('fetch', event => {
  event.respondWith(handler(event.request));
});
```



### 使用 `ES` 语法

```javascript
export default {
	fetch(request) {
		return new Response("Hello world!!")
	},
};
```


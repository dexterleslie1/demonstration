## 设置全局自签名`ssl`证书不验证合法性

> [Unable to clone Git repository due to self signed certificate](https://confluence.atlassian.com/fishkb/unable-to-clone-git-repository-due-to-self-signed-certificate-376838977.html)

```shell
git config --global http.sslVerify false
```


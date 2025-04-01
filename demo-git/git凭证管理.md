# `git`凭证管理

## 设置自动保存`git`凭证

```bash
git config --global credential.helper store
```

查看是否设置成功

```bash
git config --global credential.helper
```



## ubuntu 删除本地存储的 git 凭证

查看当前凭证提供程序，如果输出 git-credential-store 表示使用了`git-credential-store`，凭证被永久存储在`~/.git-credentials`文件中。

```sh
git config --global credential.helper
```

使用`git-credential-store`，凭证通常存储在`~/.git-credentials`文件中。你可以通过删除该文件来删除所有存储的凭证。

```sh
rm ~/.git-credentials
```


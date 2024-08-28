# `ubuntu`启用`ssh`服务

>[`ubuntu`开启`SSH`服务，并允许`ROOT`权限远程登录](https://blog.csdn.net/jinghongluexia/article/details/90031842)

- 安装`ssh`服务器组件

  ```bash
  sudo apt-get install ssh
  ```

- 修改`/etc/ssh/sshd_config`配置允许`root`远程登录

  ```nginx
  PermitRootLogin yes
  PasswordAuthentication yes
  ```

- 设置`root`密码

  ```bash
  passwd root
  ```

- 启动`ssh`服务

  ```bash
  sudo systemctl start ssh && sudo systemctl enable ssh
  ```

  
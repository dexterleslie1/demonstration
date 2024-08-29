# `zsh`使用

## `zsh`安装

### `ubuntu`安装`zsh`

- 安装`zsh shell`

  ```bash
  sudo apt install zsh
  ```

- 设置`zsh shell`为默认`shell`，其中`-s --shell`用于指定`shell`路径

  ```bash
  chsh -s /bin/zsh
  ```

- 重启系统加载`zsh shell`



## `oh my zsh`使用

>[`oh my zsh`官网](https://ohmyz.sh/)

### 什么是`oh my zsh`呢？

"Oh my zsh" 是一个流行的、开源的、社区驱动的框架，用于管理和配置 Zsh（Z Shell），这是一种在 Unix 和类 Unix 系统（如 macOS 和 Linux）上广泛使用的命令行界面。Oh My Zsh 通过提供丰富的主题、插件以及自动补全等功能，使得 Zsh 的使用体验更加友好和高效。

Oh My Zsh 的特点包括：

1. **丰富的主题**：Oh My Zsh 提供了多种预定义的提示符（prompt）主题，允许用户根据自己的喜好自定义命令行界面的外观。
2. **插件系统**：通过插件，Oh My Zsh 增强了 Zsh 的功能。这些插件可以自动完成、历史记录管理、拼写检查、目录浏览等。
3. **自动补全**：增强了命令和参数的自动补全功能，使得输入命令更加快捷。
4. **社区支持**：作为一个开源项目，Oh My Zsh 拥有庞大的社区支持，用户可以通过 GitHub 仓库提交问题、贡献插件或主题。
5. **易于安装和更新**：通过简单的命令即可安装和更新 Oh My Zsh，无需复杂的配置。

### `oh my zsh`原理

`oh my zsh`通过安装脚本把`~/.zshrc`文件替换为`oh my zsh`定制的`.zshrc`文件，其中`oh my zsh`定制的`.zshrc`文件会指向`~/.oh-my-zsh`目录，目录里面包含`oh my zsh`定制的插件、主题等组件。

### 安装`zsh-completions`插件

1. **克隆仓库到 Oh My Zsh 的插件目录**：

   打开你的终端，并使用以下命令将 `zsh-completions` 插件的仓库克隆到你的 Oh My Zsh 插件目录中。这通常位于 `~/.oh-my-zsh/custom/plugins` 目录下，但如果你还没有自定义插件目录，Oh My Zsh 会自动处理它。

   ```bash
   git clone https://github.com/zsh-users/zsh-completions ${ZSH_CUSTOM:-~/.oh-my-zsh/custom}/plugins/zsh-completions
   ```

   注意：`${ZSH_CUSTOM:-~/.oh-my-zsh/custom}` 是一个参数扩展，用于确保如果 `ZSH_CUSTOM` 环境变量未设置，则使用默认路径 `~/.oh-my-zsh/custom`。

2. **在 `.zshrc` 文件中启用插件**：

   编辑你的 `~/.zshrc` 文件，找到 `plugins=` 这一行，并添加 `zsh-completions` 到插件列表中（如果你还没有插件列表，就创建一个）。例如：

   ```bash
   plugins=(git zsh-completions zsh-syntax-highlighting)
   ```

   注意：这里我也添加了 `zsh-syntax-highlighting` 插件，因为我们接下来会安装它。

3. **重新加载配置**：

   每次修改 `.zshrc` 文件后，你都需要重新加载你的 Zsh 配置以使更改生效。这可以通过运行 `source ~/.zshrc` 或简单地重新打开一个新的终端窗口来完成。

### 安装`zsh-autosuggestions`插件

1. **克隆仓库到 Oh My Zsh 的插件目录**：

   打开你的终端，并使用以下命令将 `zsh-autosuggestions` 插件的仓库克隆到你的 Oh My Zsh 插件目录中。这通常位于 `~/.oh-my-zsh/custom/plugins` 目录下，但如果你还没有自定义插件目录，Oh My Zsh 会自动处理它。

   ```bash
   git clone https://github.com/zsh-users/zsh-autosuggestions ${ZSH_CUSTOM:-~/.oh-my-zsh/custom}/plugins/zsh-autosuggestions
   ```

   注意：`${ZSH_CUSTOM:-~/.oh-my-zsh/custom}` 是一个参数扩展，用于确保如果 `ZSH_CUSTOM` 环境变量未设置，则使用默认路径 `~/.oh-my-zsh/custom`。

2. **在 `.zshrc` 文件中启用插件**：

   编辑你的 `~/.zshrc` 文件，找到 `plugins=` 这一行，并添加 `zsh-autosuggestions` 到插件列表中（如果你还没有插件列表，就创建一个）。例如：

   ```bash
   plugins=(git zsh-autosuggestions zsh-syntax-highlighting)
   ```

   注意：这里我也添加了 `zsh-syntax-highlighting` 插件，因为我们接下来会安装它。

3. **重新加载配置**：

   每次修改 `.zshrc` 文件后，你都需要重新加载你的 Zsh 配置以使更改生效。这可以通过运行 `source ~/.zshrc` 或简单地重新打开一个新的终端窗口来完成。

### 安装`zsh-syntax-highlighting`插件

安装 `zsh-syntax-highlighting` 插件的过程与安装 `zsh-autosuggestions` 插件类似。

1. **克隆仓库到 Oh My Zsh 的插件目录**：

   ```bash
   git clone https://github.com/zsh-users/zsh-syntax-highlighting.git ${ZSH_CUSTOM:-~/.oh-my-zsh/custom}/plugins/zsh-syntax-highlighting
   ```

2. **（可选）安装依赖**：

   对于某些功能（如异步高亮），`zsh-syntax-highlighting` 可能需要额外的依赖。请查阅其 GitHub 仓库的文档以获取更多信息。

3. **确保在 `.zshrc` 中启用了插件**：

   如之前所述，确保 `zsh-syntax-highlighting` 已经被添加到你的插件列表中。

4. **重新加载配置**：

   通过运行 `source ~/.zshrc` 或重新打开一个新的终端窗口来重新加载你的 Zsh 配置。

### 安装`incr`插件

>https://www.cnblogs.com/ShawnChan/p/11723626.html

- 下载`incr-0.2.zsh`

  ```bash
  wget -O incr-0.2.zsh https://mimosa-pudica.net/src/incr-0.2.zsh
  ```

- 创建`incr`插件目录

  ```bash
  mkdir -p .oh-my-zsh/custom/plugins/incr
  ```

- 复制`incr-0.2.zsh`文件到`plugins`

  ```bash
  cp incr-0.2.zsh .oh-my-zsh/custom/plugins/incr/incr.plugin.zsh
  ```

- 编辑你的 `~/.zshrc` 文件，找到 `plugins=` 这一行，并添加 `incr` 到插件列表中（如果你还没有插件列表，就创建一个）。例如：

  ```bash
  plugins=(git zsh-completions zsh-syntax-highlighting incr)
  ```

  

### 修改主题

>[`oh my zsh`支持的主题列表](https://github.com/ohmyzsh/ohmyzsh/wiki/Themes)

- 编辑`~/.zshrc`文件把`ZSH_THEME`修改为下面主题

  ```bash
  ZSH_THEME="blinks"
  ```



### 安装自定义`oh my zsh`

```bash
# ubuntu安装
sudo apt install zsh -y && sudo apt install git -y && sh -c "$(curl -fsSL https://gitee.com/dexterleslie/oh-my-zsh/raw/master/install.sh)"

# centOS8安装
yum install zsh -y && yum install git -y && sh -c "$(curl -fsSL https://gitee.com/dexterleslie/oh-my-zsh/raw/master/install.sh)"
```




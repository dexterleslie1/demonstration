## pip使用

> 什么是pip
> https://www.jianshu.com/p/66d85c06238c
> pip是一个以Python计算机程序语言写成的软件包管理系统，他可以安装和管理软件包，另外不少的软件包也可以在“Python软件包索引”中找到。



### 基础使用

```
# 安装和卸载pip
# https://www.jianshu.com/p/66d85c06238c
# 安装pip
sudo easy_install pip
# 或者
# centOS8安装python-pip
# https://phoenixnap.com/kb/how-to-install-pip-on-centos-8
yum install epel-release

# 安装python-pip
yum install python-pip

安装python2-pip
yum -y install python2-pip

安装python3-pip
yum -y install python3-pip

pip --version

# 卸载pip
sudo pip uninstall pip

# 使用pip安装fire包
pip install fire

# 安装指定版本selenium
# https://stackoverflow.com/questions/5226311/installing-specific-package-version-with-pip
pip install selenium==4.0.0

# 查看当前安装的fire包版本
pip show fire

# 升级fire包到最新版本
pip install --upgrade fire

# 列出指定包远程所有版本
pip install click==

# 列出指定包所有版本
https://stackoverflow.com/questions/4888027/python-and-pip-list-all-versions-of-a-package-thats-available
# NOTE: 以下命令在pip3时报错不能使用，使用pip show替代
# pip index versions fire
# https://stackoverflow.com/questions/10214827/find-which-version-of-package-is-installed-with-pip
pip show fire

# 卸载selenium
pip uninstall selenium
```



### pip安装时指定国内源

```shell
# 参考 https://developer.aliyun.com/article/652884

# pip3 install指定阿里源
pip3 install fire -i https://mirrors.aliyun.com/pypi/simple/
```



### pip使用socks5

> https://stackoverflow.com/questions/22915705/how-to-use-pip-with-socks-proxy

```
# 安装socks5依赖，否则会报告缺失socks5依赖错误
sudo pip3 install pysocks

# pip install时候使用socks5代理
sudo pip3 install locust --proxy socks5://xxx:1080
```



### pip3 install 报告错误

#### sudo pip3 install selenium==4.0.0 时错误

错误信息如下：

```bash
Traceback (most recent call last):
  File "/usr/bin/pip3", line 11, in <module>
    load_entry_point('pip==20.0.2', 'console_scripts', 'pip3')()
  File "/usr/lib/python3/dist-packages/pkg_resources/__init__.py", line 490, in load_entry_point
    return get_distribution(dist).load_entry_point(group, name)
  File "/usr/lib/python3/dist-packages/pkg_resources/__init__.py", line 2854, in load_entry_point
    return ep.load()
  File "/usr/lib/python3/dist-packages/pkg_resources/__init__.py", line 2445, in load
    return self.resolve()
  File "/usr/lib/python3/dist-packages/pkg_resources/__init__.py", line 2451, in resolve
    module = __import__(self.module_name, fromlist=['__name__'], level=0)
  File "/usr/lib/python3/dist-packages/pip/_internal/cli/main.py", line 10, in <module>
    from pip._internal.cli.autocompletion import autocomplete
  File "/usr/lib/python3/dist-packages/pip/_internal/cli/autocompletion.py", line 9, in <module>
    from pip._internal.cli.main_parser import create_main_parser
  File "/usr/lib/python3/dist-packages/pip/_internal/cli/main_parser.py", line 7, in <module>
    from pip._internal.cli import cmdoptions
  File "/usr/lib/python3/dist-packages/pip/_internal/cli/cmdoptions.py", line 24, in <module>
    from pip._internal.exceptions import CommandError
  File "/usr/lib/python3/dist-packages/pip/_internal/exceptions.py", line 10, in <module>
    from pip._vendor.six import iteritems
  File "/usr/lib/python3/dist-packages/pip/_vendor/__init__.py", line 65, in <module>
    vendored("cachecontrol")
  File "/usr/lib/python3/dist-packages/pip/_vendor/__init__.py", line 36, in vendored
    __import__(modulename, globals(), locals(), level=0)
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/CacheControl-0.12.6-py2.py3-none-any.whl/cachecontrol/__init__.py", line 9, in <module>
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/CacheControl-0.12.6-py2.py3-none-any.whl/cachecontrol/wrapper.py", line 1, in <module>
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/CacheControl-0.12.6-py2.py3-none-any.whl/cachecontrol/adapter.py", line 5, in <module>
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/requests-2.22.0-py2.py3-none-any.whl/requests/__init__.py", line 95, in <module>
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/urllib3-1.25.8-py2.py3-none-any.whl/urllib3/contrib/pyopenssl.py", line 46, in <module>
  File "/usr/local/lib/python3.8/dist-packages/OpenSSL/__init__.py", line 8, in <module>
    from OpenSSL import SSL, crypto
  File "/usr/local/lib/python3.8/dist-packages/OpenSSL/SSL.py", line 42, in <module>
    from OpenSSL.crypto import (
  File "/usr/local/lib/python3.8/dist-packages/OpenSSL/crypto.py", line 550, in <module>
    utils.deprecated(
TypeError: deprecated() got an unexpected keyword argument 'name'
Error in sys.excepthook:
Traceback (most recent call last):
  File "/usr/lib/python3/dist-packages/apport_python_hook.py", line 72, in apport_excepthook
    from apport.fileutils import likely_packaged, get_recent_crashes
  File "/usr/lib/python3/dist-packages/apport/__init__.py", line 5, in <module>
    from apport.report import Report
  File "/usr/lib/python3/dist-packages/apport/report.py", line 32, in <module>
    import apport.fileutils
  File "/usr/lib/python3/dist-packages/apport/fileutils.py", line 12, in <module>
    import os, glob, subprocess, os.path, time, pwd, sys, requests_unixsocket
  File "/usr/lib/python3/dist-packages/requests_unixsocket/__init__.py", line 1, in <module>
    import requests
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/requests-2.22.0-py2.py3-none-any.whl/requests/__init__.py", line 95, in <module>
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/urllib3-1.25.8-py2.py3-none-any.whl/urllib3/contrib/pyopenssl.py", line 46, in <module>
  File "/usr/local/lib/python3.8/dist-packages/OpenSSL/__init__.py", line 8, in <module>
    from OpenSSL import SSL, crypto
  File "/usr/local/lib/python3.8/dist-packages/OpenSSL/SSL.py", line 42, in <module>
    from OpenSSL.crypto import (
  File "/usr/local/lib/python3.8/dist-packages/OpenSSL/crypto.py", line 550, in <module>
    utils.deprecated(
TypeError: deprecated() got an unexpected keyword argument 'name'

Original exception was:
Traceback (most recent call last):
  File "/usr/bin/pip3", line 11, in <module>
    load_entry_point('pip==20.0.2', 'console_scripts', 'pip3')()
  File "/usr/lib/python3/dist-packages/pkg_resources/__init__.py", line 490, in load_entry_point
    return get_distribution(dist).load_entry_point(group, name)
  File "/usr/lib/python3/dist-packages/pkg_resources/__init__.py", line 2854, in load_entry_point
    return ep.load()
  File "/usr/lib/python3/dist-packages/pkg_resources/__init__.py", line 2445, in load
    return self.resolve()
  File "/usr/lib/python3/dist-packages/pkg_resources/__init__.py", line 2451, in resolve
    module = __import__(self.module_name, fromlist=['__name__'], level=0)
  File "/usr/lib/python3/dist-packages/pip/_internal/cli/main.py", line 10, in <module>
    from pip._internal.cli.autocompletion import autocomplete
  File "/usr/lib/python3/dist-packages/pip/_internal/cli/autocompletion.py", line 9, in <module>
    from pip._internal.cli.main_parser import create_main_parser
  File "/usr/lib/python3/dist-packages/pip/_internal/cli/main_parser.py", line 7, in <module>
    from pip._internal.cli import cmdoptions
  File "/usr/lib/python3/dist-packages/pip/_internal/cli/cmdoptions.py", line 24, in <module>
    from pip._internal.exceptions import CommandError
  File "/usr/lib/python3/dist-packages/pip/_internal/exceptions.py", line 10, in <module>
    from pip._vendor.six import iteritems
  File "/usr/lib/python3/dist-packages/pip/_vendor/__init__.py", line 65, in <module>
    vendored("cachecontrol")
  File "/usr/lib/python3/dist-packages/pip/_vendor/__init__.py", line 36, in vendored
    __import__(modulename, globals(), locals(), level=0)
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/CacheControl-0.12.6-py2.py3-none-any.whl/cachecontrol/__init__.py", line 9, in <module>
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/CacheControl-0.12.6-py2.py3-none-any.whl/cachecontrol/wrapper.py", line 1, in <module>
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/CacheControl-0.12.6-py2.py3-none-any.whl/cachecontrol/adapter.py", line 5, in <module>
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/requests-2.22.0-py2.py3-none-any.whl/requests/__init__.py", line 95, in <module>
  File "<frozen importlib._bootstrap>", line 991, in _find_and_load
  File "<frozen importlib._bootstrap>", line 975, in _find_and_load_unlocked
  File "<frozen importlib._bootstrap>", line 655, in _load_unlocked
  File "<frozen importlib._bootstrap>", line 618, in _load_backward_compatible
  File "<frozen zipimport>", line 259, in load_module
  File "/usr/share/python-wheels/urllib3-1.25.8-py2.py3-none-any.whl/urllib3/contrib/pyopenssl.py", line 46, in <module>
  File "/usr/local/lib/python3.8/dist-packages/OpenSSL/__init__.py", line 8, in <module>
    from OpenSSL import SSL, crypto
  File "/usr/local/lib/python3.8/dist-packages/OpenSSL/SSL.py", line 42, in <module>
    from OpenSSL.crypto import (
  File "/usr/local/lib/python3.8/dist-packages/OpenSSL/crypto.py", line 550, in <module>
    utils.deprecated(
TypeError: deprecated() got an unexpected keyword argument 'name'
--------------------------------------------------------------------
```

解决办法，`https://blog.csdn.net/sophiasofia/article/details/138315191`

```bash
# 删除 OpenSSL
rm -rf /usr/local/lib/python3.8/dist-packages/OpenSSL
```

## uv概念

`uv`是由 Astral 团队（开发了 Ruff 代码检查器的团队）开发的**高性能 Python 工具链**，目标是用 Rust 重写并整合传统 Python 开发中的分散工具（如 `pip`、`virtualenv`、`pipx`、`poetry`的部分功能），解决 Python 生态中“速度慢、工具碎片化”的痛点。

### 一、`uv`的核心定位

`uv`不是单一工具，而是一个**一体化的 Python 开发工具链**，覆盖从“依赖管理”到“虚拟环境”再到“包运行”的全流程，核心特点是：

- 

  **极致速度**：Rust 编写，依赖解析、下载、安装速度比 `pip`快 10-100 倍；

- 

  **功能整合**：替代 `pip`（包安装）、`virtualenv`（虚拟环境）、`pipx`（临时包运行）、`poetry`（部分项目管理）等多个工具；

- 

  **兼容性**：完全兼容 `pip`的 `requirements.txt`和 `pyproject.toml`，迁移成本低；

- 

  **跨平台**：支持 macOS、Linux、Windows。

### 二、`uv`的核心命令

`uv`的命令设计简洁，核心围绕“虚拟环境、依赖管理、包运行”三大场景：

| 命令       | 作用                                                         |
| ---------- | ------------------------------------------------------------ |
| `uv venv`  | 创建/管理虚拟环境（替代 `virtualenv`/`python -m venv`）      |
| `uv pip`   | 安装/卸载/列出 Python 包（替代 `pip`，速度更快）             |
| `uv run`   | 在虚拟环境中运行命令（替代 `source venv/bin/activate && command`） |
| `uvx`      | 临时运行 Python 包（无需安装，替代 `pipx`）                  |
| `uv lock`  | 生成/更新 `uv.lock`锁文件（固定依赖版本，替代 `poetry lock`） |
| `uv tree`  | 查看依赖树（替代 `pip tree`）                                |
| `uv cache` | 管理缓存（清理/查看下载的包缓存）                            |

### 三、关键命令详解与示例

#### 1. `uv venv`：创建虚拟环境

虚拟环境是 Python 开发的基础（隔离不同项目的依赖）。`uv venv`比传统的 `python -m venv`更快：

```
# 创建名为 .venv 的虚拟环境（默认在当前目录）
uv venv

# 指定 Python 版本（需系统已安装对应版本）
uv venv --python 3.11

# 自定义虚拟环境目录
uv venv ./my-venv
```

激活虚拟环境：

- 

  macOS/Linux：`source .venv/bin/activate`

- 

  Windows：`.venv\Scripts\activate`

#### 2. `uv pip`：高速安装依赖

`uv pip`完全兼容 `pip`的语法，但速度极快（依赖 Rust 实现的并行下载和解析）：

```
# 安装单个包（如 requests）
uv pip install requests

# 从 requirements.txt 安装
uv pip install -r requirements.txt

# 安装开发依赖（可结合 pyproject.toml）
uv pip install -e .[dev]  # 安装项目本身及 dev 分组依赖

# 卸载包
uv pip uninstall requests

# 列出已安装包
uv pip list
```

#### 3. `uv run`：在虚拟环境中运行命令

无需手动激活虚拟环境，直接用 `uv run`调用环境中的命令（自动识别项目根目录的虚拟环境）：

```
# 运行虚拟环境中的 Python 脚本
uv run python main.py

# 运行虚拟环境中的工具（如 pytest 测试）
uv run pytest tests/

# 指定其他虚拟环境目录
uv run --venv ./my-venv python main.py
```

#### 4. `uvx`：临时运行 Python 包（重点）

前面已经介绍过，`uvx`用于**临时下载并运行 Python 工具，无需永久安装**（类似 `pipx`，但更快）：

```
# 临时用 httpie 发请求
uvx httpie GET https://api.github.com

# 临时用 black 格式化代码
uvx black .

# 指定版本运行
uvx poetry@1.8.2 init
```

#### 5. `uv lock`：锁定依赖版本

生成 `uv.lock`文件（类似 `poetry.lock`或 `Pipfile.lock`），确保团队成员/部署环境的依赖版本完全一致：

```
# 生成/更新锁文件（基于 pyproject.toml）
uv lock

# 根据锁文件安装依赖（确保精确复现）
uv pip install -r uv.lock
```

### 四、`uv`的优势：为什么用它？

对比传统 Python 工具链，`uv`解决了三个核心痛点：

1. 

   **速度慢**：`pip`的依赖解析是 Python 写的，面对复杂依赖树时很慢；`uv`用 Rust 重写，并行处理，速度提升一个量级。

2. 

   **工具碎片化**：以前需要 `pip`装包、`virtualenv`建环境、`pipx`跑临时工具，现在 `uv`一个工具全搞定。

3. 

   **依赖冲突**：`uv`的依赖解析算法更高效，能更好处理复杂依赖树的冲突问题。

### 五、适用场景

- 

  **新项目**：直接用 `uv`搭建高效的工作流，避免后期迁移成本；

- 

  **老项目迁移**：兼容 `pip`和 `pyproject.toml`，只需替换 `pip`命令为 `uv pip`即可逐步切换；

- 

  **CI/CD 流水线**：`uv`的高速特性能大幅缩短流水线的依赖安装时间；

- 

  **追求效率的开发者**：讨厌等待 `pip`慢慢下载安装的人，`uv`的速度会让你“回不去”。

### 六、安装 `uv`

`uv`支持多平台安装，最简单的方式：

- 

  **macOS/Linux**：Homebrew

  ```
  brew install uv
  ```

- 

  **Windows**：Scoop

  ```
  scoop install uv
  ```

- 

  **通用脚本**（Linux/macOS/WSL）：

  ```
  curl -LsSf https://astral.sh/uv/install.sh | sh
  ```

- 

  **Windows PowerShell**：

  ```
  irm https://astral.sh/uv/install.ps1 | iex
  ```

### 总结

`uv`是**下一代 Python 工具链**，用 Rust 重构了传统 Python 开发中的分散工具，以“极致速度”和“一体化”为核心优势，正在成为 Python 开发者的新选择。如果你的项目受够了 `pip`的慢速度或工具的碎片化，`uv`值得一试！

## Windows11安装uv

uv官方网站：https://astral.sh/

下载最新版本的uv https://github.com/astral-sh/uv/releases，解压uv压缩包到C盘根目录中，配置环境变量Path添加指向uv的路径C:\uv-x86_64-pc-windows-msvc

打开powershell测试uv命令是否配置正常：

```powershell
uv --version
uvx --version
```

## openpyxl读取excel中公式运算后的值

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/python/test-formula.py

加载表格时把参数设置为data_only=True就可以读取公式运算后的值

```python
# 需要安装openpyxl库: pip install openpyxl
import openpyxl

excel_file = 'test-formula.xlsx'
excel_file_output = 'test-formula-output.xlsx'

# 使用data_only=True读取公式计算后的值
wb_read = openpyxl.load_workbook(excel_file, data_only=True)
ws_read = wb_read.active
cell_value = ws_read.cell(row=1, column=3).value

# 打印单元格值
print(f"第一行第三列的值是: {cell_value}")

# 使用默认模式保存文件（保留公式）
wb_save = openpyxl.load_workbook(excel_file)
wb_save.save(excel_file_output)
wb_save.close()

# 关闭读取的工作簿
wb_read.close()

```

## xlwings

`xlwings`是一个 **Python 与 Excel 之间的桥梁库**，它允许你用 Python 代码直接控制 Excel 应用程序（比如打开文件、读写数据、执行公式、操作图表等），同时也支持将 Excel 作为前端界面，通过 Python 实现复杂的后端计算或数据处理。

### 一、核心功能：Python 如何“操控”Excel？

`xlwings`的核心能力是通过 **COM 接口**（Windows）或 **AppleScript 接口**（macOS）与本地安装的 Excel 应用程序通信，从而实现以下操作：

| 功能分类             | 具体说明                                                     |
| -------------------- | ------------------------------------------------------------ |
| **文件与工作表操作** | 打开/关闭 Excel 文件、创建工作表、重命名/删除工作表等。      |
| **数据读写**         | 读取/写入单元格数据（支持单个单元格、区域、整行/整列）、批量处理数据。 |
| **公式与计算**       | 写入公式到单元格、触发 Excel 重新计算所有公式（确保结果最新）。 |
| **图表与格式**       | 创建图表（柱状图、折线图等）、设置单元格格式（字体、颜色、边框等）。 |
| **高级交互**         | 显示/隐藏 Excel 窗口、弹出消息框、通过 Excel 按钮触发 Python 函数（实现“Excel 前端 + Python 后端”的交互模式）。 |

### 二、为什么用 `xlwings`？（优势）

相比其他 Python 操作 Excel 的库（如 `openpyxl`、`pandas`），`xlwings`的独特优势在于：

#### 1. **直接调用 Excel 引擎，计算结果100%可靠**

`openpyxl`等库只能解析 Excel 文件的“静态结构”（如公式文本、单元格格式），但**无法执行公式计算**（比如 `=VLOOKUP(...)`、`=IFERROR(...)`等复杂函数）。

而 `xlwings`通过与本地 Excel 应用程序通信，能直接触发 Excel 的内置计算引擎，确保所有公式（包括复杂函数、数组公式、外部链接等）都能被正确计算，结果与手动打开 Excel 操作完全一致。

#### 2. **支持“Excel 前端 + Python 后端”的交互模式**

这是 `xlwings`最具特色的功能之一：你可以将 Excel 作为“用户界面”（比如让用户在 Excel 表格中输入参数、点击按钮），然后通过 Python 代码实现复杂的后端逻辑（比如数据清洗、模型计算、生成报告等），最后将结果回写到 Excel 中展示给用户。

这种模式非常适合非技术用户（如业务人员）使用——他们无需学习 Python，只需在熟悉的 Excel 界面中操作，就能享受 Python 强大的计算能力。

#### 3. **跨平台支持（Windows/macOS）**

`xlwings`同时支持 Windows 和 macOS 系统：

- 

  在 Windows 上，通过 COM 接口与 Excel 通信；

- 

  在 macOS 上，通过 AppleScript 接口与 Excel 通信。

这意味着你可以在不同操作系统的环境中使用相同的 Python 代码操作 Excel，无需大幅修改。

#### 4. **与 `pandas`无缝集成**

`pandas`是 Python 数据分析的核心库，而 `xlwings`可以直接将 `pandas`的 DataFrame 与 Excel 工作表进行双向转换：

- 

  用 `xlwings`将 Excel 数据读入 `pandas`DataFrame 进行分析；

- 

  用 `xlwings`将 `pandas`处理后的 DataFrame 直接写入 Excel 工作表，甚至保留原有的格式（如列宽、单元格颜色等）。

### 三、适用场景

`xlwings`特别适合以下场景：

- 

  **需要可靠公式计算的场景**：比如处理包含 `VLOOKUP`、`INDEX+MATCH`、`SUMIFS`等复杂函数的 Excel 文件，需要确保计算结果与 Excel 手动操作一致。

- 

  **Excel 作为前端界面的场景**：比如为非技术用户开发一个“Excel 操作面板”，用户只需在 Excel 中输入参数、点击按钮，就能触发 Python 代码完成复杂的数据处理或报表生成。

- 

  **需要与 Excel 深度交互的场景**：比如批量创建带格式的 Excel 报表、动态生成 Excel 图表、通过 Excel 按钮触发 Python 脚本等。

### 四、简单示例：用 `xlwings`操作 Excel

以下是一个简单的示例，演示如何用 `xlwings`打开 Excel 文件、写入数据、触发公式计算并保存：

```
import xlwings as xw

# 1. 启动 Excel 应用程序（visible=True 表示显示 Excel 窗口，方便调试）
app = xw.App(visible=True, add_book=False)  # add_book=False 不自动创建新工作簿

# 2. 打开一个已有的 Excel 文件（如果没有则创建）
try:
    wb = app.books.open("example.xlsx")  # 打开文件
except FileNotFoundError:
    wb = app.books.add()  # 创建新工作簿
    wb.save("example.xlsx")  # 保存文件

# 3. 选择第一个工作表（Sheet1）
sheet = wb.sheets[0]  # 或 wb.sheets["Sheet1"]

# 4. 写入数据到单元格
sheet.range("A1").value = "姓名"
sheet.range("B1").value = "年龄"
sheet.range("A2").value = "张三"
sheet.range("B2").value = 25
sheet.range("A3").value = "李四"
sheet.range("B3").value = 30

# 5. 写入公式到单元格（比如计算平均年龄）
sheet.range("A4").value = "平均年龄"
sheet.range("B4").value = "=AVERAGE(B2:B3)"  # 写入公式

# 6. 触发 Excel 重新计算所有公式（确保结果最新）
wb.app.calculate()  # 或 wb.api.Application.CalculateFull()

# 7. 读取公式计算结果（此时 B4 单元格的值应该是 27.5）
average_age = sheet.range("B4").value
print(f"平均年龄计算结果：{average_age}")

# 8. 保存文件并关闭
wb.save()
wb.close()

# 9. 退出 Excel 应用程序
app.quit()
```

运行上述代码后，会生成一个名为 `example.xlsx`的 Excel 文件，其中：

- 

  A1:B3 是手动写入的数据；

- 

  B4 单元格包含公式 `=AVERAGE(B2:B3)`，并且已经计算出结果（27.5）。

### 五、总结

`xlwings`是一个功能强大的 Python 库，它通过连接本地 Excel 应用程序，实现了 Python 与 Excel 之间的深度交互。无论是需要可靠的公式计算、将 Excel 作为前端界面，还是与 `pandas`结合进行数据分析，`xlwings`都能提供高效的解决方案。

如果你经常需要用 Python 处理 Excel 文件，尤其是涉及复杂公式或需要与 Excel 深度交互的场景，`xlwings`绝对值得一试！

### 六、示例

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/python/test-xlwings.py

```python
import xlwings as xw

# 1. 启动 Excel 应用程序（visible=True 表示显示 Excel 窗口，方便调试）
app = xw.App(visible=True, add_book=False)  # add_book=False 不自动创建新工作簿

# 2. 打开一个已有的 Excel 文件（如果没有则创建）
try:
    wb = app.books.open("example.xlsx")  # 打开文件
except FileNotFoundError:
    wb = app.books.add()  # 创建新工作簿
    wb.save("example.xlsx")  # 保存文件

# 3. 选择第一个工作表（Sheet1）
sheet = wb.sheets[0]  # 或 wb.sheets["Sheet1"]

# 4. 写入数据到单元格
sheet.range("A1").value = "姓名"
sheet.range("B1").value = "年龄"
sheet.range("A2").value = "张三"
sheet.range("B2").value = 25
sheet.range("A3").value = "李四"
sheet.range("B3").value = 30

# 5. 写入公式到单元格（比如计算平均年龄）
sheet.range("A4").value = "平均年龄"
sheet.range("B4").value = "=AVERAGE(B2:B3)"  # 写入公式

# 6. 触发 Excel 重新计算所有公式（确保结果最新）
wb.app.calculate()  # 或 wb.api.Application.CalculateFull()

# 7. 读取公式计算结果（此时 B4 单元格的值应该是 27.5）
average_age = sheet.range("B4").value
print(f"平均年龄计算结果：{average_age}")

# 8. 保存文件并关闭
wb.save()
wb.close()

# 9. 退出 Excel 应用程序
app.quit()
```

使用命令运行示例

```sh
python test-xlwings.py
```


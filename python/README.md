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


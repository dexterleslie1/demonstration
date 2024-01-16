## shell、sh、bash

### 概念

> https://blog.csdn.net/lingeio/article/details/96135086
> https://blog.csdn.net/u011318077/article/details/105407068

**shell** 中文意思贝壳，寓意类似内核的壳。Shell是指一种应用程序，这个应用程序提供了一个界面，用户通过这个界面访问操作系统内核的服务。Shell 是一个用 C 语言编写的程序，它是用户使用 [Linux](https://so.csdn.net/so/search?q=Linux&spm=1001.2101.3001.7020) 的桥梁。Shell 既是一种命令语言，又是一种程序设计语言。

**sh** 就是Bourne shell，这个是UNIX标准的默认shell，对它评价是concise简洁 compact紧凑 fast高效，由AT&T编写，属于系统管理shell

**bash** 是 GNU Bourne-Again SHell (GNU 命令解释程序 “Bourne二世”)
是linux标准的默认shell ，它基于Bourne shell，吸收了C shell和Korn shell的一些特性。bash是Bourne shell的超集，bash完全兼容Bourne shell,也就是说用Bourne shell的脚本不加修改可以在bash中执行，反过来却不行，bash的脚本在sh上运行容易报语法错误。简单地说，bash是sh的完整版，bash完全兼容sh命令，反之不行

**其他** 除了bash和bin，还有/bin/csh脚本，/bin/perl脚本，/bin/awk脚本，/bin/sed脚本，/bin/echo等

### 告诉 shell 使用正确的解析器执行脚本

> https://en.wikipedia.org/wiki/Shebang_%28Unix%29
> https://stackoverflow.com/questions/19538669/run-bash-script-with-sh

告诉 shell 使用 /bin/bash 解析器

```sh
#!/bin/bash

# your script here

```

添加执行权限给 shell 脚本文件

```sh
chmod +x my_script.sh
```

执行脚本文件让 shell 根据`Shebang`自动选择脚本解析器

```sh
./my_script.sh
```

NOTE：如果用户使用 sh my_script.sh 执行脚本，解析器会使用 /bin/sh 而不是使用 /bin/bash

```sh
sh my_script.sh
```



## =、==、-eq 区别

> https://stackoverflow.com/questions/20449543/shell-equality-operators-eq
>
> = 和 == 是用于字符串比较，其中 Bourne shell 只能够使用 =，Bash 中能够使用 = 和 ==。
> -eq 用于数值比较。

## [] 和 [[]] 区别

> https://www.baeldung.com/linux/bash-single-vs-double-brackets
>
> [] 是 Bourne shell 内置的
> [[]] 是 Bash 中的一个关键词
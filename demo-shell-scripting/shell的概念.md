# `shell`、`sh`、`bash`、`zsh`的概念

## 概念

> https://blog.csdn.net/lingeio/article/details/96135086
> https://blog.csdn.net/u011318077/article/details/105407068

一、**定义与概述**

1. **Shell**：Shell是一个命令行界面（CLI），允许用户与操作系统进行交互。它接收用户输入的命令，并将其传递给操作系统执行，然后将执行结果返回给用户。Shell也可以作为脚本编程语言，允许用户编写脚本自动化执行一系列命令。Shell是操作系统提供给用户的一个操作界面，用户可以通过Shell访问操作系统提供的各种服务。
2. **sh（Bourne Shell）**：sh是Unix系统中最早的Shell之一，由Stephen Bourne在AT&T Bell Labs开发。它是许多Shell的基础，具有简洁的语法和相对较少的内置命令，但仍然是许多脚本和系统管理任务的首选。sh的脚本通常具有良好的可移植性，可以在不同的Unix系统上运行。
3. **bash（Bourne Again Shell）**：bash是sh的增强版本，由Brian Fox和Chet Ramey在1989年开发。bash继承了sh的所有特性，并增加了许多新的功能和改进，如命令历史记录、命令补全、别名、参数扩展等。bash是目前大多数Linux发行版默认的Shell，也是许多Unix-like系统（包括macOS，直到macOS Catalina之前）的默认Shell。
4. **zsh（Z Shell）**：zsh是另一种Unix Shell，与bash类似，但提供了更多的特性和改进。zsh包含了许多高级特性，如更强大的命令行历史记录、智能命令补全、模块化配置等。zsh在可定制性和用户友好性方面较强，被广泛用于开发者和系统管理员之间。从macOS Catalina版本开始，macOS的默认Shell从bash更改为zsh。

二、**关系与区别**

1. **基础与扩展**：sh是Unix系统中最早的Shell之一，是许多Shell的基础。bash是sh的增强版本，提供了更多的功能和改进。zsh则是在bash的基础上进一步扩展，提供了更多的特性和定制选项。
2. **兼容性与可移植性**：bash和zsh都兼容sh的语法和命令，因此基于sh编写的脚本通常也可以在bash和zsh中运行。这使得bash和zsh在保持脚本可移植性的同时，提供了更多的功能和便利性。然而，由于bash和zsh扩展了sh的功能，使用bash或zsh特有语法和特性编写的脚本可能无法在sh中直接运行。
3. **使用场景与特性**：bash因其强大的功能和广泛的兼容性，适用于大多数Linux系统的日常使用和脚本编写任务。zsh则因其丰富的特性和高度的可定制性，更适合于需要高度定制化和用户友好性的场景。csh（C Shell）则以其与C语言类似的语法为特点，更适合于交互式使用，但在脚本编写方面可能不如bash和zsh方便。

三、**总结**

`shell`是一个广义的概念，指的是命令行界面和脚本编程语言。`sh`、`bash`、`zsh`都是具体的Shell实现，它们之间存在继承与发展的关系。bash是sh的增强版本，zsh则是在bash的基础上进一步扩展。选择哪个Shell取决于个人偏好、使用场景以及操作系统的默认设置。对于大多数用户来说，bash是一个功能强大、灵活易用的Shell选择；而对于需要高度定制化和用户友好性的用户来说，zsh可能是一个更好的选择。



## 告诉`shell`使用正确的解析器执行脚本

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

注意：如果用户使用 sh my_script.sh 执行脚本，解析器会使用 /bin/sh 而不是使用 /bin/bash

```sh
sh my_script.sh
```


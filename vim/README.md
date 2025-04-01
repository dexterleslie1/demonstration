# vim编辑器使用

## todo

寻找一份开源的 .vimrc 配置



## 当前使用的vim配置

```bash
set mouse=a
set clipboard=unnamed

" 修改默认的dd命令，dd删除行之后不把内容放置到默认寄存器
nnoremap x "_x
nnoremap d "_d
nnoremap D "_D
vnoremap d "_d

set ts=4 " 设置tab长度
set ls=2
set paste
set nu " 显示文件行号
"set cuc  " 设置鼠标所在列高亮
set cul " 设置鼠标所在行高亮
syntax on " 语法高亮  
colorscheme  darkblue "主题颜色
set hlsearch  " 高亮显示查询搜索的字符，
hi Search term=standout ctermfg=7 ctermbg=3  "修改背景、字符颜色

set enc=utf-8  " 编码设置  
set encoding=utf-8
set fencs=utf-8,ucs-bom,shift-jis,gb18030,gbk,gb2312,cp936  
  
set langmenu=zh_CN.UTF-8  " 设置 语言
set helplang=cn

set smartindent " 为C程序提供自动缩进
set cindent  " 使用C样式的缩进

autocmd BufNewFile *.c,*.h,*.py,*.cpp,*.sh,*.java exec ":call SetTitle()"
func SetTitle() 
	if &filetype == 'sh' || &filetype == 'python'    " 如果文件类型为.sh文件   
        call setline(1,"\########################################################################")   
    else        
        call setline(1, "/*************************************************************************")   
    endif  

	call append(line("."), "#	> File Name:".expand("%"))  
    call append(line(".")+1, "#	> Author: dexterleslie")   " 这里修改成自己的名字
    call append(line(".")+2, "#	> Mail: dexterleslie@gmail.com")   "修改为自己的邮箱
    call append(line(".")+3, "#	> Created Time: ".strftime("%c"))  

    if &filetype == 'sh' || &filetype == 'python'
        call append(line(".")+4, "\########################################################################")   
    else
        call append(line(".")+4, " ************************************************************************/")   
    end

    call append(line(".")+5, "")

    if expand("%:e")=='cpp'
        call append(line(".")+6, "#include <iostream>")
        call append(line(".")+7, "") 
        call append(line(".")+8, "using namespace std;")                
        call append(line(".")+9, "") 
    elseif &filetype == 'python'
        call append(line(".")+6, "\#!/usr/bin/env python") 
        call append(line(".")+7, "") 
    elseif &filetype == 'sh'
        call append(line(".")+6, "\#!/bin/bash")   
        call append(line(".")+7, "") 
    elseif expand("%:e")=='h'
        call append(line(".")+6, "\#pragma once")   
        call append(line(".")+7, "") 
    elseif &filetype == 'c' 
        call append(line(".")+6,"#include <stdio.h>")
        call append(line(".")+7,"#include <stdlib.h>")
        call append(line(".")+8,"#include <string.h>")
        call append(line(".")+9,"")
    endif

    
endfunc

autocmd BufNewFile * normal G   " 新建文件后，自动定位到文件末尾 

:inoremap ( ()<ESC>i   " 自动补全  
" :inoremap ) <c-r>=ClosePair(')')<CR>  
:inoremap { {<CR>}<ESC>O
" :inoremap } <c-r>=ClosePair('}')<CR>  
:inoremap [ []<ESC>i
" :inoremap ] <c-r>=ClosePair(']')<CR>  
  
function! ClosePair(char)
    if getline('.')[col('.') - 1] == a:char
        return "\<Right>"
    else
        return a:char  
    endif
endfunction


map <F5> :call CompileRunGcc()<CR>  " 设置一键编译
map <F5> :w<cr>:!python %<cr>
imap <F5> <ESC>:call CompileRunGcc()<CR>
func! CompileRunGcc()
    exec "w"
    exec "cd %:p:h"
    if &filetype == 'c'
        exec "!g++ % -o %<"
        exec "! ./%<"
    elseif &filetype == 'cpp'
        exec "!g++ % -o %<"
        exec "! ./%<"
    elseif &filetype == 'java'
        exec "!javac %"
        exec "!java %<"
    elseif &filetype == 'sh'
        :!./%
    endif
endfunc
```



## 4种模式

- 正常模式
- 命令模式
- 插入模式
- 可视模式



## 设置vim y复制内容到系统粘贴板

[vim如何复制到系统剪贴板](http://t.zoukankan.com/Biiigwang-p-12086514.html)

根据平台不同，要分两种情况。先用下面命令确定你属于哪一种，

```
vim --version | grep clipboard
```

 **情况一，**

如果结果里你找到加号开头的`+clipboard`， 恭喜你，你的vim没问题，是你姿势问题。

如果想偷懒用`y`直接把内容复制到系统剪贴板，需要到vim配置文件`.vimrc`里加一行属性。用下面命令开始配置，

```
vim ~/.vimrc
```

然后，加入下面这行，

```
set clipboard=unnamed
```

现在你的`y`，`d`，`x`，`p`已经能和 `ctrl-c`和`ctrl-v` 一个效果，并且能互相混用。

 

**情况二，**

如果找到的是负号开头的**`-clipboard`，**说明你的vim不支持系统剪切板，我的MacOS系统自带vim就不支持，所以跑来了。需要先重新安装vim，

Linux系统，

```
sudo apt install vim-gtk
```

MacOS，

```
brew install vim
```

安装好之后，重复情况一的操作即可。



## 可视模式

字符选择模式: 选中光标经过的所有**字符**，普通模式下按**小写 `v`** 进入
行选择模式：选中光标经过的所有**行**，普通模式下按 **大写`V`** 进入
块选择模式：选中一整个矩形框表示的所有文本，普通模式下按 `<Ctrl> + v` 进入 

### 多行注释

 首先光标定位到第一行行首，按下键盘上的 `ctrl + v` 组合键（Windows 下可以用 `ctrl + q`）进入 Visual Block 模式，按两次 `j` 键下移光标，选中前三行的首字符。
 再按下键盘上 `I`（大写）键进入插入模式，在第一行行首插入 `#` 字符。
 按下 `Esc` 退出插入模式，则后两行行首也会自动插入 `#` 字符。

### 多行复制粘贴

shift+v进入行选择模式，上下键移动选择多行，按y复制选定行，光标移动到粘贴行按p键粘贴内容



## 删除

### 清空所有内容

[vim清空所有内容](https://www.jianshu.com/p/6e281ef206c3)

按一下 ESC 键，确保退出编辑模式，再按两次键盘上面的“g”键，让光标移动到文本的首行，然后按键盘上面的“d”和“G”键。其中“d”键是小写，“G”键要切换成大写的，小伙伴们要注意大小写的切换；

### 多行删除

首先按esc进入命令行模式下，按下Ctrl + v, 进入列模式;  

选定要取消注释的多行;  

按下“x”或者“d”. 注意：如果是“*//”注释，那需要执行两次该操作，如果是“#”注释，一次即可*



## 撤销和重做

u  撤销上一步的操作

Ctrl+r 恢复上一步被撤销的操作



## 粘贴复制板内容

p 在当前行后粘贴复制版内容

shift+p 在当前行之前粘贴复制版内容



## 多窗口和tab模式

https://www.cnblogs.com/ASICer-tianqiao/p/16615894.html

http://t.zoukankan.com/suncoolcat-p-3310668.html



```shell
#同时水平打开多个文件，命令模式使用 qall 一次退出全部窗口
vim -o 1.c 2.c

#同时垂直打开多个文件，命令模式使用 qall 一次退出全部窗口
vim -O 1.c 2.c

#命令模式输入命令split filename或者vsplit filename打开新文件
:split filename
:vsplit filename

#顶部最大化当前窗口
ctrl+w+K
#左边最大化当前窗口
ctrl+w+H
#底部最大化当前窗口
ctrl+w+J
#右边最大化当前窗口
ctrl+w+L

#窗口间跳转
ctrl+w+上下左右
#下一个窗口跳转
ctrl+w+w

#全部窗口水平排列，命令模式输入all
:all
#全部窗口垂直排列，命令模式输入vertical all
:vertical all

#打开一个新窗口显示当前文件，用于对比代码
:split
:vsplit

#使用新的tab打开文件
:tabnew filename

#tab之间切换快捷键 g+t
```



## Sex命令

Sex 水平分割当前窗口，并在一个窗口中开启目录浏览器



## vi和shell切换

:shell 可以在不关闭vi的情况下切换到shell命令行
:exit 从shell回到vi



## 搜索模式

n 搜索下一个关键词

N 搜索上一个关键词



## 上下翻页

ctrl+f 下一页

ctrl+b上一页



## `vi/vim`打开中文乱码

>[链接](https://blog.csdn.net/KimBing/article/details/81779768)

### 方案1

新建`~/.vimrc`内容如下：

```bash
set encoding=utf-8
```



### 方案2

在`vi/vim`命令模式中输入`set encoding=utf-8`

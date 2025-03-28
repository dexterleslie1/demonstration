# Word 转换为 PDF

>[todo 参考链接](https://blog.csdn.net/weixin_38409915/article/details/125317664)



## LibreOffice/OpenOffice

>[参考链接](https://tariknazorek.medium.com/convert-office-files-to-pdf-with-libreoffice-and-python-a70052121c44)
>
>[参考链接](https://github.com/codespearhead/word-to-pdf-api)

提醒：

- libreoffice 命令在 Ubuntu 系统已经预先安装



### Docker 安装 LibreOffice

```dockerfile
FROM ubuntu:20.04

# 因为 apt install libreoffice 需要获取时区信息，否则 libreoffice 安装的时候会进入交互模式
ENV TZ="Asia/Shanghai"

RUN apt update
RUN apt install libreoffice -y
```



### Word 转换为 PDF

```bash
# 转换 doc 为 pdf
$ libreoffice --headless --convert-to pdf --outdir . 1.doc
convert /home/dexterleslie/temp/1.doc -> /home/dexterleslie/temp/1.pdf using filter : writer_pdf_Export

# 转换 docx 为 pdf
$ libreoffice --headless --convert-to pdf --outdir . 1.docx
convert /home/dexterleslie/temp/1.docx -> /home/dexterleslie/temp/1.pdf using filter : writer_pdf_Export
```

执行命令后，会创建一个与原文件同名，但扩展名为 .pdf 的文件

- --headless - 以“无头模式”启动，允许在没有用户界面的情况下使用应用程序。
- --convert-to - 将文件转换为选定的过滤器，在我们的例子中为“pdf”。
- --outdir - 表示转换后文件的目标文件夹。
- 1.docx - 要转换的文件的路径。



## Pandoc

todo






# Word 转换为 PDF

>[todo 参考链接](https://blog.csdn.net/weixin_38409915/article/details/125317664)



## LibreOffice/OpenOffice

>[参考链接](https://tariknazorek.medium.com/convert-office-files-to-pdf-with-libreoffice-and-python-a70052121c44)
>
>[参考链接](https://github.com/codespearhead/word-to-pdf-api)

提醒：

- libreoffice 命令在 Ubuntu 系统已经预先安装



### Docker 安装 LibreOffice

sources.list

```
deb http://mirrors.aliyun.com/ubuntu/ jammy main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ jammy-security main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ jammy main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ jammy-security main restricted universe multiverse
```

Dockerfile

```dockerfile
FROM ubuntu:22.04

ADD sources.list /etc/apt/sources.list
RUN apt update
RUN apt install libreoffice -y
# 安装支持中文字体库，否则某些不被支持的字体在转换后乱码
RUN apt install fonts-noto-cjk fonts-noto-cjk-extra fonts-noto-color-emoji fonts-noto-mono language-pack-zh* -y

ADD target/converter.jar /usr/share/converter.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/converter.jar --spring.profiles.active=prod
```

docker-compose.yaml

```yaml
version: "3.0"

services:
  converter:
    build:
      context: ./service
      dockerfile: Dockerfile
    image: converter
    environment:
      - TZ=Asia/Shanghai
      - LANG=zh_CN.UTF-8
    ports:
      - '8080:8080'

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

上面 libreoffice 命令不支持并发执行，否则报告错误。参考 [链接](https://ask.libreoffice.org/t/how-can-i-run-multiple-instances-of-soffice-under-linux/22258) 设置 libreoffice 支持并发转换

```bash
libreoffice -env:SingleAppInstance=false -env:UserInstallation=file:///tmp/LibO_Process_[UUID random number] --headless --convert-to pdf --outdir . 1.doc
```

- [UUID random number] 为随机生成的 UUID 值，以隔离每个 libreoffice 进程。



## Pandoc

todo






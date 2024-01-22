## 删除macOS隔离文件权限

> https://malcontentboffin.com/2019/12/macOS-Error-bin-bash-bad-interpreter-Operation-not-permitted.html

显示文件隔离属性

```sh
xattr -l my-file.sh
```

删除文件隔离属性

```sh
xattr -d com.apple.quarantine my-file.sh
```

递归删除文件和目录的隔离属性

```sh
xattr -r -d com.apple.quarantine Kap.app/
```


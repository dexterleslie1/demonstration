# `xpath`使用

## 环境准备

```bash
# 安装lxml
pip3 install lxml
```

## `xpath`用法

详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/python/demo-xpath)

- 选取所有节点

  ```python
  elements = htmlObject.xpath("//*")
  print("//*选取所有节点数量：", len(elements))
  ```

- 选取所有`li`节点

  ```python
  elements = htmlObject.xpath("//li")
  print("//li选取所有li节点数：", len(elements))
  ```

- 选取子节点`li>a`

  ```python
  elements = htmlObject.xpath("//li/a")
  print("//li/a选取子节点数：", len(elements))
  ```


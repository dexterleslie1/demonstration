# YAML



## 多行文本处理

>[`yaml` 中特殊符号 `| ＞ |+ |-` 的作用](https://blog.csdn.net/zhiboqingyun/article/details/122635124)

`|` 保留每行尾部的换行符 `\n`。

```yaml
include_newlines: |
            exactly as you see
            will appear these three
            lines of poetry
```

`>` 删除每行尾部的换行符 `\n`，则看似多行文本，则在程序中会将其视为一行。

```yaml
fold_newlines: >
            this is really a
            single line of text
            despite appearances
```

在 `>` 符号应用的多行文本值中，所有换行符都会被视为空格，有两种方法都可以强制保留换行符：

```yaml
fold_some_newlines: >
    a
    b  # 在两行之间空一行

    c
    d
      e   # 在值前面再加一个空格
    f
```

- 上面的 `fold_some_newlines` 被解析后等价于 `"a b\nc d\n  e\nf\n"`

`|+` 保留每行尾部的换行符 `\n` 的同时，保留内容结尾处的换行符 `\n`。

```yaml
s2: |+
  Foo
```

`|-` 保留每行尾部的换行符 `\n` 的同时，删除内容结尾处的换行符 `\n`。

```yaml
s3: |-
  Foo
```


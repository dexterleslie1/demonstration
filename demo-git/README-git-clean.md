# 演示git clean用法

## 参考资料

**[How to remove files that are listed in the .gitignore but still on the repository?](https://stackoverflow.com/questions/13541615/how-to-remove-files-that-are-listed-in-the-gitignore-but-still-on-the-repositor#:~:text=As%20the%20files%20in%20.,clean%20%2Dxdf%20to%20execute%20it.)**

## 只显示将要删除的文件或者目录，不执行实际删除

```
git clean -xdn
```

## 执行实际删除

```
git clean -xdf
```


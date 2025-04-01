# webpack+html演示

## 安装依赖

```shell
# 安装webpack和webpack-cli用于打包项目
npm install webpack webpack-cli --save-dev

# 安装html-webpack-pulgin用于处理html
npm install html-webpack-plugin --save-dev

# 安装style-loader和css-loader用于处理css
# style-loader通过js脚本在html head标签中插入style内联式样式
npm install style-loader css-loader --save-dev

# 安装less和less-loader用于处理less资源
npm install less less-loader --save-dev

# 安装node-sass和sass-loader用于处理sass资源
npm install node-sass sass-loader --save-dev

# 安装mini-css-extract-plugin用于处理css资源为单独文件并在html head建立style标签引用此文件
npm install mini-css-extract-plugin --save-dev

# 安装css-minimizer-webpack-plugin用于压缩css资源
npm install css-minimizer-webpack-plugin --save-dev
```

## 运行

```shell
# 打包项目
webpack
```

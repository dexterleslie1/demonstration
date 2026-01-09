# Angular Tree Control 演示

这是一个基于AngularJS 1.x的angular-tree-control库的演示项目。

## 安装

1. 确保已安装Node.js和npm
2. 克隆或下载此仓库
3. 导航到项目目录
4. 运行 `npm install` 安装依赖

## 运行演示

在Web浏览器中打开 `index.html` 文件。

## 演示的功能

### 1. 作为DOM元素的树控件

```html
<treecontrol class="tree-classic"
   tree-model="dataForTheTree"
   options="treeOptions"
   on-selection="showSelected(node)"
   selected-node="selectedNode">
   {{node.name}} ({{node.type}})
</treecontrol>
```

### 2. 作为属性的树控件

```html
<div treecontrol class="tree-classic"
   tree-model="dataForTheTree"
   options="treeOptions"
   on-selection="showSelected(node)"
   selected-node="selectedNode">
   {{node.name}} ({{node.type}})
</div>
```

### 3. 自定义样式的树控件

```javascript
$scope.customTreeOptions = {
    nodeChildren: "children",
    dirSelectable: true,
    allowDeselect: true,
    injectClasses: {
        ul: "my-ul-class",
        li: "my-li-class",
        liSelected: "my-li-selected-class",
        iExpanded: "my-i-expanded-class",
        iCollapsed: "my-i-collapsed-class",
        iLeaf: "my-i-leaf-class",
        label: "my-label-class",
        labelSelected: "my-label-selected-class"
    }
};
```

## 树配置选项

演示中使用了以下配置选项：

- `nodeChildren`: 定义子节点的属性名（默认值："children"）
- `dirSelectable`: 是否允许选择有子节点的目录节点（默认值：true）
- `allowDeselect`: 是否允许取消选择节点（默认值：true）
- `injectClasses`: 为树的不同元素注入自定义CSS类

## 示例数据结构

树使用以下属性的层次结构：

- `name`: 节点的显示名称
- `type`: 节点类型（例如：organization、department、team）
- `description`: 节点的附加信息
- `children`: 子节点数组

## 包含的文件

- `package.json`: 项目依赖配置
- `index.html`: 主演示文件
- `app.js`: AngularJS应用逻辑
- `USAGE.md`: 本使用指南

## 依赖

- AngularJS 1.8.x
- angular-tree-control 0.2.x

## 额外资源

- [angular-tree-control GitHub仓库](https://github.com/wix-incubator/angular-tree-control)
- [AngularJS文档](https://docs.angularjs.org/guide)

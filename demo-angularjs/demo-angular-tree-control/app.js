// 创建带有treeControl依赖的AngularJS模块
angular.module('treeControlDemo', ['treeControl'])

.controller('TreeController', function($scope) {
    // 树选项配置
    $scope.treeOptions = {
        nodeChildren: "children",
        dirSelectable: true,
        allowDeselect: true
    };
    
    // 带有样式的自定义树选项
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
    
    // 树的示例数据
    $scope.dataForTheTree = [
        {
            "name": "公司",
            "type": "organization",
            "description": "公司主要结构",
            "children": [
                {
                    "name": "开发部",
                    "type": "department",
                    "description": "软件开发部门",
                    "children": [
                        {
                            "name": "前端",
                            "type": "team",
                            "description": "前端开发团队",
                            "children": [
                                {
                                    "name": "Angular",
                                    "type": "subteam",
                                    "description": "Angular开发小组"
                                },
                                {
                                    "name": "React",
                                    "type": "subteam",
                                    "description": "React开发小组"
                                }
                            ]
                        },
                        {
                            "name": "后端",
                            "type": "team",
                            "description": "后端开发团队",
                            "children": [
                                {
                                    "name": "Node.js",
                                    "type": "subteam",
                                    "description": "Node.js开发小组"
                                },
                                {
                                    "name": "Python",
                                    "type": "subteam",
                                    "description": "Python开发小组"
                                }
                            ]
                        }
                    ]
                },
                {
                    "name": "市场部",
                    "type": "department",
                    "description": "市场和销售部门",
                    "children": [
                        {
                            "name": "数字营销",
                            "type": "team",
                            "description": "数字营销团队"
                        },
                        {
                            "name": "销售",
                            "type": "team",
                            "description": "销售团队"
                        }
                    ]
                },
                {
                    "name": "人力资源部",
                    "type": "department",
                    "description": "人力资源部门"
                },
                {
                    "name": "财务部",
                    "type": "department",
                    "description": "财务和会计部门"
                }
            ]
        }
    ];
    
    // 选中节点跟踪
    $scope.selectedNode = null;
    
    // 选择回调函数
    $scope.showSelected = function(node) {
        $scope.selectedNode = node;
    };
    
    // 获取选中节点路径的函数
    $scope.getSelectedPath = function(node) {
        var path = [];
        var current = node;
        
        while (current) {
            path.unshift(current.name);
            // 查找父节点
            current = findParent($scope.dataForTheTree, current);
        }
        
        return path.join(' > ');
    };
    
    // 查找父节点的辅助函数
    function findParent(tree, node) {
        for (var i = 0; i < tree.length; i++) {
            if (tree[i] === node) {
                return null;
            }
            if (tree[i].children) {
                var parent = findParent(tree[i].children, node);
                if (parent) {
                    return parent;
                }
                if (tree[i].children.indexOf(node) !== -1) {
                    return tree[i];
                }
            }
        }
        return null;
    }
});
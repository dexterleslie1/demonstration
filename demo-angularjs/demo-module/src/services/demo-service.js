// 演示服务 - 使用factory模式创建服务
(function() {
    'use strict';

    angular
        .module('demoModule')
        .factory('DemoService', DemoService);

    // 依赖注入 - 使用数组语法避免minification问题
    DemoService.$inject = ['$http', '$q', 'APP_CONFIG'];

    function DemoService($http, $q, APP_CONFIG) {
        // 私有变量
        var items = [
            {id: 1, name: '项目 1'},
            {id: 2, name: '项目 2'},
            {id: 3, name: '项目 3'}
        ];
        
        // 服务公开的API
        var service = {
            getItems: getItems,
            addItem: addItem,
            removeItem: removeItem,
            getItemById: getItemById,
            updateItem: updateItem
        };

        return service;

        ////////////

        function getItems() {
            // 返回副本以避免直接修改私有数据
            return items.slice();
        }

        function getItemById(id) {
            for (var i = 0; i < items.length; i++) {
                if (items[i].id === id) {
                    // 返回副本以避免直接修改私有数据
                    return Object.assign({}, items[i]);
                }
            }
            return null;
        }

        function addItem(item) {
            if (item && item.name) {
                items.push({
                    id: items.length + 1,
                    name: item.name,
                    createdAt: new Date()
                });
            }
        }

        function removeItem(id) {
            for (var i = 0; i < items.length; i++) {
                if (items[i].id === id) {
                    items.splice(i, 1);
                    break;
                }
            }
        }

        function updateItem(id, updatedItem) {
            for (var i = 0; i < items.length; i++) {
                if (items[i].id === id) {
                    items[i] = Object.assign(items[i], updatedItem);
                    return true;
                }
            }
            return false;
        }
    }

})();
// 创建controllers子目录
// 由于无法通过文件操作创建目录，我们先检查目录是否存在

// 演示控制器 - 使用Controller As语法
(function() {
    'use strict';

    // 获取app.js中创建的demoModule并注册controller
    angular
        .module('demoModule')
        .controller('DemoController', DemoController);

    // 依赖注入 - 使用数组语法避免minification问题
    DemoController.$inject = ['DemoService', 'APP_CONFIG'];

    function DemoController(DemoService, APP_CONFIG) {
        var vm = this;
        
        // 公共属性
        vm.message = '来自演示控制器的问候!';
        vm.configName = APP_CONFIG.name;
        vm.items = [];
        
        // 公共方法
        vm.addItem = addItem;
        vm.removeItem = removeItem;
        
        // 初始化
        activate();
        
        ////////////
        
        function activate() {
            // 控制器初始化逻辑
            console.log('演示控制器已激活');
            vm.items = DemoService.getItems();
        }
        
        function addItem(name) {
            if(name) {
                DemoService.addItem({name: name, id: vm.items.length + 1});
                vm.items = DemoService.getItems();
            }
        }
        
        function removeItem(id) {
            DemoService.removeItem(id);
            vm.items = DemoService.getItems();
        }
    }

})();
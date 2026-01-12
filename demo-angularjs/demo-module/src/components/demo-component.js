// 演示组件 - Angular 1.5+ 组件示例
(function() {
    'use strict';

    angular
        .module('demoModule')
        .component('demoComponent', {
            templateUrl: 'src/components/demo-component.template.html',
            controller: 'DemoComponentController',
            controllerAs: 'vm',
            bindings: {
                name: '<' // 单向绑定
            }
        })
        .controller('DemoComponentController', DemoComponentController);

    // 依赖注入 - 使用数组语法避免minification问题
    DemoComponentController.$inject = [];

    function DemoComponentController() {
        var vm = this;
        
        // 组件生命周期钩子
        vm.$onInit = function() {
            vm.name = 'Dexter';
            vm.greeting = '欢迎使用 ' + vm.name + '!';
            vm.items = [
                { id: 1, text: '第一个项目' },
                { id: 2, text: '第二项目' },
                { id: 3, text: '第三项目' }
            ];
        };
        
        // vm.$onChanges = function(changesObj) {
        //     // 当绑定的属性发生变化时执行
        //     if (changesObj.name && !changesObj.name.isFirstChange()) {
        //         console.log('名称从', changesObj.name.previousValue, '变为', changesObj.name.currentValue);
        //     }
        // };
        
        vm.addItem = function(text) {
            if (text) {
                vm.items.push({
                    id: vm.items.length + 1,
                    text: text
                });
            }
        };
        
        vm.removeItem = function(index) {
            vm.items.splice(index, 1);
        };
    }

})();
// 演示指令 - 自定义指令示例
(function() {
    'use strict';

    angular
        .module('demoModule')
        .directive('demoDirective', demoDirective);

    function demoDirective() {
        var directive = {
            restrict: 'A', // 属性指令
            link: linkFunc,
            scope: {} // 隔离作用域
        };

        return directive;

        ////////////

        function linkFunc(scope, element, attrs) {
            // 指令链接函数
            element.css('background-color', '#f0f0f0');
            element.css('padding', '10px');
            element.css('margin', '10px 0');
            
            // 添加内容到元素
            element.append('<p>这是一个自定义指令在运行!</p>');
            
            // 可以添加事件监听等交互功能
            element.on('click', function() {
                element.css('background-color', '#e0e0e0');
            });
            
            // 清理工作
            scope.$on('$destroy', function() {
                // 清理事件监听等资源
            });
        }
    }

})();
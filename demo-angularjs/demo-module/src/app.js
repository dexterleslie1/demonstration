// 定义主应用模块
// 使用立即执行函数表达式(IIFE)确保代码在正确的作用域内运行
(function() {
    'use strict';

    // 使用数组语法显式声明依赖项，避免minification问题
    angular
        .module('demoModule', [])
        .constant('APP_CONFIG', {
            name: '演示应用程序',
            version: '1.0.0',
            apiUrl: '/api'
        });

})();
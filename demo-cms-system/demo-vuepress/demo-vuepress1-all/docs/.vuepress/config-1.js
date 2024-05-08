module.exports = {
    title: '测试 vuepress1.x',
    description: 'Just playing around',
    themeConfig: {
        sidebarDepth: 2,
        nav: [
            { text: '首页', link: '/' },
            { text: 'vue学习', link: '/vue/' },
            { text: 'react学习', link: '/react/' },
            { text: 'nodejs学习', link: '/nodejs/' },
            // 你可以添加更多的链接  
            // 外部链接可以添加 target 属性  
            { text: 'GitHub', link: 'https://github.com/vuejs/vuepress', target: '_blank' },
            // 下拉菜单  
            {
                text: '更多',
                items: [
                    { text: '关于', link: '/about/' },
                    { text: '联系我们', link: '/contact/' },
                ]
            },
        ],
        sidebar: {
            '/': [
                '', '/vue/', '/react/', '/nodejs/'
            ]
        }
    }
}
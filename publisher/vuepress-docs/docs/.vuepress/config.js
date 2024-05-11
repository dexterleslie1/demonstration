module.exports = {
    title: '我的知识库',
    themeConfig: {
        sidebarDepth: 6,
        nav: [
            { text: '首页', link: '/' },
            { text: '英语学习', link: '/英语学习/' },
            { text: 'IntelliJ IDEA的使用', link: '/intellij-idea/' },
            { text: 'MySQL/MariaDB学习', link: '/MySQL或MariaDB学习/' },
            {
                text: '容器相关',
                items: [
                    { text: 'Docker容器', link: '/docker容器/' }
                ]
            },
            { text: '我的Github', link: 'https://github.com/dexterleslie1', target: '_blank' },
        ],
        sidebar: {
            '/英语学习/': [{
                title: "英语学习总结",
                children: ['todo列表.md', '什么是语法.md',
                    '音标.md', '词性或词类.md',
                    '句子成分.md', '句型.md', '语态.md',
                    '时态.md', '主谓一致.md'],
            }],
            '/intellij-idea/': [{
                title: "IntelliJ IDEA的使用",
                children: ['多重光标同时编辑.md', '快捷键.md'],
            }],
            '/MySQL或MariaDB学习/': [
                {
                    title: "MySQL/MariaDB学习",
                    children: [
                        {
                            title: "MySQL",
                            children: [
                                '使用docker-compose运行MySQL.md',
                                'mysqldump使用.md'
                            ]
                        }, {
                            title: "MariaDB",
                            // children: [
                            //     ''
                            // ]
                        }
                    ]
                }
            ],
            '/docker容器/': [
                {
                    title: "docker容器学习",
                    children: [
                        'docker的安装.md'
                    ]
                }
            ],
            '/': ['/'],
        }
    }
}
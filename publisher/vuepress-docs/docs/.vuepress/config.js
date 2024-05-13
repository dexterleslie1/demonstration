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
                text: '前端技术',
                items: [
                    { text: 'React技术', link: '/React技术/' }
                ]
            },
            {
                text: '容器相关',
                items: [
                    { text: 'Docker容器', link: '/docker容器/' }
                ]
            },
            { text: 'Shell脚本编程', link: '/shell脚本编程/' },
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
            '/React技术/': [
                {
                    title: "React学习",
                    children: [
                        '使用react脚手架创建项目.md',
                        '路由.md',
                    ]
                }
            ],
            '/shell脚本编程/': [
                {
                    title: "Shell脚本编程",
                    children: [
                        '在脚本头声明脚本的用法.md',
                        '设置shell行为的命令.md',
                        '函数的用法.md',
                        '标准shell脚本结构.md',
                        '数组.md',
                        '关联数组.md',
                    ]
                }
            ],
            '/': ['/'],
        }
    }
}
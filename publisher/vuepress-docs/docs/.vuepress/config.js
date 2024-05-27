const lessSassPostcssSidebar = {
    title: "less、sass/scss、postcss学习",
    // 点击title时候跳转到/less-sass-postcss/路由
    path: '/less-sass-postcss/',
    children: [
        {
            title: "less",
            children: [
                '/less/浏览器开发环境中配置less.md',
                '/less/变量.md',
            ]
        }, {
            title: "sass/scss",
            children: [
                '/scss/浏览器开发环境中配置scss.md',
                '/scss/变量.md',
                '/scss/嵌套样式.md',
            ]
        },
    ]
}

const prometheusGrafanaAlertmanagerSidebar = {
    title: "监控Prometheus+Grafana+AlertManager",
    path: '/prometheus-grafana-alertmanager/',
    children: [
        '使用docker-compose运行prometheus-grafana-alertmanager.md',
        {
            title: "Grafana使用",
            children: [
                '/grafana/使用docker运行.md',
                '/grafana/手动导入dashboard.md',
                '/grafana/自动导入dashboards和datasources.md',
            ]
        }, {
            title: "Prometheus使用",
            children: [
                '/prometheus/标签.md',
                '/prometheus/指标类型.md',
                '/prometheus/客户端库.md',
                '/prometheus/拉取目标配置.md',
                '/prometheus/自定义exporter.md',
                '/prometheus/promql.md',
                '/prometheus/exporter使用.md',
            ]
        }
    ]
}

module.exports = {
    title: '我的知识库',
    plugins: [
        'mermaidjs'
    ],
    themeConfig: {
        sidebarDepth: 6,
        nav: [
            { text: '首页', link: '/' },
            { text: '英语学习', link: '/英语学习/' },
            { text: 'IntelliJ IDEA的使用', link: '/intellij-idea/' },
            {
                text: '数据库',
                items: [
                    { text: 'MySQL', link: '/MySQL/' },
                    { text: 'MariaDB', link: '/MariaDB/' },
                ]
            }, {
                text: '前端技术',
                items: [
                    { text: 'React技术', link: '/React技术/' },
                    { text: 'less、sass/scss、postcss', link: '/less-sass-postcss/' },
                ]
            },
            {
                text: '容器相关',
                items: [
                    { text: 'Docker容器', link: '/docker容器/' }
                ]
            },
            {
                text: '操作系统',
                items: [
                    { text: 'Shell脚本编程', link: '/shell脚本编程/' },
                    { text: 'Ubuntu使用', link: '/ubuntu使用/' },
                    { text: 'Linux使用', link: '/linux使用/' },
                    { text: '基准和性能测试', link: '/基准和性能测试/' },
                ]
            },
            {
                text: '运维',
                items: [
                    { text: '监控Prometheus+Grafana+AlertManager', link: '/prometheus-grafana-alertmanager/' },
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
            '/MySQL/': [
                {
                    title: "MySQL学习",
                    children: [
                        '使用docker-compose运行MySQL.md',
                        'mysqldump使用.md'
                    ]
                }
            ],'/MariaDB/': [
                {
                    title: "MariaDB学习",
                    children: [
                        '使用容器运行.md',
                        '自动更新数据库.md',
                    ]
                }
            ],
            '/docker容器/': [
                {
                    title: "docker容器学习",
                    children: [
                        'docker的安装.md',
                        'docker-compose设置项目名称.md',
                        'docker-volume.md',
                    ]
                }
            ],
            '/React技术/': [
                {
                    title: "React学习",
                    children: [
                        '使用react脚手架创建项目.md',
                        '路由.md',
                        'redux.md',
                        '样式的管理.md',
                    ]
                }
            ], '/less-sass-postcss/': [
                lessSassPostcssSidebar
            ], '/less/': [
                lessSassPostcssSidebar
            ], '/scss/': [
                lessSassPostcssSidebar
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
            '/ubuntu使用/': [
                {
                    title: "Ubuntu使用",
                    children: [
                        '配置基于ubuntu的软件开发环境.md',
                        '安装googlepinyin输入法.md'
                    ]
                }
            ], '/linux使用/': [
                {
                    title: "Linux使用",
                    children: [
                        '命令行工具列表.md'
                    ]
                }
            ], '/基准和性能测试/': [
                {
                    title: "系统基准和性能测试",
                    children: [
                        '监控方案.md',
                        '测试工具安装.md',
                        'IO测试.md',
                    ]
                }
            ], '/prometheus-grafana-alertmanager/': [
                prometheusGrafanaAlertmanagerSidebar
            ], '/grafana/': [
                prometheusGrafanaAlertmanagerSidebar
            ], '/prometheus/': [
                prometheusGrafanaAlertmanagerSidebar
            ],
            '/': ['/'],
        }
    }
}
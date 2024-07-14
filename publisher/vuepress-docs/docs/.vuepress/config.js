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
            { text: '数据结构和算法', link: '/数据结构和算法/' },
            {
                text: '性能相关',
                items: [
                    { text: '系统基准和性能测试', link: '/基准和性能测试/' },
                    { text: 'JVM性能', link: '/jvm性能/' },
                ]
            },
            {
                text: '前端相关技术',
                items: [
                    { text: 'React技术', link: '/React技术/' },
                    { text: 'less、sass/scss、postcss', link: '/less-sass-postcss/' },
                ]
            }, {
                text: '后端相关技术',
                items: [
                    { text: 'MySQL', link: '/MySQL/' },
                    { text: 'MariaDB', link: '/MariaDB/' },
                    { text: 'MyBatis-plus', link: '/mybatis/' },
                    { text: 'Redis', link: '/redis/' },
                    { text: 'Maven', link: '/maven/' },
                    { text: 'Mockito', link: '/mockito/' },
                    { text: 'Java相关', link: '/java/' },
                    { text: 'Spring Boot', link: '/spring-boot/' },
                    { text: 'Logback', link: '/logback/' },
                ]
            },
            {
                text: '计算机信息安全',
                items: [
                    { text: '密码算法', link: '/密码算法/' },
                    { text: '信息安全', link: '/计算机信息安全/' },
                ]
            },
            {
                text: '其他',
                items: [
                    { text: 'Git使用', link: '/git/' },
                    { text: 'OpenLDAP', link: '/openldap/' },
                    { text: 'Jira', link: '/jira/' },
                    { text: '软件工程', link: '/软件工程/' },
                    { text: 'Jsoup', link: '/jsoup/' },
                    { text: 'IntelliJ IDEA的使用', link: '/intellij-idea/' },
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
                children: [
                    '多重光标同时编辑.md',
                    '快捷键.md',
                    'intellij全家桶破解.md',
                ],
            }],
            '/MySQL/': [
                {
                    title: "MySQL学习",
                    children: [
                        '使用docker-compose运行MySQL.md',
                        'mysql备份和还原.md',
                        'mysql存储过程.md',
                        '启用或关闭慢日志.md',
                        '性能分析.md',
                    ]
                }
            ], '/MariaDB/': [
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
                        '安装googlepinyin输入法.md',
                        'sources.list.md',
                    ]
                }
            ], '/linux使用/': [
                {
                    title: "Linux使用",
                    children: [
                        '命令行工具列表.md',
                        'centos8相关.md',
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
            ], '/redis/': [
                {
                    title: "Redis",
                    children: [
                        'docker运行redis.md',
                        'spring-boot项目集成redis.md',
                    ]
                }
            ], '/mybatis/': [
                {
                    title: "MyBatis",
                    children: [
                        '',
                        'spring-boot项目配置mybatis-plus.md',
                        'querywrapper用法.md',
                        'mapper用法.md',
                    ]
                }
            ], '/maven/': [
                {
                    title: "Maven",
                    children: [
                        'maven仓库.md',
                        'mvn命令.md',
                        'maven的scope用法.md',
                        '指定jdk版本.md',
                        '多模块配置.md',
                        'dependencymanagement用法.md',
                        'maven的optional用法.md',
                        'maven发布带main函数可执行jar.md',
                    ]
                }
            ], '/git/': [
                {
                    title: "Git",
                    children: [
                        '分支.md',
                        '标签.md',
                        '基于分支和标签的版本管理.md',
                        '初始化非空目录指向远程仓库.md',
                    ]
                }
            ], '/mockito/': [
                {
                    title: "Mockito",
                    children: [
                        'maven项目配置mockito.md',
                        'mockito的使用.md',
                    ]
                }
            ], '/spring-boot/': [
                {
                    title: "Spring Boot",
                    children: [
                        '快速创建spring-boot项目.md',
                        'spring-boot项目的测试.md',
                        'spring-boot全局异常处理.md',
                        'spring-security.md',
                        'spring-boot中@Import用法.md',
                        'spring-boot自定义属性.md',
                        'spring-boot的condition用法.md',
                        'spring-boot自定义自动配置.md',
                        'spring的resource使用.md',
                        'spring-boot的DataSourceInitializer使用.md',
                        'spring-boot的PasswordEncoder使用.md',
                        'spring-boot外部restcontroller.md',
                        'spring-boot的@EnableXxx使用.md',
                        '通过parent或dependencymanagement方式管理spring-boot依赖.md',
                    ]
                }
            ], '/java/': [
                {
                    title: "Java相关",
                    children: [
                        'entity、vo、dto、po、do.md',
                        'GC.md',
                        'java命令行参数.md',
                        'jdk相关工具.md',
                        'metaspace与permgen.md',
                        '合理设置jvm内存.md',
                    ]
                }
            ], '/logback/': [
                {
                    title: "Logback",
                    children: [
                        '配置和使用.md',
                    ]
                }
            ], '/计算机信息安全/': [
                {
                    title: "信息安全",
                    children: [
                        '',
                        '应用安全.md',
                    ]
                }
            ], '/密码算法/': [
                {
                    title: "密码算法",
                    children: [
                        '',
                        '摘要算法.md',
                        '对称密码算法.md',
                        '非对称密码算法.md',
                        'jwt.md',
                    ]
                }
            ], '/openldap/': [
                {
                    title: "OpenLDAP使用",
                    children: [
                        '',
                        '使用docker运行openldap.md',
                        '管理openldap.md',
                    ]
                }
            ], '/jira/': [
                {
                    title: "Jira使用",
                    children: [
                        '',
                        '使用docker运行jira.md',
                    ]
                }
            ], '/软件工程/': [
                {
                    title: "软件工程",
                    children: [
                        'drawio使用.md',
                    ]
                }
            ], '/jsoup/': [
                {
                    title: "jsoup使用",
                    children: [
                        '',
                        'jsoup的用法.md',
                    ]
                }
            ], '/jvm性能/': [
                {
                    title: "JVM性能相关",
                    children: [
                        'arthas使用.md',
                        'jprofiler用法.md',
                        'cpu性能分析.md',
                        'oom分析.md',
                    ]
                }
            ], '/数据结构和算法/': [
                {
                    title: "数据结构和算法",
                    children: [
                        '布隆过滤器.md',
                    ]
                }
            ],
            '/': ['/'],
        }
    }
}
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
                    { text: 'JVM性能', link: '/jvm性能/' },
                    { text: '基准测试', link: '/benchmark/' },
                ]
            },
            {
                text: '前端相关技术',
                items: [
                    { text: 'React技术', link: '/React技术/' },
                    { text: 'Vue', link: '/vue/' },
                    { text: 'Nuxt', link: '/nuxt/' },
                    { text: 'less、sass/scss、postcss', link: '/less-sass-postcss/' },
                    { text: 'Android相关', link: '/android/' },
                ]
            }, {
                text: '后端相关技术',
                items: [
                    { text: 'MySQL、MariaDB', link: '/mysql-n-mariadb/' },
                    { text: 'Redis', link: '/redis/' },
                    { text: 'Maven', link: '/maven/' },
                    { text: 'Java相关', link: '/java/' },
                    { text: 'Java相关库', link: '/java-library/' },
                    { text: 'Spring Boot', link: '/spring-boot/' },
                    { text: 'Spring Cloud', link: '/spring-cloud/' },
                    { text: 'Minio', link: '/minio/' },
                    { text: 'Locust', link: '/locust/' },
                    { text: 'Docker容器', link: '/docker容器/' },
                    { text: 'Harbor', link: '/harbor/' },
                    { text: 'Openresty', link: '/openresty/' },
                    { text: 'JMeter用法', link: '/jmeter/' },
                    { text: 'Kubernetes', link: '/kubernetes/' },
                    { text: 'Elasticsearch、Kibana', link: '/elasticsearch/' },
                    { text: 'Terraform', link: '/terraform/' },
                    { text: 'NodeJS', link: '/nodejs/' },
                    { text: 'Canal', link: '/canal/' },
                    { text: 'Debezium', link: '/debezium/' },
                    { text: 'Zookeeper', link: '/zookeeper/' },
                ]
            },
            {
                text: '计算机信息安全',
                items: [
                    { text: '密码算法', link: '/密码算法/' },
                    { text: '信息安全', link: '/计算机信息安全/' },
                    { text: 'ssl、tls、https相关', link: '/ssl-tls-https/' },
                ]
            },
            {
                text: '其他',
                items: [
                    { text: 'Git使用', link: '/git/' },
                    { text: 'OpenLDAP', link: '/openldap/' },
                    { text: 'Jira', link: '/jira/' },
                    { text: '软件工程', link: '/软件工程/' },
                    { text: 'IntelliJ IDEA的使用', link: '/intellij-idea/' },
                    { text: 'Lua的使用', link: '/lua/' },
                    { text: 'Postman使用', link: '/postman/' },
                    { text: 'VMWare', link: '/vmware/' },
                    { text: 'Golang', link: '/golang/' },
                    { text: 'Python', link: '/python/' },
                    { text: 'Chrome用法', link: '/chrome/' },
                    { text: 'Wireshark使用', link: '/wireshark/' },
                    { text: 'Gost使用', link: '/gost/' },
                    { text: 'Ruby', link: '/ruby/' },
                    { text: '工具集', link: '/toolset/' },
                    { text: 'Selenium', link: '/selenium/' },
                ]
            },
            {
                text: '操作系统',
                items: [
                    { text: 'Shell脚本编程', link: '/shell脚本编程/' },
                    { text: 'linux、ubuntu使用', link: '/linux使用/' },
                    { text: 'macOS', link: '/macos/' },
                ]
            },
            {
                text: '运维',
                items: [
                    { text: '监控Prometheus', link: '/prometheus/' },
                    { text: 'ELK', link: '/elk/' },
                    { text: 'Skywalking', link: '/skywalking/' },
                ]
            }, {
                text: '云提供商',
                items: [
                    { text: '亚马逊', link: '/aws/' },
                    { text: '阿里云', link: '/aliyun/' },
                    { text: '谷歌云', link: '/gcp/' },
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
                    '时态.md', '主谓一致.md',
                    '短语.md',
                    '音标.md',
                ],
            }],
            '/intellij-idea/': [{
                title: "IntelliJ IDEA的使用",
                children: [
                    '多重光标同时编辑.md',
                    '快捷键.md',
                    'intellij全家桶破解.md',
                    'AI插件.md',
                    '创建并管理项目.md',
                ],
            }],
            '/mysql-n-mariadb/': [
                {
                    title: "MySQL、MariaDB学习",
                    children: [
                        '版本和兼容性.md',
                        '运行mysql和mariadb.md',
                        'mysql客户端工具.md',
                        'binlog.md',
                        'mysql-cli快捷键.md',
                        '备份和还原.md',
                        'mysql锁.md',
                        '事务.md',
                        '分区表.md',
                        '初始化数据库.md',
                        '变量.md',
                        '启用或关闭慢日志.md',
                        '存储过程.md',
                        '性能分析.md',
                        '用户和权限管理.md',
                        '自动更新数据库.md',
                        '系统数据库和表.md',
                        '函数.md',
                        '计算数据和索引大小.md',
                        '命令.md',
                        '原理.md',
                        '配置参数.md',
                        '内存.md',
                        'too-many-connections处理.md',
                        'sql.md',
                        '分库分表.md',
                    ]
                }
            ],
            '/docker容器/': [
                {
                    title: "docker容器学习",
                    children: [
                        'docker的安装.md',
                        'docker-compose.md',
                        'docker-volume.md',
                        'docker-swarm.md',
                        'docker命令.md',
                        '资源限制.md',
                        'dockerize用法.md',
                        'dockerhub加速代理.md',
                        'docker日志.md',
                        'entrypoint、cmd、command.md',
                        '镜像仓库.md',
                    ]
                }
            ], '/React技术/': [
                {
                    title: "React学习",
                    children: [
                        '使用react脚手架创建项目.md',
                        '路由.md',
                        'redux.md',
                        '样式的管理.md',
                    ]
                }
            ], '/vue/': [
                '脚手架创建项目.md',
                '指令.md',
            ], '/nuxt/': [
                '',
                '集成element-ui.md',
                '路由.md',
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
                        'shell的概念.md',
                        'zsh使用.md',
                        '语法基础.md',
                        '在脚本头声明脚本的用法.md',
                        '设置shell行为的命令.md',
                        '函数的用法.md',
                        '标准shell脚本结构.md',
                        '数组.md',
                        '关联数组.md',
                    ]
                }
            ], '/linux使用/': [
                {
                    title: "Linux使用",
                    children: [
                        '命令行工具列表.md',
                        'centos.md',
                        'lvm逻辑卷管理.md',
                        'systemd、systemctl服务.md',
                        '搭建nfs服务器.md',
                        'dns.md',
                        'ubuntu-n-debian.md',
                        '操作系统页面缓存、目录项缓存、索引节点缓存.md',
                        'ssh客户端.md',
                    ]
                }
            ], '/prometheus/': [
                {
                    title: "Prometheus使用",
                    children: [
                        '使用docker-compose运行prometheus.md',
                        'prometheus设置.md',
                        '使用docker运行grafana.md',
                        'grafana手动导入dashboard.md',
                        'grafana自动导入dashboards和datasources.md',
                        'prometheus自定义exporter.md',
                        'promql.md',
                        'prometheus标签.md',
                        'prometheus指标类型.md',
                        'prometheus客户端库.md',
                        'prometheus拉取目标配置.md',
                        'exporter使用.md',
                        '告警设置.md',
                    ]
                }
            ], '/redis/': [
                {
                    title: "Redis",
                    children: [
                        '各种模式.md',
                        'redisson用法.md',
                        '持久化.md',
                        '数据类型和命令.md',
                        '客户端.md',
                        '事务.md',
                        'Lua脚本.md',
                        '应用场景.md',
                        '最佳实践.md',
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
                        'maven插件.md',
                        'maven-wrapper用法.md',
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
                        'git凭证管理.md',
                        'gitee.md',
                        'github.md',
                        '设置自签名ssl证书不验证.md',
                        '原理.md',
                        '变基.md',
                        '命令.md',
                    ]
                }
            ], '/spring-boot/': [
                {
                    title: "Spring Boot",
                    children: [
                        '快速创建spring-boot项目.md',
                        'spring-boot项目的测试.md',
                        'spring-security.md',
                        'spring的resource使用.md',
                        'spring-boot的DataSourceInitializer使用.md',
                        'spring-boot的PasswordEncoder使用.md',
                        'spring-boot外部restcontroller.md',
                        '通过parent或dependencymanagement方式管理spring-boot依赖.md',
                        '横向扩展.md',
                        '任务调度框架.md',
                        '@Autowired和@Resource的区别.md',
                        'spring-boot-actuator.md',
                        'spring容器.md',
                        'spring-aop.md',
                        'spring事务.md',
                        'spring-mvc.md',
                        'swagger2-knife4j.md',
                        'ibatis、mybatis、mybatis-plus.md',
                        'spring-boot.md',
                        'spring-boot-thymeleaf.md',
                    ]
                }
            ], '/java/': [
                {
                    title: "Java相关",
                    children: [
                        '基础.md',
                        'entity、vo、dto、po、do.md',
                        'java命令行参数.md',
                        'jdk相关工具.md',
                        'jvm内存.md',
                        'jmh.md',
                        'arthas使用.md',
                        'cpu性能分析.md',
                        'jprofiler用法.md',
                        '异常.md',
                        '生产环境问题排查.md',
                        'tomcat.md',
                        'double类型运算精度问题.md',
                        'JDK8新特性.md',
                        'JUC.md',
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
            ], '/minio/': [
                {
                    title: "Minio使用",
                    children: [
                        'docker运行minio.md',
                        'java客户端.md',
                    ]
                }
            ], '/locust/': [
                {
                    title: "Locust使用",
                    children: [
                        '安装并运行.md',
                        '使用docker运行.md',
                        'master-slave模式.md',
                        '编写测试.md',
                        '性能测试实践.md',
                        'locust4j.md',
                    ]
                }
            ], '/harbor/': [
                {
                    title: "Harbor使用",
                    children: [
                        '',
                    ]
                }
            ], '/lua/': [
                {
                    title: "Lua的使用",
                    children: [
                        '运行环境安装.md',
                        '运行lua脚本.md',
                        'lua编程基础.md',
                    ]
                }
            ], '/openresty/': [
                {
                    title: "Openresty的使用",
                    children: [
                        '编译docker基础镜像.md',
                        'lua脚本.md',
                        '监控.md',
                        'x-forwarded-for用法.md',
                        'lua-resty-limit-traffic库使用.md',
                        'https设置.md',
                        '横向扩展.md',
                        '通过环境变量传递参数.md',
                    ]
                }
            ], '/性能测试/': [
                {
                    title: "性能测试相关",
                    children: [
                        '启动性能测试辅助目标.md',
                    ]
                }
            ], '/jmeter/': [
                {
                    title: "JMeter用法",
                    children: [
                        '',
                        '自定义插件.md',
                        '定时器.md',
                    ]
                }
            ], '/spring-cloud/': [
                {
                    title: "SpringCloud用法",
                    children: [
                        '',
                        'assistant示例.md',
                        '分布式ID.md',
                    ]
                }
            ], '/ssl-tls-https/': [
                {
                    title: "ssl、tls、https相关",
                    children: [
                        '概念.md',
                        '密钥和证书的管理.md',
                        'https中间人攻击.md',
                    ]
                }
            ], '/kubernetes/': [
                {
                    title: "kubernetes用法",
                    children: [
                        '安装k8s.md',
                        '实验用的镜像.md',
                        'pod的基础.md',
                        'kubectl命令.md',
                        '命名空间namespace.md',
                        'pod控制器.md',
                        '高级调度.md',
                        'volume数据存储.md',
                        'service服务.md',
                        '节点运维.md',
                        'configmap和secret.md',
                        '计算资源管理.md',
                        'helm.md',
                        'dashboard.md',
                        '监控.md',
                    ]
                }
            ], '/postman/': [
                ''
            ], '/aws/': [
                {
                    title: "亚马逊使用",
                    children: [
                        '信息.md',
                        'aws-sdk.md',
                        'eks.md',
                    ]
                }
            ], '/aliyun/': [
                {
                    title: "阿里云使用",
                    children: [
                        '阿里云帐号信息.md',
                        '阿里云容器镜像服务.md',
                        'oss服务.md',
                        '阿里云存储服务.md',
                        '阿里云网络.md',
                        '阿里云容器服务.md',
                    ]
                }
            ], '/vmware/': [
                {
                    title: "VMWare相关产品使用",
                    children: [
                        'vsphere安装.md',
                        'vmware-tools.md',
                        'vmware序列号.md',
                        'vmware-workstation.md',
                        '操作系统安装.md',
                    ]
                }
            ], '/java-library/': [
                {
                    title: "Java相关库使用",
                    children: [
                        'awaitility使用.md',
                        'jsoup使用.md',
                        'hutool使用.md',
                        'htmlunit使用.md',
                        '日志框架.md',
                        '测试.md',
                        'rest-assured.md',
                        '缓存框架.md',
                        'stopwatch.md',
                        'lombok.md',
                        'json库.md',
                        'apache-commons-exec.md',
                        'ssh客户端.md',
                    ]
                }
            ], '/elasticsearch/': [
                {
                    title: "ElasticSearch、Kibana使用",
                    children: [
                        '概念.md',
                        '运行elasticsearch.md',
                        '常见错误.md',
                        '性能.md',
                        'curator.md',
                        'postman操作elasticsearch.md',
                        '客户端.md',
                    ]
                }
            ], '/terraform/': [
                {
                    title: "Terraform使用",
                    children: [
                        '安装.md',
                        'providers.md',
                    ]
                }
            ], '/golang/': [
                {
                    title: "Golang使用",
                    children: [
                        'goreleaser使用.md',
                    ]
                }
            ], '/python/': [
                {
                    title: "Python使用",
                    children: [
                        'centOS6升级python2.6到python2.7.md',
                        'xpath使用.md',
                    ]
                }
            ], '/chrome/': [
                {
                    title: "Chrome用法",
                    children: [
                        '',
                    ]
                }
            ], '/gcp/': [
                {
                    title: "谷歌云",
                    children: [
                        '谷歌信息.md',
                        'gce使用.md',
                        'gcp项目管理.md',
                        'gke.md',
                    ]
                }
            ], '/wireshark/': [
                {
                    title: "Wireshark使用",
                    children: [
                        '安装.md',
                    ]
                }
            ], '/gost/': [
                {
                    title: "Gost使用",
                    children: [
                        '',
                    ]
                }
            ], '/nodejs/': [
                {
                    title: "NodeJS使用",
                    children: [
                        'npm命令.md',
                    ]
                }
            ], '/macos/': [
                {
                    title: "macOS使用",
                    children: [
                        '和宿主机共享文件夹.md',
                        'xcode.md',
                        'cocoapods.md',
                        'homebrew.md',
                        'sourcetree.md',
                    ]
                }
            ], '/elk/': [
                {
                    title: "ELK",
                    children: [
                        '容器运行.md',
                        'kibana.md',
                    ]
                }
            ], '/android/': [
                {
                    title: "Android相关",
                    children: [
                        'android-studio.md',
                        'gradle.md',
                    ]
                }
            ], '/skywalking/': [
                {
                    title: "Skywalking",
                    children: [
                        'agent设置.md',
                        '运行demo.md',
                        'skywalking-ui.md',
                    ]
                }
            ], '/ruby/': [
                {
                    title: "Ruby",
                    children: [
                        '管理.md',
                    ]
                }
            ], '/toolset/': [
                ''
            ], '/selenium/': [
                ''
            ], '/canal/': [
                ''
            ], '/debezium/': [
                ''
            ], '/zookeeper/': [
                '',
                'curator-framework.md',
            ], '/benchmark/': [
                '',
                '监控方案.md',
                '基准测试工具.md',
                'IO测试.md',
                '网络测试.md',
                '应用或组件基准测试.md',
            ],
            '/': ['/'],
        }
    }
}
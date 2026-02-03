import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "我的分享",
  description: "分享技术和信息",
  // https://vitepress.dev/reference/site-config#ignoredeadlinks
  ignoreDeadLinks: true,
  themeConfig: {
    // 启用本地全文检索功能
    search: {
      provider: 'local'
    },
    // https://blog.csdn.net/delete_you/article/details/129938705
    // aside，设定为false将关闭右侧栏，文档内容会填充剩余空白部分
    aside: true,
    // outline设置为deep可以解析2-6层深度的标题嵌套
    outline: "deep",
    // 设置所有aside的标题
    outlineTitle: "页面导航",

    // https://vitepress.dev/reference/default-theme-config
    nav: [
      {
        text: 'IntelliJ破解', items: [
          { text: 'IDEA 2023.2.5 Ultimate破解', link: '/intellij/IDEA-2023.2.5-Ultimate-Windows11破解.md' },
        ]
      },
      // {
      //   text: '前端', items: [
      //     { text: 'UI、UX、平面设计', link: '/front-end/README.md' },
      //     { text: 'Vite', link: '/vite/README.md' },
      //     { text: 'Vue', link: '/vue/README.md' },
      //     { text: 'Nuxt', link: '/nuxt/README.md' },
      //     { text: 'Element-UI', link: '/element-ui/README.md' },
      //     { text: 'VitePress', link: '/cms/vitepress.md' },
      //     { text: 'Html、Javascript、CSS', link: '/html-js-css/README.md' },
      //     { text: 'Axios', link: '/axios/README.md' },
      //     { text: 'Electron', link: '/electron/README.md' },
      //     { text: '多媒体技术', link: '/多媒体技术/README.md' },
      //     { text: 'Adobe', link: '/adobe/README.md' },
      //     { text: 'Figma', link: '/figma/README.md' },
      //     { text: 'Android', link: '/android/README.md' },
      //     { text: 'uni-app', link: '/uni-app/README.md' },
      //     { text: 'SVG', link: '/svg/README.md' },
      //     { text: 'React', link: '/react/README.md' },
      //     { text: 'Qt', link: '/qt/README.md' },
      //   ]
      // }, {
      //   text: '后端', items: [
      //     { text: 'SpringBoot', link: '/springboot/快速创建spring-boot项目.md' },
      //     { text: 'SpringCloud', link: '/springcloud/README.md' },
      //     { text: 'Redis', link: '/redis/各种模式.md' },
      //     { text: 'Docker、Compose', link: '/docker/README.md' },
      //     { text: 'Java', link: '/java/README.md' },
      //     { text: 'Java 相关库', link: '/java-library/lombok.md' },
      //     { text: 'JMeter', link: '/jmeter/README.md' },
      //     { text: 'MySQL、MariaDB', link: '/mysql-n-mariadb/README.md' },
      //     { text: 'TiDB', link: '/tidb/README.md' },
      //     { text: 'NodeJS', link: '/nodejs/README.md' },
      //     { text: 'OpenResty', link: '/openresty/编译docker基础镜像.md' },
      //     { text: 'Maven', link: '/maven/README.md' },
      //     { text: 'Zookeeper', link: '/zookeeper/README.md' },
      //     { text: 'ElasticSearch', link: '/elasticsearch/README.md' },
      //     { text: 'Cassandra', link: '/cassandra/README.md' },
      //     { text: 'ScyllaDB', link: '/scylladb/README.md' },
      //     { text: 'RabbitMQ', link: '/rabbitmqt/README.md' },
      //     { text: 'RocketMQ', link: '/rocketmq/README.md' },
      //     { text: 'Skywalking', link: '/skywalking/README.md' },
      //     { text: 'Debezium', link: '/debezium/README.md' },
      //     { text: 'Kafka', link: '/kafka/README.md' },
      //     { text: '场景案例', link: '/场景案例/README.md' },
      //     { text: 'Dubbo', link: '/dubbo/README.md' },
      //   ]
      // }, {
      //   text: 'Linux', items: [
      //     { text: 'Linux 相关', link: '/linux/README.md' },
      //     { text: 'Ubuntu/Debian', link: '/linux/ubuntu-n-debian.md' },
      //     { text: 'CentOS', link: '/linux/centos.md' },
      //     { text: '命令行工具', link: '/linux/命令行工具列表.md' },
      //     { text: 'DNS', link: '/linux/dns.md' },
      //     { text: 'LVM 逻辑卷', link: '/linux/lvm逻辑卷管理.md' },
      //     { text: 'systemd、systemctl 服务', link: '/linux/systemd、systemctl服务.md' },
      //     { text: '搭建 NFS 服务器', link: '/linux/搭建nfs服务器.md' },
      //     { text: '操作系统页面缓存、目录项缓存、索引节点缓存', link: '/linux/操作系统页面缓存、目录项缓存、索引节点缓存.md' },
      //     { text: 'SSH 客户端', link: '/linux/ssh客户端.md' },
      //     { text: 'Shell 编程', link: '/shell-scripting/shell的概念.md' },
      //   ]
      // }, {
      //   text: '网络安全', items: [
      //     { text: '密码算法', link: '/密码算法/README.md' },
      //     { text: 'ssl、tls、https', link: '/ssl-tls-https/概念.md' },
      //   ]
      // }, {
      //   text: '测试和性能', items: [
      //     { text: '基准测试', link: '/benchmark/README.md' },
      //     { text: 'SpringBoot 项目的测试', link: '/springboot/spring-boot项目的测试.md' },
      //     { text: 'Java 相关测试框架', link: '/java-library/测试.md' },
      //   ]
      // }, {
      //   text: '运维', items: [
      //     { text: 'Ansible', link: '/ansible/README.md' },
      //     { text: 'Prometheus', link: '/prometheus/README.md' },
      //   ]
      // }, {
      //   text: '编程语言', items: [
      //     { text: 'Go', link: '/golang/README.md' },
      //     { text: 'Python', link: '/python/README.md' },
      //     { text: 'C/C++', link: '/c-plus/README.md' },
      //   ]
      // }, {
      //   text: '云提供商', items: [
      //     { text: '亚马逊', link: '/aws/README.md' },
      //     { text: '阿里云', link: '/aliyun/阿里云帐号信息.md' },
      //     { text: '谷歌云', link: '/gcp/README.md' },
      //     { text: 'Cloudflare', link: '/cloudflare/README.md' },
      //   ]
      // }, {
      //   text: '其他', items: [
      //     { text: '软件工程', link: '/软件工程/SDLC.md' },
      //     { text: 'Git', link: '/git/README.md' },
      //     { text: 'Dcli', link: '/dcli/README.md' },
      //     { text: '科学上网', link: '/科学上网/README.md' },
      //     { text: 'frp 内网穿透', link: '/frp/README.md' },
      //     { text: 'Gost', link: '/gost/README.md' },
      //     { text: 'Word 转换为 PDF', link: '/word-to-pdf/README.md' },
      //     { text: 'IDEA', link: '/idea/README.md' },
      //     { text: 'HTTP 相关', link: '/http/README.md' },
      //     { text: '英语学习', link: '/english/README.md' },
      //     { text: 'Windows 系统', link: '/windows/README.md' },
      //     { text: '腾讯', link: '/tencent/README.md' },
      //     { text: '小孩成长计划', link: '/小孩/README.md' },
      //     { text: '工具集', link: '/toolset/README.md' },
      //     { text: 'Future', link: '/future/README.md' },
      //     { text: 'MacOS', link: '/macos/README.md' },
      //     { text: 'Typora', link: '/typora/README.md' },
      //     { text: '数据结构和算法', link: '/数据结构和算法/README.md' },
      //     { text: 'VSCode', link: '/vscode/README.md' },
      //     { text: 'Firefox', link: '/firefox/README.md' },
      //     { text: '谷歌浏览器（Chrome）', link: '/chrome/README.md' },
      //     { text: '验证码', link: '/captcha/README.md' },
      //     { text: 'ffmpeg', link: '/ffmpeg/README.md' },
      //     { text: 'KVM', link: '/kvm/README.md' },
      //     { text: 'AI', link: '/ai/README.md' },
      //   ]
      // },
    ],

    sidebar: {
      '/springboot/': [
        {
          text: 'SpringBoot',
          items: [
            { text: '基础', link: '/springboot/README.md' },
            { text: '快速创建SpringBoot项目', link: '/springboot/快速创建spring-boot项目.md' },
            { text: 'SpringBoot项目的测试', link: '/springboot/spring-boot项目的测试.md' },
            { text: 'Spring Security', link: '/springboot/spring-security.md' },
            { text: 'Spring的Resource使用', link: '/springboot/spring的resource使用.md' },
            { text: 'SpringBoot的DataSourceInitializer使用', link: '/springboot/spring-boot的DataSourceInitializer使用.md' },
            { text: 'SpringBoot的PasswordEncoder使用', link: '/springboot/spring-boot的PasswordEncoder使用.md' },
            { text: 'SpringBoot外部Restcontroller', link: '/springboot/spring-boot外部restcontroller.md' },
            { text: '通过parent或dependencymanagement方式管理SpringBoot依赖', link: '/springboot/通过parent或dependencymanagement方式管理spring-boot依赖.md' },
            { text: '横向扩展', link: '/springboot/横向扩展.md' },
            { text: '任务调度框架', link: '/springboot/任务调度框架.md' },
            { text: '@Autowired和@Resource的区别', link: '/springboot/@Autowired和@Resource的区别.md' },
            { text: 'SpringBoot Actuator', link: '/springboot/spring-boot-actuator.md' },
            { text: 'Spring容器', link: '/springboot/spring容器.md' },
            { text: 'Spring AOP', link: '/springboot/spring-aop.md' },
            { text: 'Spring事务', link: '/springboot/spring事务.md' },
            { text: 'Spring MVC', link: '/springboot/spring-mvc.md' },
            { text: 'Swagger2和Knife4j', link: '/springboot/swagger2-knife4j.md' },
            { text: 'ibatis、mybatis、mybatis-plus', link: '/springboot/ibatis、mybatis、mybatis-plus.md' },
            { text: 'SpringBoot', link: '/springboot/spring-boot.md' },
            { text: 'SpringBoot Thymeleaf', link: '/springboot/spring-boot-thymeleaf.md' },
            { text: '连接池', link: '/springboot/连接池.md' },
          ]
        }
      ],
      '/springcloud/': [
        {
          text: 'SpringCloud',
          items: [
            { text: 'SpringCloud的基础', link: '/springcloud/README.md' },
            { text: 'Assistant示例', link: '/springcloud/assistant示例.md' },
            { text: '分布式ID', link: '/springcloud/分布式ID.md' },
            { text: '分布式事务', link: '/springcloud/分布式事务.md' },
            { text: '服务网关', link: '/springcloud/服务网关.md' },
            { text: '服务熔断、降级、限流', link: '/springcloud/服务熔断、降级、限流.md' },
            { text: '服务调用和负载均衡', link: '/springcloud/服务调用和负载均衡.md' },
          ]
        }
      ],
      '/redis/': [
        {
          text: 'Redis',
          items: [
            { text: '各种模式', link: '/redis/各种模式.md' },
            { text: 'Redisson', link: '/redis/redisson用法.md' },
            { text: '持久化', link: '/redis/持久化.md' },
            { text: '数据类型和命令', link: '/redis/数据类型和命令.md' },
            { text: '客户端', link: '/redis/客户端.md' },
            { text: '事务', link: '/redis/事务.md' },
            { text: '脚本编程', link: '/redis/脚本编程.md' },
            { text: '应用场景', link: '/redis/应用场景.md' },
            { text: '最佳实践', link: '/redis/最佳实践.md' },
            { text: '基准测试', link: '/redis/基准测试.md' },
          ]
        }
      ],
      '/html-js-css/': [
        {
          text: 'Html、Javascript、CSS',
          items: [
            { text: '基础', link: '/html-js-css/README.md' },
            { text: 'CSS', link: '/html-js-css/css.md' },
            { text: '媒体查询', link: '/html-js-css/media-query.md' },
            { text: 'JavaScript', link: '/html-js-css/javascript.md' },
          ]
        }
      ],
      '/vue/': [
        {
          text: 'Vue',
          items: [
            { text: '基础', link: '/vue/README.md' },
            { text: '脚手架创建项目', link: '/vue/脚手架创建项目.md' },
            { text: '集成 Element-UI', link: '/vue/集成element-ui.md' },
          ]
        }
      ],
      '/nuxt/': [
        {
          text: 'Nuxt',
          items: [
            { text: 'Nuxt基础', link: '/nuxt/README.md' },
            { text: '路由', link: '/nuxt/路由.md' },
            { text: '集成 Element-UI', link: '/nuxt/集成element-ui.md' },
          ]
        }
      ],
      '/密码算法/': [
        {
          text: '密码算法',
          items: [
            { text: '介绍', link: '/密码算法/README.md' },
            { text: '摘要算法', link: '/密码算法/摘要算法.md' },
            { text: '对称密码算法', link: '/密码算法/对称密码算法.md' },
            { text: '非对称密码算法', link: '/密码算法/非对称密码算法.md' },
            { text: 'JWT', link: '/密码算法/jwt.md' },
          ]
        }
      ],
      '/docker/': [
        {
          text: 'Docker、Compose',
          items: [
            { text: '基本用法', link: '/docker/README.md' },
            { text: 'Docker安装', link: '/docker/docker的安装.md' },
            { text: 'Docker Compose', link: '/docker/docker-compose.md' },
            { text: '存储卷', link: '/docker/docker-volume.md' },
            { text: 'Swarm', link: '/docker/docker-swarm.md' },
            { text: 'Docker命令', link: '/docker/docker命令.md' },
            { text: '资源限制', link: '/docker/资源限制.md' },
            { text: 'Dockerize', link: '/docker/dockerize用法.md' },
            { text: '加速代理', link: '/docker/dockerhub加速代理.md' },
            { text: '日志', link: '/docker/docker日志.md' },
            { text: 'entrypoint、cmd、command', link: '/docker/entrypoint、cmd、command.md' },
            { text: '镜像仓库', link: '/docker/镜像仓库.md' },
          ]
        }
      ],
      '/软件工程/': [
        {
          text: '软件工程',
          items: [
            { text: '软件开发生命周期', link: '/软件工程/SDLC.md' },
            { text: 'DrawIO使用', link: '/软件工程/drawio使用.md' },
            { text: 'UML建模与设计.md', link: '/软件工程/uml建模与设计.md' },
          ]
        }
      ],
      '/java-library/': [
        {
          text: 'Java相关库',
          items: [
            { text: 'Lombok', link: '/java-library/lombok.md' },
            { text: 'Awaitility', link: '/java-library/awaitility使用.md' },
            { text: 'Jsoup', link: '/java-library/jsoup使用.md' },
            { text: 'Hutool', link: '/java-library/hutool使用.md' },
            { text: 'HtmlUnit', link: '/java-library/htmlunit使用.md' },
            { text: '日志框架', link: '/java-library/日志框架.md' },
            { text: '测试', link: '/java-library/测试.md' },
            { text: 'rest-assured', link: '/java-library/rest-assured.md' },
            { text: '缓存框架', link: '/java-library/缓存框架.md' },
            { text: 'Stopwatch', link: '/java-library/stopwatch.md' },
            { text: 'JSON相关库', link: '/java-library/json库.md' },
            { text: 'Apache Commons', link: '/java-library/apache-commons.md' },
            { text: 'SSH客户端库', link: '/java-library/ssh客户端.md' },
          ]
        }
      ],
      '/jmeter/': [
        {
          text: 'JMeter',
          items: [
            { text: '基础', link: '/jmeter/README.md' },
            { text: '自定义插件', link: '/jmeter/自定义插件.md' },
            { text: '定时器', link: '/jmeter/定时器.md' },
          ]
        }
      ],
      '/git/': [
        {
          text: 'Git',
          items: [
            { text: '基础', link: '/git/README.md' },
            { text: '分支', link: '/git/分支.md' },
            { text: '标签', link: '/git/标签.md' },
            { text: '基于分支和标签的版本管理', link: '/git/基于分支和标签的版本管理.md' },
            { text: '初始化非空目录指向远程仓库', link: '/git/初始化非空目录指向远程仓库.md' },
            { text: 'git凭证管理', link: '/git/git凭证管理.md' },
            { text: 'gitee', link: '/git/gitee.md' },
            { text: 'github', link: '/git/github.md' },
            { text: '设置自签名ssl证书不验证', link: '/git/设置自签名ssl证书不验证.md' },
            { text: '原理', link: '/git/原理.md' },
            { text: '变基', link: '/git/变基.md' },
            { text: '命令', link: '/git/命令.md' },
            { text: 'Pull Request', link: '/git/pr.md' },
          ]
        }
      ],
      '/shell-scripting/': [
        {
          text: 'Shell 编程',
          items: [
            { text: 'Shell 的概念', link: '/shell-scripting/shell的概念.md' },
            { text: 'Zsh 使用', link: '/shell-scripting/zsh使用.md' },
            { text: '语法基础', link: '/shell-scripting/语法基础.md' },
            { text: '在脚本头声明脚本的用法', link: '/shell-scripting/在脚本头声明脚本的用法.md' },
            { text: '设置 Shell 行为的命令', link: '/shell-scripting/设置shell行为的命令.md' },
            { text: '函数的用法', link: '/shell-scripting/函数的用法.md' },
            { text: '标准 Shell 脚本结构', link: '/shell-scripting/标准shell脚本结构.md' },
            { text: '数组', link: '/shell-scripting/数组.md' },
            { text: '关联数组', link: '/shell-scripting/关联数组.md' },
            { text: 'IO 重定向', link: '/shell-scripting/io-redirection.md' },
          ]
        }
      ],
      '/benchmark/': [
        {
          text: '基准测试',
          items: [
            { text: '基础', link: '/benchmark/README.md' },
            { text: 'Gatling', link: '/benchmark/gatling.md' },
            { text: 'CPU 测试', link: '/benchmark/CPU测试.md' },
            { text: 'IO 测试', link: '/benchmark/IO测试.md' },
            { text: '内存测试', link: '/benchmark/内存测试.md' },
            { text: '网络测试', link: '/benchmark/网络测试.md' },
            { text: '基准测试工具', link: '/benchmark/基准测试工具.md' },
            { text: '应用或组件基准测试', link: '/benchmark/应用或组件基准测试.md' },
            { text: '监控方案', link: '/benchmark/监控方案.md' },
          ]
        }
      ],
      '/golang/': [
        {
          text: 'Go语言',
          items: [
            { text: '基础', link: '/golang/README.md' },
            { text: 'goreleaser', link: '/golang/goreleaser使用.md' },
          ]
        }
      ],
      '/aws/': [
        {
          text: '亚马逊',
          items: [
            { text: '基础', link: '/aws/README.md' },
            { text: '信息', link: '/aws/信息.md' },
            { text: 'SDK', link: '/aws/aws-sdk.md' },
            { text: 'Elastic Kubernetes Service(EKS)', link: '/aws/eks.md' },
          ]
        }
      ],
      '/aliyun/': [
        {
          text: '阿里云',
          items: [
            { text: '帐号信息', link: '/aliyun/阿里云帐号信息.md' },
            { text: '容器镜像服务', link: '/aliyun/阿里云容器镜像服务.md' },
            { text: 'OSS 服务', link: '/aliyun/oss服务.md' },
            { text: '存储服务', link: '/aliyun/阿里云存储服务.md' },
            { text: '网络', link: '/aliyun/阿里云网络.md' },
            { text: '容器服务', link: '/aliyun/阿里云容器服务.md' },
            { text: 'ECS', link: '/aliyun/云服务器.md' },
            { text: 'ROS', link: '/aliyun/ROS.md' },
          ]
        }
      ],
      '/gcp/': [
        {
          text: '谷歌云',
          items: [
            { text: '基础', link: '/gcp/README.md' },
            { text: '信息', link: '/gcp/谷歌信息.md' },
            { text: 'GCE', link: '/gcp/gce使用.md' },
            { text: '项目管理', link: '/gcp/gcp项目管理.md' },
            { text: 'GKE', link: '/gcp/gke.md' },
          ]
        }
      ],
      '/ssl-tls-https/': [
        {
          text: 'ssl、tls、https',
          items: [
            { text: '概念', link: '/ssl-tls-https/概念.md' },
            { text: '密钥和证书的管理', link: '/ssl-tls-https/密钥和证书的管理.md' },
            { text: 'https中间人攻击', link: '/ssl-tls-https/https中间人攻击.md' },
          ]
        }
      ],
      '/mysql-n-mariadb/': [
        {
          text: 'MySQL、MariaDB',
          items: [
            { text: '基础', link: '/mysql-n-mariadb/README.md' },
            { text: 'Binlog', link: '/mysql-n-mariadb/binlog.md' },
            { text: 'Cli 快捷键', link: '/mysql-n-mariadb/mysql-cli快捷键.md' },
            { text: '客户端', link: '/mysql-n-mariadb/mysql客户端工具.md' },
            { text: '锁', link: '/mysql-n-mariadb/mysql锁.md' },
            { text: 'performance_schema', link: '/mysql-n-mariadb/performance_schema.md' },
            { text: 'SQL', link: '/mysql-n-mariadb/sql.md' },
            { text: 'Too many connections', link: '/mysql-n-mariadb/too-many-connections处理.md' },
            { text: '事务', link: '/mysql-n-mariadb/事务.md' },
            { text: '内存', link: '/mysql-n-mariadb/内存.md' },
            { text: '函数', link: '/mysql-n-mariadb/函数.md' },
            { text: '分区表', link: '/mysql-n-mariadb/分区表.md' },
            { text: '分库分表', link: '/mysql-n-mariadb/分库分表.md' },
            { text: '初始化数据库', link: '/mysql-n-mariadb/初始化数据库.md' },
            { text: '原理', link: '/mysql-n-mariadb/原理.md' },
            { text: '变量', link: '/mysql-n-mariadb/变量.md' },
            { text: '启用或关闭慢日志', link: '/mysql-n-mariadb/启用或关闭慢日志.md' },
            { text: '命令', link: '/mysql-n-mariadb/命令.md' },
            { text: '基准测试', link: '/mysql-n-mariadb/基准测试.md' },
            { text: '备份和还原', link: '/mysql-n-mariadb/备份和还原.md' },
            { text: '存储过程', link: '/mysql-n-mariadb/存储过程.md' },
            { text: '性能分析', link: '/mysql-n-mariadb/性能分析.md' },
            { text: '版本和兼容性', link: '/mysql-n-mariadb/版本和兼容性.md' },
            { text: '用户和权限管理', link: '/mysql-n-mariadb/用户和权限管理.md' },
            { text: '系统数据库和表', link: '/mysql-n-mariadb/系统数据库和表.md' },
            { text: '自动更新数据库', link: '/mysql-n-mariadb/自动更新数据库.md' },
            { text: '计算数据和索引大小', link: '/mysql-n-mariadb/计算数据和索引大小.md' },
            { text: '运行 MySQL 和 MariaDB', link: '/mysql-n-mariadb/运行mysql和mariadb.md' },
            { text: '配置参数', link: '/mysql-n-mariadb/配置参数.md' },
            { text: '主从配置', link: '/mysql-n-mariadb/主从配置.md' },
          ]
        }
      ],
      '/idea/': [
        {
          text: 'IDEA',
          items: [
            { text: '基础', link: '/idea/README.md' },
            { text: '创建并管理项目', link: '/idea/创建并管理项目.md' },
            { text: '多重光标同时编辑', link: '/idea/多重光标同时编辑.md' },
            { text: '快捷键', link: '/idea/快捷键.md' },
          ]
        }
      ],
      '/java/': [
        {
          text: 'Java',
          items: [
            { text: '基础', link: '/java/README.md' },
            { text: 'Arthas', link: '/java/arthas使用.md' },
            { text: 'CPU 性能分析', link: '/java/cpu性能分析.md' },
            { text: 'double类型运算精度问题', link: '/java/double类型运算精度问题.md' },
            { text: 'entity、vo、dto、po、do', link: '/java/entity、vo、dto、po、do.md' },
            { text: 'java 命令行参数', link: '/java/java命令行参数.md' },
            { text: 'JDK8 新特性', link: '/java/JDK8新特性.md' },
            { text: 'JDK 相关工具', link: '/java/jdk相关工具.md' },
            { text: 'JMH', link: '/java/jmh.md' },
            { text: 'JProfiler', link: '/java/jprofiler用法.md' },
            { text: 'JUC', link: '/java/JUC.md' },
            { text: 'JVM 内存', link: '/java/jvm内存.md' },
            { text: 'Tomcat', link: '/java/tomcat.md' },
            { text: '异常', link: '/java/异常.md' },
            { text: '生产环境问题排查', link: '/java/生产环境问题排查.md' },
          ]
        }
      ],
      '/python/': [
        {
          text: 'Python',
          items: [
            { text: '基础', link: '/python/README.md' },
            { text: 'Xpath', link: '/python/xpath使用.md' },
            { text: 'CentOS6 升级 Python2.6 到 Python2.7', link: '/python/centOS6升级python2.6到python2.7.md' },
          ]
        }
      ],
      '/nodejs/': [
        {
          text: 'NodeJS',
          items: [
            { text: '基础', link: '/nodejs/README.md' },
            { text: 'Npm', link: '/nodejs/npm命令.md' },
          ]
        }
      ],
      '/openresty/': [
        {
          text: 'OpenResty',
          items: [
            { text: '基础', link: '/openresty/README.md' },
            { text: 'Https 设置', link: '/openresty/https设置.md' },
            { text: 'lua-resty-limit-traffic 库使用', link: '/openresty/lua-resty-limit-traffic库使用.md' },
            { text: 'Lua 脚本', link: '/openresty/lua脚本.md' },
            { text: 'x-forwarded-for 用法', link: '/openresty/x-forwarded-for用法.md' },
            { text: '横向扩展', link: '/openresty/横向扩展.md' },
            { text: '监控', link: '/openresty/监控.md' },
            { text: '编译容器基础镜像', link: '/openresty/编译docker基础镜像.md' },
            { text: '通过环境变量传递参数', link: '/openresty/通过环境变量传递参数.md' },
          ]
        }
      ],
      '/english/': [
        {
          text: '英语学习',
          items: [
            { text: '基础', link: '/english/README.md' },
            { text: 'TODO 列表', link: '/english/todo列表.md' },
            { text: '主谓一致', link: '/english/主谓一致.md' },
            { text: '什么是语法', link: '/english/什么是语法.md' },
            { text: '句型', link: '/english/句型.md' },
            { text: '句子成分', link: '/english/句子成分.md' },
            { text: '复合句或从句', link: '/english/复合句或从句.md' },
            { text: '强调句', link: '/english/强调句.md' },
            { text: '时态', link: '/english/时态.md' },
            { text: '短语', link: '/english/短语.md' },
            { text: '词性或词类', link: '/english/词性或词类.md' },
            { text: '语态', link: '/english/语态.md' },
            { text: '语气', link: '/english/语气.md' },
            { text: '音标', link: '/english/音标.md' },
            { text: '实践阅读', link: '/english/实践阅读.md' },
            { text: '实践翻译', link: '/english/实践翻译.md' },
          ]
        }
      ],
      '/maven/': [
        {
          text: 'Maven',
          items: [
            { text: '基础', link: '/maven/README.md' },
            { text: 'DependencyManagement', link: '/maven/dependencymanagement用法.md' },
            { text: 'Maven Wrapper', link: '/maven/maven-wrapper用法.md' },
            { text: '仓库', link: '/maven/maven仓库.md' },
            { text: '发布可执行 Jar', link: '/maven/maven发布带main函数可执行jar.md' },
            { text: '插件', link: '/maven/maven插件.md' },
            { text: 'Optional 用法', link: '/maven/maven的optional用法.md' },
            { text: 'Scope 用法', link: '/maven/maven的scope用法.md' },
            { text: 'mvn 命令', link: '/maven/mvn命令.md' },
            { text: '多模块配置', link: '/maven/多模块配置.md' },
            { text: '指定 JDK 版本', link: '/maven/指定jdk版本.md' },
          ]
        }
      ],
      '/zookeeper/': [
        {
          text: 'Zookeeper',
          items: [
            { text: '基础', link: '/zookeeper/README.md' },
            { text: 'Curator Framework', link: '/zookeeper/curator-framework.md' },
          ]
        }
      ],
      '/elasticsearch/': [
        {
          text: 'ElasticSearch',
          items: [
            { text: '基础', link: '/elasticsearch/README.md' },
            { text: 'Curator', link: '/elasticsearch/curator.md' },
            { text: 'Postman 操作', link: '/elasticsearch/postman操作elasticsearch.md' },
            { text: '常见错误', link: '/elasticsearch/常见错误.md' },
          ]
        }
      ],
      '/prometheus/': [
        {
          text: 'Prometheus',
          items: [
            { text: '基础', link: '/prometheus/README.md' },
            { text: 'Exporter', link: '/prometheus/exporter使用.md' },
            { text: 'Prometheus拉取目标配置', link: '/prometheus/prometheus拉取目标配置.md' },
            { text: 'Prometheus指标类型', link: '/prometheus/prometheus指标类型.md' },
            { text: 'Prometheus标签', link: '/prometheus/prometheus标签.md' },
            { text: 'Prometheus监控Spring Boot', link: '/prometheus/prometheus监控spring.md' },
            { text: 'Prometheus自定义Exporter', link: '/prometheus/prometheus自定义exporter.md' },
            { text: 'PromQL', link: '/prometheus/promql.md' },
            { text: '使用Docker Compose运行Prometheus', link: '/prometheus/使用docker-compose运行prometheus.md' },
            { text: '告警设置', link: '/prometheus/告警设置.md' },
            { text: 'Grafana', link: '/prometheus/grafana.md' },
          ]
        }
      ],
      '/skywalking/': [
        {
          text: 'Skywalking',
          items: [
            { text: '基础', link: '/skywalking/README.md' },
            { text: 'Agent设置', link: '/skywalking/agent设置.md' },
            { text: 'Skywalking UI', link: '/skywalking/skywalking-ui.md' },
            { text: '运行Demo', link: '/skywalking/运行demo.md' },
          ]
        }
      ],
      '/react/': [
        {
          text: 'React',
          items: [
            { text: '基础', link: '/react/README.md' },
            { text: 'Redux', link: '/react/redux.md' },
          ]
        }
      ],
      '/macos/': [
        {
          text: 'MacOS',
          items: [
            { text: '基础', link: '/macos/README.md' },
            { text: 'Cocoapods', link: '/macos/cocoapods.md' },
            { text: 'HomeBrew', link: '/macos/homebrew.md' },
            { text: 'Objective-C', link: '/macos/objective-c.md' },
            { text: 'Swift', link: '/macos/swift.md' },
            { text: 'Xcode', link: '/macos/xcode.md' },
          ]
        }
      ],
      '/数据结构和算法/': [
        {
          text: '数据结构和算法',
          items: [
            { text: '基础', link: '/数据结构和算法/README.md' },
            { text: '布隆过滤器', link: '/数据结构和算法/布隆过滤器.md' },
          ]
        }
      ],
      '/c-plus/': [
        {
          text: 'C/C++',
          items: [
            { text: '基础', link: '/c-plus/README.md' },
            { text: 'GDB', link: '/c-plus/gdb.md' },
          ]
        }
      ],
      '/uni-app/': [
        {
          text: 'uni-app',
          items: [
            { text: '基础', link: '/uni-app/README.md' },
            { text: '组件库', link: '/uni-app/组件库.md' },
            { text: 'Html5 Plus', link: '/uni-app/html5plus.md' },
            { text: '打包发布', link: '/uni-app/打包发布.md' },
          ]
        }
      ],
      '/android/': [
        {
          text: '安卓',
          items: [
            { text: '基础', link: '/android/README.md' },
            { text: '模拟器', link: '/android/模拟器.md' },
          ]
        }
      ],
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/dexterleslie1' }
    ]
  }
})

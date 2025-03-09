import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "我的分享",
  description: "分享技术和信息",
  themeConfig: {
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
        text: '首页', link: '/'
      }, {
        text: '前端', items: [
          { text: 'Vite', link: '/vite/README.md' },
          { text: 'Vue', link: '/vue/脚手架创建项目.md' },
          { text: 'Nuxt', link: '/nuxt/README.md' },
          { text: 'Element-UI', link: '/element-ui/README.md' },
          { text: 'VitePress', link: '/cms/vitepress.md' },
          { text: 'Html、Javascript、CSS', link: '/html-js-css/css.md' },
        ]
      }, {
        text: '后端', items: [
          { text: 'SpringBoot', link: '/springboot/快速创建spring-boot项目.md' },
          { text: 'SpringCloud', link: '/springcloud/README.md' },
          { text: 'Redis', link: '/redis/各种模式.md' },
        ]
      }, {
        text: 'Linux', items: [
          { text: 'DNS', link: '/linux/dns.md' },
        ]
      }, {
        text: 'CloudFlare', link: '/cloudflare/README.md'
      },
    ],

    sidebar: {
      '/springboot/': [
        {
          text: 'SpringBoot',
          items: [
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
            { text: 'Lua脚本', link: '/redis/Lua脚本.md' },
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
            { text: 'CSS', link: '/html-js-css/css.md' },
          ]
        }
      ],
      '/vue/': [
        {
          text: 'Vue',
          items: [
            { text: '脚手架创建项目', link: '/vue/脚手架创建项目.md' },
            { text: '指令', link: '/vue/指令.md' },
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
            { text: '数据交互和跨域', link: '/nuxt/数据交互和跨域.md' },
          ]
        }
      ],
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/dexterleslie1' }
    ]
  }
})

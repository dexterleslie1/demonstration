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
      { text: '首页', link: '/' },
      {
        text: '前端', items: [
          { text: 'Vite', link: '/vite/README.md' },
          { text: 'VitePress', link: '/cms/vitepress.md' },
        ]
      },
      {
        text: 'Linux', items: [
          { text: 'DNS', link: '/linux/dns.md' },
        ]
      },
      { text: 'CloudFlare', link: '/cloudflare/README.md' },
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/dexterleslie1' }
    ]
  }
})

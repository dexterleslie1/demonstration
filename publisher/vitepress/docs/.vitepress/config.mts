import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "我的分享",
  description: "分享技术和信息",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: '首页', link: '/' },
      {
        text: '前端', items: [
          { text: 'VitePress', link: '/cms/vitepress.md' },
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/dexterleslie1' }
    ]
  }
})

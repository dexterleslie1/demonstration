## CMS、项目文档审计跟踪、知识库关系

为了让您有更直观的理解，我们可以做一个简单的比喻：

- CMS 就像一家印刷厂。它关心的是如何高效、规范地印刷出书籍、传单（内容）。它提供纸张、油墨、排版工具（模板、编辑器）。
- 知识库 就像一家图书馆。它关心的是如何收集有价值的书籍（知识），并对它们进行分类、编目、上架（组织），方便读者快速找到并学习。印刷厂（CMS）可以为图书馆（知识库）生产书籍。
- 项目文档审计跟踪 就像图书馆的藏书版本管理和借阅记录系统。它记录了一本珍贵的古籍（如项目文档）有多少个版本（手稿、初版、修订版），谁在什么时候修改了哪些部分（版本历史），以及谁借阅过（访问审计）。这保证了藏书的完整性、可追溯性和安全性。

## 需求清单

>说明：
>
>- 需求清单可以参考`阿里云帮助文档库`这个产品收集。
>- 资料显示Confluence可以同时满足CMS、项目文档跟踪、知识库、产品文档统一管理。（未做测试验证）

| 功能                                         | 优先级 | 说明                                                         |
| -------------------------------------------- | ------ | ------------------------------------------------------------ |
| CMS、项目文档跟踪、知识库、产品文档统一管理  | 高     |                                                              |
| 支持Markdown格式编写文档                     | 高     |                                                              |
| Markdown格式编写文档时支持目录导航功能       | 高     |                                                              |
| 项目文档审计跟踪                             | 高     |                                                              |
| 数据导出                                     | 中     | 支持按照目前的分类结构导出为Markdown格式的文档以实现文档数据备份。 |
| 提供各类型文档专用模板或者支持自定义文档模板 | 中     |                                                              |
| 支持自定义审批和审阅流程以适应企业需求       | 低     |                                                              |
| 设置文档分类或者文档权限                     | 低     |                                                              |

## 搜索关键词

Google 搜索的关键词：open source cms system, open source kms(Knowledge Management System) system, markdown as source cms, static site generator, markdown-centered static site generator

AI 询问的关键词：

- 企业级文档管理系统有那些？

## 开源的内容管理系统

- VitePress

  >说明：目前使用VitePress作为个人知识库管理系统。
  >
  >[demo-cms-system/demo-vitepress · dexterleslie/demonstration - 码云 - 开源中国](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cms-system/demo-vitepress)

- vuepress

  >[demo-cms-system/demo-vuepress · dexterleslie/demonstration - 码云 - 开源中国](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cms-system/demo-vuepress)

- hugo

  没有动手做 demo 体验，通过教学视频了解到是基于 markdown 编写内容的。

- docsify / docute

  todo 未研究

- hexo

  todo 未研究

- gitbook

  todo 未研究
## 主流游戏开发引擎

本文简要介绍目前业界常用的游戏开发引擎及其特点，便于选型与入门。

---

### Unity

- **定位**：跨平台通用引擎，2D / 3D 均可，独立游戏与商业项目都大量使用
- **语言**：C#
- **特点**：资源与生态成熟、学习资料多、Asset Store 丰富；适合手游、PC、主机及 XR
- **官网**：[https://unity.com](https://unity.com)

---

### Unreal Engine（虚幻引擎）

- **定位**：偏重视觉效果与大型 3D 项目，3A 与影视虚拟制作常用
- **语言**：C++、蓝图（Blueprint）可视化脚本
- **特点**：渲染与物理能力强、开源（源码可获取）；学习曲线相对陡峭
- **官网**：[https://www.unrealengine.com](https://www.unrealengine.com)

---

### Godot

- **定位**：开源、轻量、适合独立开发与中小型项目
- **语言**：GDScript、C#、C++ 等
- **特点**：完全免费开源、体积小、2D 支持出色；生态相对 Unity / UE 仍在成长
- **官网**：[https://godotengine.org](https://godotengine.org)

---

### Cocos（Cocos Creator）

- **定位**：偏 2D / 轻量 3D，国内手游与小游戏场景常见
- **语言**：TypeScript / JavaScript
- **特点**：对微信小游戏等国内平台适配好；适合休闲、卡牌、轻度玩法
- **官网**：[https://www.cocos.com](https://www.cocos.com)

---

### CryEngine

- **定位**：高画质 3D 引擎，以第一人称射击等写实场景闻名
- **语言**：C++、C#（部分版本）
- **特点**：画面与物理表现突出；社区与文档体量小于 Unity / UE
- **官网**：[https://www.cryengine.com](https://www.cryengine.com)

---

### 其他常见选择

| 引擎 / 框架 | 简要说明 |
| --- | --- |
| **GameMaker** | 偏 2D，脚本友好，适合独立与原型 |
| **Construct** | 可视化 / 事件驱动，几乎不用写代码即可做 2D 游戏 |
| **Phaser** | 基于 HTML5 / Web 的 2D 游戏框架 |
| **Defold** | 轻量 2D，适合手游与跨平台 |
| **RPG Maker** | 面向 RPG 的专用工具，快速出原型 |
| **自研引擎** | 大厂或特殊需求项目常基于自研或深度定制 |

---

### 哪些引擎可用于 Steam 开发与上架

Steam **不限制使用哪种引擎**，只要能打出 Windows（以及可选的 macOS / Linux）可执行包，并完成 [Steamworks](https://partner.steamgames.com/) 接入与审核，就可以上架。上文提到的引擎大致情况如下：

| 引擎 / 框架 | 能否用于 Steam | 说明 |
| --- | --- | --- |
| **Unity** | 很适合 | Steam 上最常见之一；官方/社区有完善的 Steamworks 插件（成就、云存档、联机等） |
| **Unreal Engine** | 很适合 | 大量 3A / 独立作在 Steam 发行；引擎内置或官方插件可对接 Steamworks |
| **Godot** | 可以 | 独立游戏上架 Steam 越来越多；通过社区插件对接 Steamworks |
| **CryEngine** | 可以 | 本身有多款 Steam 成作；支持桌面端打包与平台 SDK |
| **Cocos** | 可以，但不常见 | 能导出桌面端再上架，但生态更偏手游 / 小游戏，Steam 案例相对少 |
| **GameMaker** | 可以 | 大量独立 2D 游戏通过它上架 Steam |
| **Construct** | 可以 | 支持导出桌面端并发布到 Steam |
| **Defold** | 可以 | 支持桌面导出与 Steamworks 相关集成 |
| **RPG Maker** | 可以 | Steam 上有大量 RPG Maker 作品 |
| **Phaser** | 一般不直接用 | 偏 Web / HTML5；若要上 Steam 通常需再用 Electron 等封装成桌面应用 |
| **自研引擎** | 可以 | 只要能出桌面包并接 Steamworks，与引擎品牌无关 |

补充要点：

1. **上架门槛在平台侧，不在引擎侧**：需注册 Steamworks 合作伙伴、缴纳 Steam Direct 费用，并提交构建与商店信息审核。
2. **Steamworks**：成就、好友、云存档、创意工坊、联机等能力，需按引擎接入对应 SDK / 插件；纯“能跑起来”的 exe 也可以先上架，再逐步加这些功能。
3. **实践上最稳妥**：想做 PC 并向 Steam 发行，优先 **Unity、Unreal、Godot、GameMaker** 等桌面与 Steam 资料都更成熟的方案。

---

### 如何简单选型

1. **手游 / 跨平台 / 学习成本优先** → 优先考虑 Unity 或 Cocos
2. **高品质 3D / 主机 / 影视级画面** → 优先考虑 Unreal Engine
3. **开源、免费、独立开发** → 优先考虑 Godot
4. **Web / 小游戏** → Cocos、Phaser 或各平台专用方案
5. **极简 2D / 无编程背景** → GameMaker、Construct、RPG Maker 等

实际选型还需结合团队技术栈、目标平台、授权费用与项目周期综合判断。

## 概念

### 一句话概括

**FFmpeg 是一个开源的、功能极其强大的跨平台音视频处理工具和开发库。** 你可以把它想象成音视频领域的“瑞士军刀”。

---

### 一个生动的比喻

想象一下你对一个视频文件想做以下操作：
*   **转换格式**：把 `.mkv` 文件变成 `.mp4` 文件。
*   **提取音频**：从电影里把背景音乐截取成 `.mp3`。
*   **压缩体积**：把一个很大的视频文件变小，方便通过网络发送。
*   **剪辑拼接**：剪掉视频的开头或结尾，或者把几个短视频合并成一个。

**FFmpeg 就是能帮你完成所有这些事情（甚至远不止这些）的命令行工具。**

---

### 核心组成部分

FFmpeg 这个名字其实代表了一个完整的项目，主要由以下几个核心部分组成：

1.  **`ffmpeg`**：这是最常用的命令行工具本身。它用于对音视频文件进行转码（转换格式和编码）、滤镜处理（如加水印、缩放、裁剪）等。
2.  **`ffplay`**：一个非常简单但功能强大的媒体播放器。它通常用于快速预览视频文件或测试流媒体地址，不需要安装庞大的播放器软件。
3.  **`ffprobe`**：一个多媒体流分析工具。它不用来播放或转换，而是用来查看媒体文件的“元信息”，比如视频的编码格式、分辨率、码率、音频的采样率、声道数等。当你不知道一个视频的具体参数时，用它来查看非常方便。
4.  **核心代码库**：除了命令行工具，FFmpeg 还提供了一系列功能强大的库（如 `libavcodec`——解码/编码库，`libavformat`——封装格式处理库等）。许多知名的软件（如 VLC 媒体播放器、暴风影音、格式工厂、Blender、甚至 YouTube 和 iTunes）都在底层直接或间接地使用了 FFmpeg 的代码库。

---

### 主要功能和用途

FFmpeg 的功能几乎涵盖了音视频处理的方方面面：

*   **格式转换（转码）**：这是最基本也是最常用的功能，在不同容器格式（如 AVI, MKV, MP4, MOV, FLV）和编码格式（如 H.264, H.265, VP9, AAC, MP3）之间进行转换。
*   **视频/音频提取**：从视频中分离出纯音频，或从文件中提取视频流。
*   **压缩视频/音频**：通过调整编码参数（码率、分辨率、帧率等）来减小文件体积。
*   **基本编辑**：裁剪、拼接、缩放、旋转视频，调整音量等。
*   **屏幕录制**：录制你的电脑屏幕或摄像头画面。
*   **流媒体处理**：可以抓取、推流和转换实时流媒体（如 RTMP, HLS, DASH）。
*   **添加水印、字幕**：为视频叠加图片水印或硬字幕。
*   **音视频滤镜**：应用复杂的滤镜效果，如降噪、色彩校正、模糊、加速/慢放等。

---

### 一个简单的例子

假设你有一个名为 `input.mkv` 的视频文件，你想把它转换成兼容性更好的 `output.mp4` 文件，并且把视频尺寸缩小到 1280x720。

你可以使用以下 FFmpeg 命令：

```bash
ffmpeg -i input.mkv -s 1280x720 output.mp4
```

*   `-i input.mkv`：指定输入文件。
*   `-s 1280x720`：指定输出视频的分辨率。
*   `output.mp4`：指定输出文件名。

在命令行中执行这条命令，FFmpeg 就会开始工作。

### 总结

| 特性           | 描述                                                         |
| :------------- | :----------------------------------------------------------- |
| **本质**       | 命令行工具 + 开源代码库                                      |
| **核心功能**   | 音视频的录制、转码、转换、流媒体处理                         |
| **优势**       | **免费、开源、功能全面、高效、跨平台**（Windows, macOS, Linux） |
| **学习曲线**   | 命令行操作有一定门槛，但学会后效率极高，可以批量自动化处理。 |
| **图形化界面** | 有很多软件（如 HandBrake, ShanaEncoder）是 FFmpeg 的图形界面外壳，让普通用户也能轻松使用其核心功能。 |

简单来说，**FFmpeg 是音视频处理领域事实上的行业标准，是几乎所有相关软件背后的“发动机”**。无论是普通用户进行简单的格式转换，还是开发者构建复杂的流媒体服务，FFmpeg 都是不可或缺的核心工具。



## 为何多个版本并存

不同场景对编解码器需求不同

```
# 场景1：传统嵌入式设备（资源受限）
推荐版本: FFmpeg 2.8/3.4
原因: 内存占用小，API稳定，硬件要求低

# 场景2：现代云转码服务  
推荐版本: FFmpeg 5.x/6.x
原因: 需要最新编解码器，性能优化好

# 场景3：桌面应用集成
推荐版本: FFmpeg 4.4
原因: 平衡功能和稳定性，文档完善

安防监控行业：
- 需要: H.264, H.265, MJPEG
- 不需要: AV1, VP9
- 锁定版本: FFmpeg 3.4（稳定够用）

视频网站转码：
- 需要: AV1, VP9, HEVC 最新优化  
- 锁定版本: FFmpeg 6.x（追求性能）

传统电视广播：
- 需要: MPEG-2, MPEG-TS 稳定支持
- 锁定版本: FFmpeg 2.8（久经考验）
```

当前并存的典型版本

| 版本           | 主要用户群             | 存在原因              |
| -------------- | ---------------------- | --------------------- |
| **FFmpeg 2.8** | 老嵌入式设备、传统安防 | 极致稳定，资源占用小  |
| **FFmpeg 3.4** | 企业级系统、LTS发行版  | API稳定，经过长期测试 |
| **FFmpeg 4.4** | ⭐主流应用、新项目      | 功能完善，生态支持好  |
| **FFmpeg 5.x** | 需要新特性的项目       | 性能优化，新编解码器  |
| **FFmpeg 6.x** | 技术前沿项目           | 实验性功能，最新技术  |



## 安装 - macOS13.0.1使用brew

>提示：在安装过程中提示如下错误，所以放弃在此版本平台安装ffmpeg。
>
>```sh
>==> meson setup build_staging -Dintrospection=disabled --localstatedir=/usr/local/var -Dgio_module_dir=/usr/local/lib/gio/modules -Dbsymbolic_functions=false -Ddtrace=false -Druntime_dir=/usr/local/var/run -Dte
>Last 15 lines from /Users/dexterleslie/Library/Logs/Homebrew/glib/01.meson.log:
>--buildtype=release
>--wrap-mode=nofallback
>
>The Meson build system
>Version: 1.9.1
>Source dir: /private/tmp/glib-20251028-44527-q3alu8/glib-2.86.1
>Build dir: /private/tmp/glib-20251028-44527-q3alu8/glib-2.86.1/build_staging
>Build type: native build
>DEPRECATION: Option "dtrace" value 'false' is replaced by 'disabled'
>Project name: glib
>Project version: 2.86.1
>
>meson.build:1:0: ERROR: Compiler clang cannot compile programs.
>
>A full log can be found at /private/tmp/glib-20251028-44527-q3alu8/glib-2.86.1/build_staging/meson-logs/meson-log.txt
>
>
>
>Error: You are using macOS 13.
>We (and Apple) do not provide support for this old version.
>
>```
>
>

通过环境变量设置`brew install`安装过程中使用的`socks5`代理服务

```bash
export ALL_PROXY=socks5h://192.168.235.128:1080
```

使用 `homebrew` 安装 `ffmpeg`

```sh
brew install ffmpeg
```

## 安装 - macOS13.0.1从源代码

访问 https://ffmpeg.org/download.html#releases 下载ffmpeg-4.2.11.tar.gz

安装pkg-config

```sh
# 设置brew代理
export ALL_PROXY=socks5h://192.168.235.128:1080

# 安装pkg-config
brew install pkg-config

# 检查是否成功安装
pkg-config --version
```

导出/usr/local/ffmpeg/bin到PATH环境变量中

```sh
sudo vim ~/.zshrc

# 在文件末尾添加
export PATH=/usr/local/ffmpeg/bin:$PATH
```

重新打开shell会自动加载最新环境变量

解压ffmpeg源代码并切换到源代码目录中

```sh
# 配置编译环境
# 提醒：需要使用--disable-x86asm参数，否则会报告nasm/yasm not found or too old. Use --disable-x86asm for a crippled build.错误
./configure --prefix=/usr/local/ffmpeg --enable-debug=3 --disable-x86asm

# 编译
make -j 8

# 安装
sudo make install

# 检查ffmpeg是否成功安装
ffmpeg -version
```


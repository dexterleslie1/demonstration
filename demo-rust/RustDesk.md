## Ubuntu20.4编译RustDesk

>提示：使用Ubuntu20.4 Server编译RustDesk（在Ubuntu20.4 Desktop中安装编译RustDesk编译的依赖会报告包冲突错误）。

步骤：

1. 在Ubuntu安装vcpkg

2. 安装依赖库

   ```sh
   vcpkg install libvpx libyuv opus aom
   
   sudo apt install libglib2.0-dev
   sudo apt install libgstreamer1.0-dev
   sudo apt install libgstreamer-plugins-base1.0-dev
   sudo apt install libgtk-3-dev
   sudo apt install libssl-dev
   sudo apt install libclang-dev
   sudo apt install libpam0g-dev
   sudo apt install libxcb-randr0-dev
   sudo apt install libpulse-dev
   ```

3. 安装Rust环境

4. 下载RustDesk代码，https://github.com/rustdesk/rustdesk

5. 编译RustDesk

   ```sh
   cd ~/workspace-git/rustdesk
   cargo build
   ```

6. 等待编译完毕会在target目录生成二进制文件

   ```sh
   $ tree target -L 2
   target
   ├── CACHEDIR.TAG
   └── debug
       ├── build
       ├── deps
       ├── examples
       ├── incremental
       ├── liblibrustdesk.a
       ├── liblibrustdesk.d
       ├── liblibrustdesk.rlib
       ├── liblibrustdesk.so
       ├── naming
       ├── naming.d
       ├── rustdesk
       ├── rustdesk.d
       ├── service
       └── service.d
   
   ```

   


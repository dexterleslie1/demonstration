# `jprofiler`用法

## 安装

### `windows`安装`jprofiler`

1. 访问`https://www.ej-technologies.com/download/jprofiler/files`下载`windows`平台最新版本`jprofiler`
2. 使用超级管理员权限安装`jprofiler_windows-x64_14_0_3.exe`

### `ubuntu`安装`jprofiler`

1. 访问`https://www.ej-technologies.com/download/jprofiler/files`下载`linux`平台最新版本`JProfiler for Linux Setup Executable`

2. 为`jprofiler_linux-x64_14_0_3.sh`新增执行权限

   ```bash
   chmod +x jprofiler_linux-x64_14_0_3.sh
   ```

3. 执行`jprofiler`安装程序，根据提示安装`jprofiler`

   ```bash
   sudo ./jprofiler_linux-x64_14_0_3.sh
   ```

   执行上面命令时报告如下错误，可以使用 `unset DISPLAY` 命令解决。

   ```java
   Unpacking JRE ...
   Starting Installer ...
   An error occurred:
   java.lang.NoClassDefFoundError: Could not initialize class sun.awt.X11.XToolkit
   Error log: /tmp/install4jError11842990430240225258.log
   java.lang.NoClassDefFoundError: Could not initialize class sun.awt.X11.XToolkit
   	at java.desktop/sun.awt.PlatformGraphicsInfo.createToolkit(PlatformGraphicsInfo.java:40)
   	at java.desktop/java.awt.Toolkit.getDefaultToolkit(Toolkit.java:599)
   	at java.desktop/java.awt.Toolkit.getEventQueue(Toolkit.java:1498)
   	at java.desktop/java.awt.EventQueue.isDispatchThread(EventQueue.java:1107)
   	at com.install4j.runtime.installer.frontend.GUIHelper.invokeOnEDT(GUIHelper.java:436)
   	at com.install4j.runtime.installer.frontend.headless.AbstractHeadlessScreenExecutor.init(AbstractHeadlessScreenExecutor.java:83)
   	at com.install4j.runtime.installer.frontend.headless.ConsoleScreenExecutor.<init>(ConsoleScreenExecutor.java:25)
   	at com.install4j.runtime.installer.frontend.headless.InstallerConsoleScreenExecutor.<init>(InstallerConsoleScreenExecutor.java:6)
   	at com.install4j.runtime.installer.Installer.getScreenExecutor(Installer.java:97)
   	at com.install4j.runtime.installer.Installer.runInProcess(Installer.java:73)
   	at com.install4j.runtime.installer.Installer.main(Installer.java:50)
   	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
   	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
   	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
   	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
   	at com.exe4j.runtime.LauncherEngine.launch(LauncherEngine.java:84)
   	at com.install4j.runtime.launcher.UnixLauncher.start(UnixLauncher.java:71)
   	at install4j.Installer3991225894.main(Unknown Source)
   Caused by: java.lang.ExceptionInInitializerError: Exception java.awt.AWTError: Can't connect to X11 window server using ':0' as the value of the DISPLAY variable. [in thread "main"]
   	at java.desktop/sun.awt.X11GraphicsEnvironment.initDisplay(Native Method)
   	at java.desktop/sun.awt.X11GraphicsEnvironment$1.run(X11GraphicsEnvironment.java:112)
   	at java.base/java.security.AccessController.doPrivileged(AccessController.java:318)
   	at java.desktop/sun.awt.X11GraphicsEnvironment.initStatic(X11GraphicsEnvironment.java:67)
   	at java.desktop/sun.awt.X11GraphicsEnvironment.<clinit>(X11GraphicsEnvironment.java:62)
   	at java.desktop/sun.awt.PlatformGraphicsInfo.createGE(PlatformGraphicsInfo.java:36)
   	at java.desktop/java.awt.GraphicsEnvironment$LocalGE.createGE(GraphicsEnvironment.java:103)
   	at java.desktop/java.awt.GraphicsEnvironment$LocalGE.<clinit>(GraphicsEnvironment.java:88)
   	at java.desktop/java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment(GraphicsEnvironment.java:116)
   	at java.desktop/sun.awt.X11.XToolkit.<clinit>(XToolkit.java:514)
   	at java.desktop/sun.awt.PlatformGraphicsInfo.createToolkit(PlatformGraphicsInfo.java:40)
   	at java.desktop/java.awt.Toolkit.getDefaultToolkit(Toolkit.java:599)
   	at java.desktop/java.awt.EventQueue.<init>(EventQueue.java:263)
   	at java.desktop/sun.awt.SunToolkit.initEQ(SunToolkit.java:180)
   	at java.desktop/sun.awt.SunToolkit.createNewAppContext(SunToolkit.java:321)
   	at java.desktop/sun.awt.AppContext$2.run(AppContext.java:273)
   	at java.desktop/sun.awt.AppContext$2.run(AppContext.java:262)
   	at java.base/java.security.AccessController.doPrivileged(AccessController.java:318)
   	at java.desktop/sun.awt.AppContext.initMainAppContext(AppContext.java:262)
   	at java.desktop/sun.awt.AppContext$3.run(AppContext.java:315)
   	at java.desktop/sun.awt.AppContext$3.run(AppContext.java:298)
   	at java.base/java.security.AccessController.doPrivileged(AccessController.java:318)
   	at java.desktop/sun.awt.AppContext.getAppContext(AppContext.java:297)
   	at java.desktop/java.awt.Component.<init>(Component.java:1023)
   	at java.desktop/java.awt.Container.<init>(Container.java:300)
   	at java.desktop/java.awt.Window.<init>(Window.java:561)
   	at java.desktop/java.awt.Frame.<init>(Frame.java:428)
   	at java.desktop/java.awt.Frame.<init>(Frame.java:393)
   	at com.install4j.runtime.installer.helper.InstallerUtil.initHeadless(InstallerUtil.java:599)
   	at com.install4j.runtime.installer.helper.InstallerUtil.isHeadless(InstallerUtil.java:589)
   	at com.install4j.runtime.installer.helper.InstallerUtil.getExecutionMode(InstallerUtil.java:554)
   	at com.install4j.runtime.installer.Installer.getScreenExecutor(Installer.java:86)
   	... 9 more
   java.lang.NoClassDefFoundError: Could not initialize class sun.awt.X11.XToolkit
   	at java.desktop/sun.awt.PlatformGraphicsInfo.createToolkit(PlatformGraphicsInfo.java:40)
   	at java.desktop/java.awt.Toolkit.getDefaultToolkit(Toolkit.java:599)
   	at java.desktop/java.awt.Toolkit.getEventQueue(Toolkit.java:1498)
   	at java.desktop/java.awt.EventQueue.isDispatchThread(EventQueue.java:1107)
   	at com.install4j.runtime.installer.ContextImpl.immediateExit(ContextImpl.java:1299)
   	at com.install4j.api.Util$4.run(Util.java:448)
   	at com.install4j.runtime.installer.helper.comm.actions.RunAction.execute(RunAction.java:11)
   	at com.install4j.runtime.installer.helper.comm.HelperCommunication.executeActionDirect(HelperCommunication.java:292)
   	at com.install4j.runtime.installer.helper.comm.HelperCommunication.executeActionInt(HelperCommunication.java:267)
   	at com.install4j.runtime.installer.helper.comm.HelperCommunication.executeAction(HelperCommunication.java:244)
   	at com.install4j.api.Util.fatalError(Util.java:441)
   	at com.install4j.runtime.installer.Installer.main(Installer.java:52)
   	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
   	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
   	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
   	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
   	at com.exe4j.runtime.LauncherEngine.launch(LauncherEngine.java:84)
   	at com.install4j.runtime.launcher.UnixLauncher.start(UnixLauncher.java:71)
   	at install4j.Installer3991225894.main(Unknown Source)
   Caused by: java.lang.ExceptionInInitializerError: Exception java.awt.AWTError: Can't connect to X11 window server using ':0' as the value of the DISPLAY variable. [in thread "main"]
   	at java.desktop/sun.awt.X11GraphicsEnvironment.initDisplay(Native Method)
   	at java.desktop/sun.awt.X11GraphicsEnvironment$1.run(X11GraphicsEnvironment.java:112)
   	at java.base/java.security.AccessController.doPrivileged(AccessController.java:318)
   	at java.desktop/sun.awt.X11GraphicsEnvironment.initStatic(X11GraphicsEnvironment.java:67)
   	at java.desktop/sun.awt.X11GraphicsEnvironment.<clinit>(X11GraphicsEnvironment.java:62)
   	at java.desktop/sun.awt.PlatformGraphicsInfo.createGE(PlatformGraphicsInfo.java:36)
   	at java.desktop/java.awt.GraphicsEnvironment$LocalGE.createGE(GraphicsEnvironment.java:103)
   	at java.desktop/java.awt.GraphicsEnvironment$LocalGE.<clinit>(GraphicsEnvironment.java:88)
   	at java.desktop/java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment(GraphicsEnvironment.java:116)
   	at java.desktop/sun.awt.X11.XToolkit.<clinit>(XToolkit.java:514)
   	at java.desktop/sun.awt.PlatformGraphicsInfo.createToolkit(PlatformGraphicsInfo.java:40)
   	at java.desktop/java.awt.Toolkit.getDefaultToolkit(Toolkit.java:599)
   	at java.desktop/java.awt.EventQueue.<init>(EventQueue.java:263)
   	at java.desktop/sun.awt.SunToolkit.initEQ(SunToolkit.java:180)
   	at java.desktop/sun.awt.SunToolkit.createNewAppContext(SunToolkit.java:321)
   	at java.desktop/sun.awt.AppContext$2.run(AppContext.java:273)
   	at java.desktop/sun.awt.AppContext$2.run(AppContext.java:262)
   	at java.base/java.security.AccessController.doPrivileged(AccessController.java:318)
   	at java.desktop/sun.awt.AppContext.initMainAppContext(AppContext.java:262)
   	at java.desktop/sun.awt.AppContext$3.run(AppContext.java:315)
   	at java.desktop/sun.awt.AppContext$3.run(AppContext.java:298)
   	at java.base/java.security.AccessController.doPrivileged(AccessController.java:318)
   	at java.desktop/sun.awt.AppContext.getAppContext(AppContext.java:297)
   	at java.desktop/java.awt.Component.<init>(Component.java:1023)
   	at java.desktop/java.awt.Container.<init>(Container.java:300)
   	at java.desktop/java.awt.Window.<init>(Window.java:561)
   	at java.desktop/java.awt.Frame.<init>(Frame.java:428)
   	at java.desktop/java.awt.Frame.<init>(Frame.java:393)
   	at com.install4j.runtime.installer.helper.InstallerUtil.initHeadless(InstallerUtil.java:599)
   	at com.install4j.runtime.installer.helper.InstallerUtil.isHeadless(InstallerUtil.java:589)
   	at com.install4j.runtime.installer.helper.InstallerUtil.getExecutionMode(InstallerUtil.java:554)
   	at com.install4j.runtime.installer.Installer.getScreenExecutor(Installer.java:86)
   	at com.install4j.runtime.installer.Installer.runInProcess(Installer.java:73)
   	at com.install4j.runtime.installer.Installer.main(Installer.java:50)
   	... 7 more
   
   ```

4. 在`Show Applications`面板搜索并打开`jprofiler`



## `jprofiler`术语

### `Shallow Size`和`Retained Size`

>[参考链接](https://www.jianshu.com/p/851b5bb0a4d4)

`Shallow Size`是指实例**自身**占用的内存, 可以理解为保存该'数据结构'需要多少内存, 注意**不包括**它引用的其他实例。

`Retained Size`是指, 当实例A被回收时, 可以**同时被回收**的实例的Shallow Size之和

### `outgoing references`和`incoming references`

>[参考链接](https://blog.csdn.net/qq_22222499/article/details/100069126)

outgoing references ，这个对象引用了哪些对象
incoming references ，哪些对象引用了这个对象




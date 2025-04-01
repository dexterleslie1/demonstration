const { app, BrowserWindow, globalShortcut } = require('electron')

function createWindow () {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    useContentSize: true,
    webPreferences: {
      // 允许打开调试窗口
      devTools: true,

      // nodeIntegration、contextIsolation允许在render进程使用require('electron')相关api
      nodeIntegration: true,
      contextIsolation: false,

      // 这个选项在electron14之后被移除，所以在electron14之后使用remote需要查看文档最新方法
      enableRemoteModule: true
    }
  })

  // 打开开发者工具
  win.webContents.openDevTools()

  win.loadFile('index.html')
}

app.whenReady().then(() => {
  createWindow()

  app.on('activate', function () {
    if (BrowserWindow.getAllWindows().length === 0) createWindow()
  })

  // 参考文档
  // https://www.electronjs.org/zh/docs/latest/api/accelerator
  // https://www.electronjs.org/zh/docs/latest/api/global-shortcut

  // 注册ctrl+shift+a快捷键
  // macOS按下command+shift+a键触发
  let result = globalShortcut.register('CommandOrControl+shift+a', function() {
    console.log(`按下CommandOrControl+shift+a快捷键`);
  });
  if(!result) {
    console.log('注册快捷键ctrl+shift+a失败');
  } else {
    console.log('注册快捷键ctrl+shift+a成功');
  }
})

app.on('will-quit', function() {
  // 注销ctrl+shift+a快捷键
  globalShortcut.unregister('CommandOrControl+shift+a');

  // 注销所有快捷键
  globalShortcut.unregisterAll();
})

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit()
})

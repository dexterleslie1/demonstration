const { app, BrowserWindow } = require('electron')

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
})

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit()
})

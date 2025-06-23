const { app, BrowserWindow } = require('electron')

function createWindow() {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
  })

  // 开发环境下自动打开 DevTools
  // 在 package.json 中传递 NODE_ENV 环境变量 "start": "NODE_ENV=development electron ."
  if (process.env.NODE_ENV === 'development') {
    win.webContents.openDevTools()
  }

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

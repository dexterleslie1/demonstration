const { app, BrowserWindow, ipcMain } = require('electron')

function createWindow() {
    const win = new BrowserWindow({
        width: 800,
        height: 200,
        // 使 electron 应用没有边框
        frame: false,
        // 使 electron 应用透明
        transparent: true,
        webPreferences: {
            nodeIntegration: true,
            // 仅在开发环境中使用，生产环境应设为 true
            contextIsolation: false
        },
    })

    // 开发环境下自动打开 DevTools
    // 在 package.json 中传递 NODE_ENV 环境变量 "start": "NODE_ENV=development electron ."
    // if (process.env.NODE_ENV === 'development') {
    //     win.webContents.openDevTools()
    // }

    win.loadFile('index.html')

    ipcMain.on('window-minimize', () => {
        // 窗口最小化
        win.minimize()
    })

    ipcMain.on('window-maximum', () => {
        if (win.isMaximized()) {
            // 如果最大化则恢复到之前状态
            win.unmaximize()
        } else {
            // 最大化窗口
            win.maximize()
        }
    })

    ipcMain.on('window-exit', () => {
        // 关闭窗口
        win.close()
        // 关闭应用
        app.quit()
    })
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
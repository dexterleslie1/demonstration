const { app, BrowserWindow, ipcMain } = require('electron')

function createWindow() {
    const win = new BrowserWindow({
        width: 800,
        height: 200,
        // 使 electron 应用没有边框
        frame: false,
        // 使 electron 应用透明
        transparent: true,
    })

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
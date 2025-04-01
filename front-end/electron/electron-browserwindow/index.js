const remote = require('electron').remote
const BrowserWindow = remote.BrowserWindow
const globalShortcut = remote.globalShortcut

let win;

document.getElementById("btnFullScreen").onclick = function() {
    if(!win) {
        // 获取所有屏幕
        let displays = remote.screen.getAllDisplays()

        win = new BrowserWindow({
            // 设置全屏窗口
            x: displays[0].bounds.x,
            y: displays[0].bounds.y,
            width: displays[0].bounds.width,
            height: displays[0].bounds.height,

            // 不自动显示窗口，需要调用show方法显示窗口
            show: false,
            
            // 无边
            frame: false,
            fullscreen: undefined,
            transparent: true,
            movable: false,
            resizable: false,
            hasShadow: false,
            enableLargerThanScreen: true,
        })
        win.loadURL('https://www.baidu.com')

        win.setAlwaysOnTop(true, 'screen-saver');
        win.setSkipTaskbar(true)

        // 显示窗口
        win.show()
    }
}

globalShortcut.register('Esc', function() {
    if(win) {
        win.destroy()
        win = null
    }
})
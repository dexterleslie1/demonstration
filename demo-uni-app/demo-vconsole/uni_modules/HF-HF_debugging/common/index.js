import Vue from "vue"
class ConsoleData {
	 request=[]
	 console=[
	 	["log", ["欢迎使用HF调试器~~~"]]
	 ]
	 consoleData={
	 	log:[],
	 	error:[]
	 }
	 storage= {}
	 URL= ""
	 
	 constructor(arg) {
		 this._isConsoleData = true
	     this.console =  [["log", ["欢迎使用HF调试器~~~"]]]
	 }
 }
const CONSOLE_DATA  = new ConsoleData()
const wx_request = wx.request
let view = null;
!function addInterceptorRequest() {
	wx.request = uni.request
	/* 拦截请求 */
	uni.addInterceptor('request', {
		invoke(args) {
			// request 触发前拼接 url 
			const addInterceptorRequestData = {
				Headers: {
					data: args.data,
					method: args.method,
					responseType: args.responseType,
					url: args.url
				}
			}
			let _complete = args.complete;
			args.complete = function(e) {
				_complete && _complete(e)
				addInterceptorRequestData.Preview = e
				if (CONSOLE_DATA.request.length >= 1000) {
					CONSOLE_DATA.request.shift()
				}
				CONSOLE_DATA.request.push(addInterceptorRequestData)
			}
		},
	})

}()


function createView() {
	let page = {},
		onType = false
	view = new plus.nativeObj.View('HF', {
		bottom: '100px',
		left: '60%',
		height: '40px',
		width: '100px',
		zIndex:'999'
	});
	view.drawRect({
		color: '#FF4A46',
		radius: '5px'
	}, {
		top: '0px',
		left: '0px',
		width: '100%',
		height: '100%'
	});
	view.drawText('HF调试器', {
		top: '0px',
		left: '0px',
		width: '100%',
		height: '100%'
	}, {
		size: '16px',
		color: '#FFFFFF'
	});
	view.addEventListener("touchstart", (touch) => {
		page = touch
	})
	view.addEventListener("touchmove", (touch) => {
		if ((touch.pageY - 20 > 0 && touch.pageX - 50 > 0) 
			&& (Math.abs(touch.pageX - page.pageX) > 20 
			|| Math.abs(touch.pageY - page.pageY) > 20)) {
			view.setStyle({
				top: (touch.pageY - 20) + 'px',
				left: (touch.pageX - 50) + 'px'
			})
			onType = true
		} else {
			onType = false
		}
	})
	view.addEventListener("click", (e) => {
		!onType && uni.$emit("HFshow")
	})
	console.log("")
}

function showHF() {
	createView()
	view.show()
}


/* 拦截日志文件 */
! function consoleInit() {
	/* 点击事件 */
	UniServiceJSBridge.subscribe("BNT_VIEW", () => {
		uni.$emit("HFshow")
	});
	/* 移动事件 */
	UniServiceJSBridge.subscribe("BNT_VIEW.ontouchend", (e) => {
		// uni.$emit("HFshow")
		uni.setStorageSync("pageXY", e)
	});
	let console_log = console.log
	let console_error = console.error
	console.log = function(...arg) {
		if (CONSOLE_DATA.console.length >= 1000) {
			CONSOLE_DATA.console.shift()
		}
		if (CONSOLE_DATA.consoleData.log.length >= 1000) {
			CONSOLE_DATA.consoleData.log.shift()
		}
		CONSOLE_DATA.console.push(["log", [...arg]])
		CONSOLE_DATA.consoleData.log.push(["log", [...arg]])
		console_log(...arg)
	}
	console.error = function(...arg) {
		if (CONSOLE_DATA.console.length >= 1000) {
			CONSOLE_DATA.console.shift()
		}
		if (CONSOLE_DATA.consoleData.error.length >= 1000) {
			CONSOLE_DATA.consoleData.error.shift()
		}
		CONSOLE_DATA.console.push(["error", [...arg]])
		CONSOLE_DATA.consoleData.error.push(["error", [...arg]])
		console_error(...arg)
	}
	// #ifndef VUE3
	Vue.config.errorHandler = (...err) => {
		CONSOLE_DATA.console.push(["error", [...err]])
		CONSOLE_DATA.consoleData.error.push(["error", [...err]])
		throw err
	}
	// #endif
	// #ifdef APP
	showHF()
	// #endif
}()

/* 获取storage */
function getStorageAllSync() {
	let storageAllData = {}
	uni.getStorageInfo({
		success(res) {
			res.keys.forEach(item => {
				storageAllData[item] = JSON.stringify(uni.getStorageSync(item))
			})
			CONSOLE_DATA.storage = storageAllData
		}
	})
}

uni.setStorageSync("test", "HF数据存储验证，欢迎使用HF调试器")
uni.$on("HFshow", () => {
	getStorageAllSync()
	// getApp().globalData.CONSOLE_DATA = CONSOLE_DATA
	console.log("EEglobalData")
	if(getApp().$vm.$refs['HFDebugging']){
		getApp().$vm.$refs['HFDebugging'].onClick()
		return
	}
	const pages = getCurrentPages()
	if (pages[pages.length - 1].route == 'uni_modules/HF-HF_debugging/pages/subnvue/HFconsole') {
		uni.navigateBack({
			delta: 1
		})
	} else {
		uni.navigateTo({
			url: "/uni_modules/HF-HF_debugging/pages/subnvue/HFconsole",
			animationType: "slide-in-bottom"
		})
	}
})

// 存贮文件内容
/* function createWriter(){
	return new Promise((resolve,reject)=>{
		plus.io.requestFileSystem( plus.io.PRIVATE_DOC, function( fs ) {
				// 可通过fs操作PUBLIC_DOWNLOADS文件系统 
				// ......
				console.log("fs",fs)
				fs.root.getFile('__CONSOLE_DATA__.js', {
				  create: true  // 文件不存在则创建
				}, fileEntry => {
				  // 文件在手机中的路径
				  console.log(fileEntry.fullPath)
				  fileEntry.createWriter(writer => {
					// 写入文件成功完成的回调函数
					writer.onwrite = e => {
					  resolve(e)
					};
					// 写入数据
					writer.write("window.__CONSOLE_DATA__ = "+JSON.stringify(CONSOLE_DATA));
				  })
				  CONSOLE_DATA.URL = plus.io.convertLocalFileSystemURL(fileEntry.fullPath)
				}, e => {
				  console.log("getFile failed: " + e.message);
				});
			}, function ( e ) {
			reject("Request file system failed: " + e.message)
		});
	})
} */

function HFdebugging(app, {
	wxRequest = true
}) {
	if (!wxRequest) wx.request = wx_request
	if (!app) return
	/* addInterceptorRequest()
	consoleInit() */
	if (!app.config.errorHandler) {
		app.config.errorHandler = (...err) => {
			CONSOLE_DATA.console.push(["error", [...err]])
			throw err
		}
	} else {
		let errorHandler = app.config.errorHandler
		app.config.errorHandler = (...err) => {
			CONSOLE_DATA.console.push(["error", [...err]])
			errorHandler(err)
			throw err
		}
	}

}

export default {
	CONSOLE_DATA,
	HFdebugging,
	view,
	showHF,
	getStorageAllSync
}

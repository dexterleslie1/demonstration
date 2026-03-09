import {ConsoleData,RequestData,StorageData,AppDataClass,CurrentPagesClass} from './../data.js'
// #ifndef VUE3
import Vue from 'vue'
// #endif


/* 拦截日志文件 */
export class consoleInit {
	DATA = []
	NAME = 'Console'
	consoleNameArr = ['log', 'error', 'info', 'warn']
	componentsName='Console'
	app = {}
	constructor(arg) {
		this.DATA = new ConsoleData()
		this._init()
	}
	
	_init() {
		const consoleNameArr = this.consoleNameArr
		this.consoleArrFunc = consoleNameArr.map(item => console[item])
		// global.$_log  = this.consoleArrFunc[0]
		consoleNameArr.map((item,index) => {
			console[item] = this._record(item,this.consoleArrFunc[index])
		})
		this.app = this.$HFdebugging.options.app
		// #ifdef VUE3
		this._vue3Error(this.app)
		window&&(window.$_log  = this.consoleArrFunc[0])
		// #endif
		// #ifndef VUE3
		this._vueError()
		global.$_log  = this.consoleArrFunc[0]
		// #endif
	}
	_record(type,consoleFunc=()=>{}) {
		return (...args) => {
			this.addRecord(type,...args)
			consoleFunc&&consoleFunc(...args);
		}
	}
	addRecord(type,...args){
		let time = this._getDate()
		this.DATA.push({
			time,
			type,
			objects: [...args]
		})
	}

	_getDate() {
		let d = new Date
		let a = d.toString()
		if(a.split(' ')[4]){
			return a.split(' ')[4] + '.' + d.getMilliseconds()
		}else{
			return a
		}
		
	}


	_vueError() {
		let errorHandler 
		let warnHandler 
		if(typeof Vue.config.errorHandler == 'function'){
			errorHandler = Vue.config.errorHandler
		}
		Vue.config.errorHandler = (...err) => {
			errorHandler&&errorHandler(...err)
			this.addRecord('error',...err)
			throw err
		}
		if(typeof Vue.config.errorHandler == 'function'){
			errorHandler = Vue.config.errorHandler
		}
		Vue.config.warnHandler = (...err) => {
			warnHandler&&warnHandler(...err)
			this.addRecord('warn',...err)
		}
	}
	_vue3Error(app){
		let errorHandler
		let warnHandler 
		if(typeof app.config.errorHandler == 'function'){
			errorHandler = app.config.errorHandler
		}
		app.config.errorHandler = (...err) => {
			errorHandler&&errorHandler(...err)
			this.addRecord('error',...err)
			throw err
		}
		if(typeof app.config.errorHandler == 'function'){
			errorHandler = app.config.errorHandler
		}
		app.config.warnHandler = (...err) => {
			warnHandler&&warnHandler(...err)
			this.addRecord('warn',...err)
		}
	}
}

/* 拦截请求 */
export class InterceptorRequest {
	NAME='Request'
	DATA = []
	componentsName='InterceptorRequest'
	constructor(arg) {
		this.DATA = new RequestData()
		this.init()
	}
	init() {
		let that = this
		wx.request = uni.request
		uni.addInterceptor('request', {
			invoke(args) {
				// request 触发前拼接 url 
				const addInterceptorRequestData = {
					Headers: {...args}
				}
				let _complete = args.complete;
				args.complete = function (e) {
					_complete && _complete(e)
					addInterceptorRequestData.Preview = {...e}
					that.DATA.push(addInterceptorRequestData)
				}
			},
		})
	}
	addRequestData(addInterceptorRequestData={}){
		this.DATA.push(addInterceptorRequestData)
	}
	
}

/* 存储信息 */
export class StorageInit {
	DATA = []
	NAME = 'Storage'
	componentsName='StorageInit'
	constructor(){
		this.DATA = new StorageData()
		this._init()
	}

	_init() {

	}
}


/* AppData */
export class AppDataInit{
	DATA = []
	NAME = 'AppData'
	componentsName='AppData'
	constructor(){
		this.DATA = new AppDataClass()
		this._init()
	}
	
	_init() {
	
	}
}

/* AppData */
export class CurrentPagesInit{
	DATA = []
	NAME = 'CurrentPages'
	componentsName='CurrentPages'
	constructor(){
		this.DATA = new CurrentPagesClass()
		this._init()
	}
	
	_init() {
	
	}
}
/**
 * 
 */

import {consoleInit,InterceptorRequest,StorageInit,AppDataInit,CurrentPagesInit} from './PlugIn/index.js'
// #ifdef H5
import BntBoxConfirm from './../components/BntBox/index.js'
// #endif
import {information,HFDebuggingInformation } from './store.js'
const wx_request = wx.request
// consoleInit,AppDataInit,CurrentPagesInit,InterceptorRequest,StorageInit
let informationArr = [consoleInit,AppDataInit,CurrentPagesInit,InterceptorRequest,StorageInit]
export class HFdebugging {
	wx_request = wx_request
	view = null

	/* 挂载的插件数据 */
	information = []

	/* 设置内容 */
	settingData	= {
		invert:{
			name:'深浅色',
			type:'switch',
			value:false,
		}
	}
	// 选项
	options={}

	constructor(options={}) {
		this.options = options
		// #ifdef App
		uni.onThemeChange((res)=>{
		   this.settingData.invert.value=(res.theme!='dark');
		});
		// #endif
		this._init()
		this._eventInit()
		HFDebuggingInformation.push(this)
	}


	_init() {
		if(this.options.plugIn&& this.options.plugIn.isArray()){
			informationArr.map(item=>{
				this.loadPlugIn(item)
			})
		}else{
			informationArr.map(item=>{
				this.loadPlugIn(item)
			})
		}
		this.information = information
		// #ifdef H5
		BntBoxConfirm()
		// #endif
		
		// #ifdef APP
		this.view = createView();
		this.show();
		// #endif
		
	}

	_eventInit(){
		uni.$on("HFshow", () => {
			const pages = getCurrentPages()
			if (pages[pages.length - 1].route == 'uni_modules/HF-HF_debugging/pages/next/next') {
				uni.navigateBack({
					delta: 1
				})
			} else {
				uni.navigateTo({
					url: "/uni_modules/HF-HF_debugging/pages/next/next",
					animationType: "slide-in-bottom"
				})
				this.view?.hide()
			}
			
		})
		uni.$on("viewShow", () => {
			this.show()
		})
		uni.$on("viewHide", () => {
			this.hide()
		})
	}

	show(){
		this.view?.show()
	}

	hide() {
		this.view?.hide()
	}

	// 挂在插件
	loadPlugIn(PlugIn) {
		PlugIn.prototype.$HFdebugging = this
		information.push(new PlugIn())
	}
	
	// 筛选插件实例
	getPlugIn(plugInName=''){
		return this.information.filter(item=>item.NAME==plugInName)[0]
	}
	
	// 删除实例
	removePlugIn(plugInName=''){
		let index = this.information.findIndex(item=>item.NAME==plugInName)
		if(index>-1){
			this.information.splice(index,1)
		}
	}
	
}










function createView() {
	let page = {},
		onType = false
	const view = new plus.nativeObj.View('HF', {
		bottom: '100px',
		left: '60%',
		height: '40px',
		width: '100px',
		zIndex: '999'
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
		if ((touch.pageY - 20 > 0 && touch.pageX - 50 > 0) &&
			(Math.abs(touch.pageX - page.pageX) > 20 ||
				Math.abs(touch.pageY - page.pageY) > 20)) {
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
	return view
}

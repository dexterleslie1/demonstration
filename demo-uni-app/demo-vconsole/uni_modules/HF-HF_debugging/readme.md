# HF-HF_debugging

## 0.0.2rc 更新的主要功能
	- 新增 基于uni技术栈的请求重复发（如有其他技术栈的需要，可以进行插件扩展）
	- 新增 Storage 编辑
	- 新增 AppData 预览
	- 新增 页面栈信息预览
	- 新增 插件功能，开发者可以根据需求进行扩展 （例如扩展：获取所有图片地址、）
	- ui样式优化 增加设置箱 设置深浅色
	

## @注意 如果使用第三方请求拦截库 可能 Network 无请求信息
	- 解决：
	
		````JavaScript
			// 本框架默认已将 wx.request 替换为 uni.request 已经适配大多数 第三方请求库  
			/* 0.0.1-rc.2 及一下版本 */
			// 因为大部分的开平台库都是基于微信小程序标准开发的
			// 这里已 flyio 为例
			// 在 flyio 文件中全局搜索  wx.request  替换为 uni.request 即可拦截请求信息
		````
		
## @注意 配置信息添加
	
	```JavaScript
		// main.js 文件示例
		/* 注意当前是 0.0.2rc 及以上版本的设置
			不再是直接引用就可以还需要 new 成一个实例
		 */
		import App from './App'
		import {HFdebugging} from '@/uni_modules/HF-HF_debugging/common/next.js'
		
		// #ifndef VUE3
		import Vue from 'vue'
		Vue.config.productionTip = false
		App.mpType = 'app'
		const app = new Vue({
		    ...App
		})
		app.$mount()
		new HFdebugging()
		// #endif
		
		// #ifdef VUE3
		import { createSSRApp } from 'vue'
		export function createApp() {
		  const app = createSSRApp(App)
		  new HFdebugging({app});
		  return {
		    app
		  }
		}
		// #endif
		
	```


	``` jons5
		// pages.json  添加如下页面信息
		"pages":[
			// ...
			{
				"path": "uni_modules/HF-HF_debugging/pages/next/next",
				"style": {
					"navigationStyle": "custom",
					"backgroundColor": "transparent",
					"app-plus": {
						"animationType": "slide-in-bottom",
						"background": "transparent",
						"popGesture": "none"
					}
				}
			},
		]
	```

## API 文档

### 组件说明
	
	- BntBox HF调试器红色按钮组件 /uni_modules/HF-HF_debugging/components/BntBox/index.vue
	- HFBox 调试面板组件 /uni_modules/HF-HF_debugging/components/HFBox.vue
	- InterceptorRequest 请求组件 /uni_modules/HF-HF_debugging/components/InterceptorRequest.vue
	- StorageInit 缓存组件 /uni_modules/HF-HF_debugging/components/StorageInit.vue
	- Console 日志组件 /uni_modules/HF-HF_debugging/components/Console.vue
	- setting 设置组件 /uni_modules/HF-HF_debugging/components/setting.vue
	- AppData 页面AppData数据组件 /uni_modules/HF-HF_debugging/components/AppData.vue
	- CurrentPages 页面栈组件 /uni_modules/HF-HF_debugging/components/CurrentPages.vue
	- objTree 对象树状结构组件 /uni_modules/HF-HF_debugging/components/objTree.vue
	
### HFdebugging 实例中的API

	- options 配置项
	- view  App 中HF调试按钮按钮实例
	- information  插件实例列表
	- settingData  设置项数据
	- hide():void 隐藏按钮
	- show():void 显示按钮
	- loadPlugIn(PlugIn):void   挂在某插件实例 
	- getPlugIn(plugInName):PlugIn  获取某插件实例 
	- removePlugIn(plugInName):void   删除某插件实例 
	- wx_request  wx.request 原始值

### Console 实例中的API
	
	- addRecord(type:string,...args):void  添加指定的日志信息
	```JavaScript
		let HF = new HFdebugging();
		let Console = HF.getPlugIn('Console');
		Console.addRecord('log','日志信息');
		Console.addRecord('warn','warn信息');
		Console.addRecord('info','info信息');
		Console.addRecord('error','error信息');
	```

### 插件使用

	- 调用new 后的实例方法 将插件实例进行挂载
	
		```javaScript
			/**
			 *  带 * ✳ 星号 为必存项 
			 *  
			 *  */
		
			let HF = new HFdebugging();
			HF.loadPlugIn(MyPlugIn);
			
			// MyPlugIn 必须是一个类
			class MyPlugIn{ // 插件实例
				DATA = [] // ✳
				NAME = 'myPlugIn' // 插件名称 ✳
				componentsName='myPlugIn' // vue 组件名称 ， 需要全局引入组件 ✳
				constructor(){
					this.DATA = new MyPlugInClass() // 数据信息 ✳
					this._init()
				}
				
				_init() { 
					// ... 
					// 拦截处理请求等
				}
			}
			
			// MyPlugInClass 数据 必须是一个类
			class MyPlugInClass { // 数据管理器
				DATA = [] // ✳
				constructor(arg) {
					this._myPlugInData = true // ✳ 数据值判断
					this.init()
				}
				init() {
					this.DATA = []
				}
				push(args) {
					if (this.DATA.length > 512) {
						this.DATA.shift()
					}
					this.DATA.push(args)
				}
				eliminateAll(){
					this.DATA = [];
				}
				// 可以使用 get 拦截请求 DATA 数据时进行处理
				get DATA() {  // 注意 get 后的变量名不能重复 否则报错  这是 this.DATA 需要更改为 变量名 例如：DATAC
					/* 获取storage */
					let storageAllData = []
					let that = this
					uni.getStorageInfo({
						success(res) {
							res.keys.forEach(item => {
								// storageAllData[item] = JSON.stringify(uni.getStorageSync(item))
								storageAllData.push({
									key: item,
									obj: uni.getStorageSync(item)
								})
							})
						   that.DATAC = storageAllData 
						}
					})
					return this.DATAC
				}
			}
			
			
			// 组件
			// objTree 是一个树状结构组件 按需引入
			// import objTree from './objTree.vue' 
			export default {
				name: 'AppData',
				// components: {
				//	 objTree
				// },
				props: {
					/**
					 *  ✳ 每一个插件组件都会传递来一个 当前插件的实例 既 MyPlugIn
					 *  每一个插件实例的prototype下都存在 $HFdebugging 属性，
					 *  $HFdebugging 便是 HFdebugging 的实例 
					 *  例如 PlugIn.prototype.$HFdebugging 
					 *  可以调用HFdebugging获取所有的 插件及配置项
					 *  this.livingExample.$HFdebugging.options  // 获取new HFdebugging 时传递的参数信息
					 *  this.livingExample.$HFdebugging.view // HF调试器 按钮的实例
					 *  this.livingExample.$HFdebugging.settingData // 设置信息
					 *  this.livingExample.$HFdebugging.information // 所有的插件实例 是一个数组
					 *  如果不喜欢使用上述方法获取对用信息也可以引入 store 获取 类型均为数组
					 *  import {HFDebuggingInformation,information} from '@/uni_modules/HF-HF_debugging/common/store'
					 */
					livingExample: { 
						type: Object,
						default: () => {
							return {}
						}
					}
				},
				data() {
					return {
						openType:1
					}
				},
				computed: {
					exData() {
						return this.livingExample.DATA.DATA // MyPlugIn 中的数据管理器，下的实际数据 
					}
				},
				methods:{
					scrolltolower(e){}, // 来自父级的触底事件
					scrolltoupper(e){}, // 来自父级的触顶事件
				}
			}
			
			
			
		```
		
## @注意 一下为0.0.1的配置说明 如果使用第三方请求拦截库 可能 Network 无请求信息
	- 解决：
	
		````JavaScript
			// 本框架默认已将 wx.request 替换为 uni.request 已经适配大多数 第三方请求库  
			// 如需取消默认替换请在 请调用HFdebugging(app,{wxRequest:false}) 
			// 并在沿用0.0.1-rc.2 及一下版本版本方法替换
			
			/* 0.0.1-rc.2 及一下版本 */
			// 因为大部分的开平台库都是基于微信小程序标准开发的
			// 这里已 flyio 为例
			// 在 flyio 文件中全局搜索  wx.request  替换为 uni.request 即可拦截请求信息
		````
		
## @注意 一下为0.0.1的配置说明 配置信息添加
	
	```JavaScript
		// main.js 文件示例
		
		import App from './App'
		
		// #ifdef APP
		import {HFdebugging} from '@/uni_modules/HF-HF_debugging/common/index.js'
		// #endif
		
		
		// #ifndef VUE3
		import Vue from 'vue'
		Vue.config.productionTip = false
		App.mpType = 'app'
		const app = new Vue({
		    ...App
		})
		app.$mount()
		// #endif
		
		// #ifdef VUE3
		import { createSSRApp } from 'vue'
		export function createApp() {
		  const app = createSSRApp(App)
		  HFdebugging(app)
		  return {
		    app
		  }
		}
		// #endif
		
	```


	``` jons5
		// pages.json  添加如下页面信息
		"pages":[
			// ...
			{
				"path": "uni_modules/HF-HF_debugging/pages/subnvue/HFconsole",
				"style": {
					"navigationStyle": "custom",
					"backgroundColor": "transparent",
					"app-plus": {
						"animationType": "slide-in-bottom",
						"background": "transparent",
						"popGesture": "none"
					}
				}
			},
			/* 注意 0.0.1-rc.1及一下版本需要添加 concat 文件 */
			{
				"path": "uni_modules/HF-HF_debugging/pages/subnvue/concat"
			}
			// ...
		]
		/* 注意 0.0.1-rc.1及一下版本需要添加一下内容 */
		"globalStyle": {
			// ...
			"subNVues": [{
				"id": "concat", // 唯一标识  
				"path": "uni_modules/HF-HF_debugging/pages/subnvue/concat", // 页面路径  
				"style": {
					"position": "absolute",
					"right": "80rpx",
					"bottom": "120rpx",
					"width": "200rpx",
					"height": "80rpx",
					"background": "transparent"
				}
			}]
			// ...
		}
	```
	
## 如果有兴趣一起开发的可以在gitee上申请加入

## 未来计划
	- [-] 优化 Vue3 环境错误拦截 
	- [-] 优化纯nvue环境的渲染
	- [-] 优化插件管理系统
	- [-] 欢迎在 评论区 提问 或 改进建议
	- [-] ...等
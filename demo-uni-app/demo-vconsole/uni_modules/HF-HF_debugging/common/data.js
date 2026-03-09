// #ifdef VUE3
import { ref,reactive,computed } from 'vue'
// #endif
export class ConsoleData {
    DATA = []
	FILTRATE = {}
    constructor(arg) {
        this._isConsoleData = true
        this.init()
    }
	
    init() {
        this.DATA = [{
            type: 'log',
            objects: ["欢迎使用HF调试器~~~"]
        }]
		// #ifdef VUE3
		this.DATA_REF = ref(this.DATA)
		this.DATA = computed(()=>this.DATA_REF.value).value
		this.FILTRATE_REF = ref(this.FILTRATE)
		this.FILTRATE = computed(()=>this.FILTRATE_REF.value).value
		// #endif
    }
	
    push(args) {
        if (this.DATA.length > 512) {
            this.DATA.shift()
        }
        this.DATA.push(args)
		this.initFiltrate(args)
		
    }
	
	initFiltrate(args){
		if(!this.FILTRATE[args.type]){
			this.FILTRATE[args.type] = []
		}
		if (this.FILTRATE[args.type].length > 256) {
		    this.FILTRATE[args.type].shift()
		}
		this.FILTRATE[args.type].push(args)
	}
	
	filtrate(type){
		if(!type)return this.DATA
		return this.FILTRATE[type]||[]
	}
	
	eliminateAll(){
		this.init()
		this.FILTRATE={}
	}
	remove(type){
		if(this.FILTRATE[type]){
			this.FILTRATE[type] = []
		}
	}
	
}

export class RequestData {
    DATA = []
    constructor(arg) {
        this._requestData = true
        this.init()
    }
    init() {
        this.DATA = []
		// #ifdef VUE3
		this.DATA_REF = ref([])
		this.DATA = computed(()=>this.DATA_REF.value).value
		// #endif
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
}


export class StorageData {
    DATAC = []

    constructor(arg) {
        this._storageData = true
        this.init()
    }

    init() {
        this.DATAC = []
		// #ifdef VUE3
		this.DATA_REF = ref(this.DATAC)
		this.DATAC = computed(()=>this.DATA_REF.value).value
		// #endif
    }

    push(args) {
        if (this.DATAC.length > 512) {
            this.DATAC.shift()
        }
        this.DATAC.push(args)
    }

	eliminateAll(){
		uni.clearStorageSync();
		this.DATAC = [];
	}
	
	remove(key,index){
		uni.removeStorageSync(key)
		this.DATAC.splice(index,1)
	}
	
	edit(storageItem,storageIndex){
		this.DATAC[storageIndex] = storageItem
		uni.setStorageSync(storageItem.key,storageItem.obj)
	}
	
    get DATA() {
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

    // 设置时更改 全局storageAllData
    /* set DATA(){

        return this.DATA
    } */

}


export class AppDataClass {
    DATAC = []
    constructor(arg) {
        this._appDataClass = true
        this.init()
    }
    init() {
        this.DATAC = []
		// #ifdef VUE3
		this.DATA_REF = ref(this.DATAC)
		this.DATAC = computed(()=>this.DATA_REF.value).value
		// #endif
    }
    push(args) {
        if (this.DATAC.length > 512) {
            this.DATAC.shift()
        }
        this.DATAC.push(args)
    }
	get DATA() {
	    /* 获取 */
		let arr = [...getCurrentPages()].reverse()
		// #ifndef VUE3
		this.DATAC =  arr.map(item=>item.$vm._data)
		//  #endif
		// #ifdef VUE3
		this.DATAC =  arr.map(item=>item.$vm.$data)
		//  #endif
		return this.DATAC
	}
}

export class CurrentPagesClass {
    DATAC = []
    constructor(arg) {
        this._appDataClass = true
        this.init()
    }
    init() {
        this.DATAC = []
		// #ifdef VUE3
		this.DATA_REF = ref(this.DATAC)
		this.DATAC = computed(()=>this.DATA_REF.value).value
		// #endif
    }
    push(args) {
        if (this.DATAC.length > 512) {
            this.DATAC.shift()
        }
        this.DATAC.push(args)
    }
	get DATA() {
	    /* 获取 */
		var CurrentPages = getCurrentPages()
		// #ifdef APP
		let keyArr = ['_data','data','$refs','route','options']
		CurrentPages = [...CurrentPages].map(item=>{
			let a = {...item}
			// a.$vm = JSON.parse(JSON.stringify(item.$vm,['$vm']))
			a.$vm = "[Object,$vm]"
			return a
		})
		// #endif
		this.DATAC =[...CurrentPages].reverse()
	    return this.DATAC
	}
}
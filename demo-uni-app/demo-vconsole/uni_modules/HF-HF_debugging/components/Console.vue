<template>
	<view >
		<view :class="item.type" class="content" v-for="(item,index) in exDataFiltrate(type)" :key="index">
			<text class="time">{{item.time}}</text>
			<text class="text-type" >{{item.type}}</text>
			<objTree :isShowKey="false" :objects="analysis(item.objects)" :isAt="true"></objTree>
		</view>
		<view class="" style="height: 200rpx;"></view>
		<view class="content-bottom-view">
			<scroll-view scroll-x="true" class="content-bottom">
				<text :class="{hit:type==''}" @click="type=''">All</text>
				<text :class="{hit:type==item}" @click="type=item" v-for="(item,index) in livingExample.consoleNameArr" :key="index" >{{item}}</text>
				<text @click="eliminateAll">eliminateAll</text>
				<text @click="remove">remove</text>
			</scroll-view>
			<view class="textarea-view">
				<textarea v-model="evalJsCode" placeholder="请入指令(实验性的)" @confirm="confirm" />
				<text @click="confirm" style="color: #FFFFFF;">{{'执行 \n 🛠'}} </text>
			</view>
		</view>
	</view>
</template>

<script>
	import objTree from './objTree.vue'
	import * as tool from './../common/tool.js'
	export default {
		name: 'Console',
		components: {
			objTree
		},
		props: {
			livingExample: {
				type: Object,
				default: () => {
					return {}
				}
			}
		},
		data() {
			return {
				type:'',
				evalJsCode:''
			}
		},
		computed: {
			exData() {
				return this.livingExample.DATA.DATA
			},
			exDataFiltrate() {
				return (type)=>this.livingExample.DATA.filtrate(type)
			},
			analysis(){
				return (item)=>this.solveLogItem(item)
			}
		},
		mounted() {
			this.$emit("setScrollToLower")
		},
		methods:{
			scrolltolower(e){
				// console.log("e",e)
			},
			solveLogItem(k) {
				// #ifndef VUE3
				if(typeof k === 'object' && !k.toString().includes(' at ') )return k
				// #endif
				// #ifdef VUE3
				if(typeof k === 'object')return k
				// #endif
				
				if(!k)return '';
				const solve = (str) => {
					const finalArr = [];
					let next_speical = '';
					const str_arr = str.split('---');
					for(let i = 0; i < str_arr.length; i++) {
						const str_item = str_arr[i];
						if(str_item == 'UNDEFINED') {
							finalArr.push(undefined);
						} else if(str_item.indexOf('BEGIN:') == 0) {
							next_speical = str_item.substr(6);
						} else if(str_item.indexOf('END:') == 0) {
							if(next_speical == str_item.substr(4)) {
								next_speical = '';
							}
						} else {
							switch(next_speical) {
								case 'BOOLEAN':
									finalArr.push(new Boolean(str_item) ? true : false);
									break;
								case 'NUMBER':
									finalArr.push(parseFloat(str_item));
									break;
								case 'JSON':
									finalArr.push(JSON.parse(str_item));
									break;
								default:
									str_item&&finalArr.push(str_item);
									break;
							}
						}
					}
					return finalArr
				};
				const atArr = (k[0]+'').split(' at ');
				const obj_arr = atArr[0].split('---COMMA---');
				const kArr = []
				if(obj_arr.length > 1) {
					for (let i = 0; i < obj_arr.length; i++){
						kArr.push(...solve(obj_arr[i]))
					}
				}else {
					kArr.push(...solve(obj_arr[0]));
				}
				kArr.push(' at '+atArr[1])
				return kArr;
			},
			eliminateAll(){
				 this.livingExample.DATA.eliminateAll()
			},
			remove(){
				 this.livingExample.DATA.remove(this.type)
			},
			confirm() {
				this.livingExample.addRecord('eval',(this.evalJsCode || 'undefined'))
				let result = ''
				let $content = ''
				try {
					result = eval.call(global, '(' + this.evalJsCode + ')');
				} catch (e) {
					try {
						result = eval.call(global, this.evalJsCode);
					} catch (e) {
						this.livingExample.addRecord('error','eval-error:',e)
					}
				}
				if (tool.isArray(result) || tool.isObject(result)) {
					result = getApp()!=result?result:' isObject - getApp'
				} else {
				  if (tool.isNull(result)) {
					result = 'null';
				  } else if (tool.isUndefined(result)) {
					result = 'undefined';
				  } else if (tool.isFunction(result)) {
					result = 'function()'
				  } else if (tool.isString(result)) {
					result = '"' + result + '"';
				  }
				}
				this.livingExample.addRecord('eval',(result || 'undefined'))
				this.evalJsCode = ""
			},
		}
	};
</script>

<style lang="scss">
	.content{
		display: flex;
		flex-wrap: wrap;
		width: 100%;
		box-shadow: 0rpx 0rpx 1rpx #C0C0C0;
		padding: 4rpx 0;
		.time{
			padding: 0 8rpx;
		}
	}
	.log{
		// color: ;
	}
	.error{
		color: #d50000;
		background: rgba($color: #7c3615, $alpha: .3);
		box-shadow: 0 0 1rpx #d50000;
	}
	.warn{
		color: #f8b251;
		background: rgba($color: #c3a356, $alpha: .2);
		box-shadow: 0 0 1rpx #f8b251;
	}
	.info{
		color: #5099f8;
		background: rgba($color: #5099f8, $alpha: .2);
		box-shadow: 0 0 1rpx #2500f8;
	}
	.eval{
		color: #aaaaff;
		background: rgba($color: #aaaaff, $alpha: .2);
		box-shadow: 0 0 1rpx #7979b5;
	}
	.text-type{
		padding:0 6rpx;
		/* border: 1rpx solid #3F536E;
		border-radius: 8rpx;
		background: #aaff7f; */
	}
	.content-bottom-view{
		position: fixed;
		z-index: 99;
		bottom: 0rpx;
		left: 0;
		background: #FFFFFF;
		.content-bottom{
			width: 100vw;
			height: 70rpx;
			text{
				padding: 4rpx 8rpx;
				border: 1rpx solid #999999;
			}
		}
		.textarea-view{
			width: 100vw;
			display: flex;
			text{
				width: 100rpx;
				text-align: center;
				line-height: 50rpx;
				background: #FF4A46;
			}
		}
		textarea{
			width: calc(100vw - 100rpx);
			max-height: 100rpx;
			// background: #007AFF;
		}
		.hit{
			background: #C8C7CC;
		}
		
	}
	.flex{
		display: flex;
		flex-wrap: wrap;
	}
</style>

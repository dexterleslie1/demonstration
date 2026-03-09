<template>
	<view>
		<block v-if="isObjectsThesaurus">
			<view  v-for="(item,index) in objects" :key="index" class="flex">
				<text v-if="isShowKey" class="colorfc98ff">{{index}}:</text>
				<text v-if="isArray(item)" class="color7898ff">Array ({{item.length}})</text>
				<text v-if="isThesaurus(item) === 1" :class="{link:isAt&&index==objectsLength}" >{{(item+'')||'null'}}</text>
				<view v-else-if="isThesaurus(item) === 2" @click.stop="openFunc==(index+'func')?openFunc='':openFunc=(index+'func')"  class="flex bg_kuang">
					<text> {{openFunc!=(index+'func')?'❓':'⚡'}}</text>
					<text class="colorfc98ff" v-if="openFunc!=(index+'func')">{{item.name+' '}}F()</text>
					<text v-if="openFunc==(index+'func')" >{{item.toString()}}</text>
				</view>
				<view v-else class="flex bg_kuang" >
					<text @click.stop="openType==index?openType=-1:openType=index">{{openType==index?'➖':'➕'}}</text>
					<!-- <text @click.stop="openType==index?openType=-1:openType=index" v-if="openType!=index" class="preview">{{JSON.stringify(item)}}</text> -->
					<objTree v-if="openType==index && index!=='$vm'" :objects="item"></objTree>
					<text v-if="openType==index && index=='$vm'" >[Object,$vm]</text>
					<!-- <text @click.stop="setIsOpen(index)">{{isOpen(index)?'➖':'➕'}}</text>
					<objTree v-if="isOpen(index) && item!=='$vm'" :objects="item"></objTree>
					<text v-if="isOpen(index) && item=='$vm'" >[$vm]</text> -->
				</view>
			</view>
		</block>
		<text v-else>{{objectType}}</text>
	</view>
</template>

<script>
	// import objTree from './objTree.vue'
	import * as tool from './../common/tool.js'
	const typeArr = ['boolean', 'undefined', 'number', 'string', 'null']
	export default {
		name: 'objTree',
		/* components: {
			objTree
		}, */
		props: {
			objects: {
				default: {}
			},
			isShowKey:{
				type:Boolean,
				default:true
			},
			isAt:{
				type:Boolean,
				default:false
			}
		},
		data() {
			return {
				openType: -1,
				openFunc:'',
				isNull:true,
				isObjectsThesaurus:false,
				openTypeSet:new Set(),
				objectsLength:-1,
				objectsKeys:[]
			}
		},
		computed: {
			isThesaurus() {
				this.isNull = false
				return (k)=>this.thesaurus(k)
			},
			isType(){
				return (k) => {
					let type = typeof k;
					if (typeArr.includes(type) || k === null || type === 'function') {
						return type
					} else if (type === 'function') {
						return type
					}else {
						return ''
					}
				}
			},
			objectType(){
				if(tool.isArray(this.objects)){
					return '[]'
				}else if(tool.isObject(this.objects)){
					return '{}'
				}else{
					return '{}'
				}
			},
			isArray(){
				return (k)=>tool.isArray(k)
			},
			isOpen(){
				const openTypeSet = this.openTypeSet
				return (index)=>openTypeSet.has(index)
			}
		},
		mounted() {
			// this.analysisObjects()
			this.objectsKeys = Object.keys(this.objects)
			if(this.objectsKeys.length){
				this.isObjectsThesaurus = true
			}
			
			//#ifdef APP
			this.isAt && (this.objectsLength = this.objects.length-1)
			// #endif
		},
		methods: {
			thesaurus(k){
				let type = typeof k;
				if (typeArr.includes(type) || k === null) {
					return 1
				} else if (type === 'function') {
					return 2
				}else {
					return 0
				}
			},
			setIsOpen(index){
				let openTypeSet = this.openTypeSet
				if(openTypeSet.has(index)){
					openTypeSet.delete(index)
				}else{
					openTypeSet.add(index)
				}
				this.openTypeSet = openTypeSet
			}
		}
	};
</script>

<style lang="scss">
	.inline-flex{
		display: inline-flex;
		flex-wrap: wrap;
	}
	.flex{
		display: flex;
		flex-wrap: wrap;
	}
	.bg_kuang{
		border: #3F536E solid 1rpx;
	}
	.colorfc98ff{
		color: #9e467c;
	}
	.color7898ff{
		color: #7898ff;
	}
	.preview{
		height: 30rpx;
		text-overflow: ellipsis;
		overflow: hidden;
	}
	.link{
		color: #5500ff;
	}
</style>

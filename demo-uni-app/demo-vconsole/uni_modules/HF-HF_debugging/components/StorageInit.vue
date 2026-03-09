<template>
  <view>
     <view class="">
	  	<text @click="eliminateAll">🚫</text>
	  </view>
	  <view class="">
		  <!-- {{exData}} -->
	  	<view class="details-top">
	  		<view  @click="openIndex==indx?openIndex=-1:openIndex=indx" v-for="(item,indx) in exData" :key="indx" class="details">
	  			<text>{{item.key}}</text>
				<text v-if="openIndex==indx">{{item.obj}}</text>
				<text v-else style="max-height: 100rpx;overflow: hidden;text-overflow: ellipsis;">{{item.obj}}</text>
				<text @click.stop="edit(item,indx)"> {{'✍'}} </text>
				<text @click.stop="copy(item)">{{'📋'}} </text>
				<text @click.stop="remove(item,indx)">{{'❌'}} </text>
	  		</view>
	  	</view>
	  </view>
	  
	  <view v-if="editState" class="edit-view">
			<text>key:</text>
			<input type="text" v-model="storageItem.key" />
			<text>obj:</text>
			<textarea :value="analysis" placeholder="" @input="textAreaInput" />
			<text class="bnt" @click.stop="editOK()">{{'💾'}}</text>
			<text class="bnt" @click.stop="editState=false">{{'❎'}}</text>
			<text>{{'待验证的'}}</text>
	  </view>
	  
  </view>
</template>

<script>
let textValue = ''
export default {
	name:'StorageInit',
	components:{
		
	},
	props:{
		livingExample:{
			type:Object,
			default:()=>{
				return {}
			}
		}
	},
	computed: {
		exData() {
			return this.livingExample.DATA.DATA
		},
		analysis:{
			get(){
				return JSON.stringify(this.storageItem.obj) 
			},
			set(val){
				if(typeof val == 'string'){
					try{
						val = JSON.parse(val)
					}catch(e){
						
					}
				}
				return this.storageItem.obj = val
			}
		}
	},
	data(){
		return {
			openIndex:0,
			storageItem:{},
			storageIndex:-1,
			editState:false,
		}
	},
	created() {
		
	},
	methods:{
		copy(item){
			uni.setClipboardData({
				data:''+ JSON.stringify(item),
				success() {
					uni.showToast({
						title:"复制成功"
					})
				},
				fail() {
					uni.showToast({
						title:"复制失败"
					})
				}
			})
		},
		eliminateAll(){
			this.livingExample.DATA.eliminateAll()
		},
		remove(item,index){
			this.livingExample.DATA.remove(item.key,index)
		},
		edit(item,index){
			this.storageItem = item
			this.storageIndex = index
			this.editState = true
		},
		editOK(){
			this.analysis = textValue
			this.livingExample.DATA.edit(this.storageItem,this.storageIndex)
			this.editState = false
		},
		textAreaInput(e){
			textValue = e.detail.value
		}
	}
};
</script>

<style lang="scss">
	*{
		word-break: break-all;
	}
	.details{
		display: flex;
		border-bottom: 1rpx solid #999999;
		text{
			border-right: 1rpx solid #999999;
			&:nth-child(1){
				flex: 1;
			}
			&:nth-child(2){
				flex: 6;
			}
		}
		view{
			border-bottom: 1rpx solid #999999;
			border-right: 1rpx solid #999999;
			flex: 6;
		}
		
	}
	.details-view{
		width: 100%;
	}
	.details-top{
		border-top: 1rpx solid #999999;
	}
	.edit-view{
		box-sizing: border-box;
		position: absolute;
		padding: 20rpx;
		top: 20rpx;
		left: 10vw;
		// margin: 0 auto;
		width: 80vw;
		max-height: 60vh;
		background: #ffffff;
		border: 1rpx solid #999999;
		box-shadow: 0rpx 10rpx 30rpx #333333;
		text{
			padding: 8rpx 0;
			font-weight: 600;
		}
		input{
			margin: 20rpx 0;
			width: 100%;
			box-sizing: border-box;
			border: 1rpx solid #999999;
		}
		textarea{
			margin: 20rpx 0;
			width: 100%;
			box-sizing: border-box;
			border: 1rpx solid #999999;
		}
		.bnt{
			margin-right: 80rpx;
			padding: 0 40rpx;
			border: 1rpx solid #999999;
		}
	}
</style>
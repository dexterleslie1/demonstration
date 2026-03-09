<template>
  <view>
	  <text class="del-text" @click="onDel">🚫</text>
	  <text>🚫</text>
	  <view class="headline">
		  <text class="method-text">Method</text>
		  <text class="url-text">Url</text>
		  <text class="status-text">Status</text>
		   <text class="status-text">选项</text>
	  </view>
	 <view :class="{error:item.Preview.statusCode>200&&item.Preview.statusCode<500||!item.Preview.statusCode,warn:item.Preview.statusCode>500 }" class="content" 
	 v-for="(item,index) in exData" :key="index">
		 <view @click="openIndex == index?openIndex=-1:openIndex=index" class="headline">
		 	<text class="method-text">{{item.Headers.method}}</text>
		 	<text class="url-text">{{item.Headers.url}}</text>
		 	<text class="status-text">{{item.Preview.statusCode||(0+'\n[推测CORS]')}}</text>
		 	<text @click.stop="showUp(item,index)" class="status-text">重发🆙</text>
		 </view>
		<view v-if="openIndex == index"  class="details-view">
			<block>
				<view @click="nthChild=0" class="">
					<text class="fw600 link">Headers</text>
				</view>
				<view v-if="nthChild==0" class="details-top">
					<view v-for="(ite,indx) of item.Headers" :key="indx" class="details">
						<text>{{indx}}</text>
						<text v-if="indx=='data'">{{JSON.stringify(ite,null,2)}}</text>
						<text v-else>{{ite}}</text>
						<text @click="copy(ite)">{{'📋'}}</text>
					</view>
					<!-- <view class="details">
						<text>data-json-stringify</text>
						<view  class="body-json">
							<objTree  :objects="{body:item.Headers.data}"></objTree>
						</view>
						<text @click="copy(item.Headers.data)">{{'📋'}}</text>
					</view> -->
				</view>
			</block>
			<block>
				<view @click="nthChild=1" class="">
					<text class="fw600 link">Preview</text>
				</view>
				<view v-if="nthChild==1" class="details-top">
					<view v-for="(ite,indx) of item.Preview" :key="indx" class="details">
						<text>{{indx}}</text>
						<text>{{ite}}</text>
						<text @click="copy(ite)">{{'📋'}}</text>
					</view>
					<view v-if="item.Preview.statusCode>=200&&item.Preview.statusCode<400" class="details">
						<text>body-json</text>
						<view class="body-json">
							<objTree :isShowKey="false" :objects="{body:item.Preview.data}"></objTree>
						</view>
						<text @click="copy(item.Preview.data)">{{'📋'}}</text>
					</view>
				</view>
			</block>
			<block v-if="item.Preview.statusCode>=500||(item.Preview.data+'').includes('<!DOCTYPE HTML PUBLIC')">
				<view @click="nthChild=2" class="">
					<text class="fw600 link">ServerHtml</text>
				</view>
				<view v-if="nthChild==2" class="details-top">
					<rich-text v-html="item.Preview.data" :nodes="item.Preview.data"></rich-text>
				</view>
			</block>
		</view>
	 </view>
  </view>
</template>

<script>
import objTree from './objTree.vue'
export default {
	name:"InterceptorRequest",
	components:{
		objTree
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
		exDataLength() {
			return this.livingExample.DATA.DATA.length
		},
		analysis(){
			return (item)=>this.solveLogItem(item)
		}
	},
	data(){
		return {
			openIndex:0,
			nthChild:0,
			list:[]
		}
	},
	mounted() {
		this.$emit("setScrollToLower")
	},
	methods:{
		copy(item){
			uni.setClipboardData({
				data:''+JSON.stringify(item),
				success() {
					uni.showToast({
						title:"复制成功"
					})
				}
			})
		},
		showUp(item,index){
			uni.request({...item.Headers})
		},
		onDel(){
			this.livingExample.DATA.init()
		}
	},
	created() {
		
	}
};
</script>

<style lang="scss">
	.warn{
		color: #f8b251;
		background: rgba($color: #c3a356, $alpha: .2);
		box-shadow: 0 0 1rpx #f8b251;
	}
	.error{
		color: #d50000;
		background: rgba($color: #7c3615, $alpha: .3);
		box-shadow: 0 0 1rpx #d50000;
	}
	*{
		word-break: break-all;
	}
	.headline{
		width: 100vw;
		display: flex;
		justify-content: space-between;
		border: 1rpx solid #303030;
		
		text{
			padding: 0 8rpx;
			border-right: 1rpx solid #303030;
			text-align: center;
			font-weight: 600;
			
		}
	}
	
	.method-text{
		flex: 1;
	}
	.url-text{
		flex: 3;
		max-width: 50%;
		text-align: right;
		text-overflow: overflow;
	}
	.status-text{
		flex: 1;
	}
	
	.fw600{
		font-weight: 600;
	}
	.link{
		margin-bottom: 4rpx;
		width: 100%;
		border-bottom: 2rpx solid #999999;
	}
	.details{
		display: flex;
		text{
			border-bottom: 1rpx solid #999999;
			border-right: 1rpx solid #999999;
			&:nth-child(1){
				flex: 1;
			}
			&:nth-child(2){
				flex: 6;
			}
		}
		.body-json{
			flex: 6;
		}
		
	}
	.details-view{
		width: 100%;
	}
	.details-top{
		border-top: 1rpx solid #999999;
	}
	.del-text{
		position: fixed;
		top: calc(20vh + 24rpx);
	}
</style>
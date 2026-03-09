<template>
	<view>
		<!-- 背景 -->
		<view class="mark" @click="onClickMack"></view>
		<!-- 主体 -->
		<view class="main" @click="setting=false" :class="{invert:settingData.invert.value}">
			<!-- 标题 -->
			<scroll-view class="headline-scroll-view" scroll-x="true">
				<text :class="{hit:plugInId==index}" class="headline" @click="plugInId=index,setPlugInId(index)" v-for="(item,index) in plugInName" :key="index">{{item}}</text>
				<text style="padding-right: 40rpx;"></text>
				<text @click.stop="setting=!setting" class="setting">🔧</text>
			</scroll-view>
			<!-- 组件模板-->
			<scroll-view :scroll-into-view="scrollToLowerId" :scroll-top="scrollTop" class="plug-in-scroll-view" scroll-y="true" @scrolltolower="scrolltolower" @scrolltoupper="scrolltoupper">
				<component ref="informationComponents" :is="information[plugInId].componentsName" :livingExample="information[plugInId]" 
						@setScrollTop="setScrollTop" @setScrollToLower="setScrollToLower" ></component>
				<text id="scroll-bot"></text>
			</scroll-view>
			<setting v-if="setting"></setting>
		</view>
	</view>
</template>

<script>
	import {
		HFDebuggingInformation,information
	} from './../common/store.js'
	
	import Console from './../components/Console.vue'
	import StorageInit from './../components/StorageInit.vue'
	import InterceptorRequest from './../components/InterceptorRequest.vue'
	import setting from './../components/setting.vue'
	import AppData from './../components/AppData.vue'
	import CurrentPages from './../components/CurrentPages.vue'
	
	export default {
		name:'HFBox',
		components: {
			InterceptorRequest,
			StorageInit,
			Console,
			setting,
			AppData,
			CurrentPages
		},
		data() {
			return {
				setting:false,
				plugInId:uni.getStorageSync("plugInId")||0,
				information,
				plugInName: [],
				componentsName: '',
				scrollTop:'100%',
				scrollToLowerId:'',
				settingData:HFDebuggingInformation[0].settingData
			}
		},
		created() {
			this.plugInName = information.map((item,index) => {
				return item.NAME
			})
		},
		methods:{
			scrolltolower(e){
				if(this.$refs?.informationComponents?.scrolltolower){
					this.$refs?.informationComponents?.scrolltolower(e)
				}
			},
			scrolltoupper(e){
				if(this.$refs?.informationComponents?.scrolltoupper){
					this.$refs?.informationComponents?.scrolltoupper(e)
				}
			},
			onClickMack(){
				uni.$emit("viewShow")
				uni.navigateBack({
					delta: 1
				})
			},
			setScrollTop(e){
				this.scrollTop = e
			},
			setScrollToLower(){
				this.scrollToLowerId = ''
				this.$nextTick(()=>{
					this.scrollToLowerId = 'scroll-bot'
				})
			},
			setPlugInId(index){
				 uni.setStorageSync("plugInId",index)
			}
		}
	};
</script>

<style lang="scss" scoped>
	/* *{
		font-size: 24rpx;
	} */
	.invert{
		filter: invert(100%);
	}
	.mark {
		position: fixed;
		z-index: 998;
		bottom: 0;
		left: 0;
		width: 100vw;
		height: 100vh;
		background: rgba(0, 0, 0, .3);
	}
	
	.main {
		padding:20rpx 0rpx;
		font-size: 24rpx;
		position: fixed;
		z-index: 9999;
		bottom: 0;
		left: 0;
		width: 100vw;
		height: 80vh;
		background: #FFFFFF;
	}
	.headline-scroll-view{
		// position: relative;
		width: 100vw;
		height: 44rpx;
	}
	.headline{
		padding: 6rpx;
		border: solid 1rpx #999999;
	}
	.hit{
		background: #b1b1b1;
	}
	.plug-in-scroll-view{
		width: 100vw;
		height: calc(80vh - 50rpx);
	}
	.setting{
		padding: 0 4rpx;
		position: absolute;
		right: 0;
		top: 0;
		border: 1rpx solid #999999;
	}
	#scroll-bot{
		width: 100vw;
		text-align: center;
	}
</style>

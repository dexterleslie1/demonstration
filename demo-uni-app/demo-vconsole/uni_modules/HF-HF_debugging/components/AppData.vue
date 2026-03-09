<template>
	<view class="">
		<view v-for="(item,index) in exData" :key="index" class="" >
			<view class="flex" style="font-weight: 600;">
				<text>{{exData.length-(index+1)}}AppData:</text>
				<text @click.stop="openType==index?openType=-1:openType=index">{{openType==index?'➖':'➕'}}</text>
			</view>
			<view class="bg_kuang">
				<objTree v-if="openType==index" :objects="item"></objTree>
			</view>			
		</view>
	</view>
</template>

<script>
	import objTree from './objTree.vue'
	export default {
		name: 'AppData',
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
				openType:1
			}
		},
		computed: {
			exData() {
				return this.livingExample.DATA.DATA
			}
		},
	}
</script>

<style lang="scss">
	.flex{
		display: flex;
	}
	.bg_kuang{
		border: #b8b9ff solid 1rpx;
	}
	
</style>

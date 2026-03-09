<template>
	<view @click.stop >
		<view class="setting">
			<text>这里是设置箱</text>
			<text>❌</text>
			<text>🚫</text>
			<br/>
			<text>请期待更多设置</text>
			<view v-for="(item,index) of HFDebuggingInformation.settingData" :key="index">
				<block v-if="item.type == 'switch'" >
					<text>{{item.name}}</text>
					<switch class="switch" :checked="item.value" @change="switchFunc(item,index,$event)" />
				</block>
				<text>{{index}}:</text>
				<text>{{item.value}}</text>
			</view>
		</view>
		
	</view>
</template>

<script>
	import {
		HFDebuggingInformation
	} from './../common/store.js'
	export default {
		name: 'setting',
		components: {

		},
		props: {},
		data() {
			return {
				HFDebuggingInformation
			}
		},
		created() {
			this.HFDebuggingInformation = HFDebuggingInformation[0]
		},
		methods:{
			switchFunc(item,index,$event){
				this.HFDebuggingInformation.settingData[index].value = !this.HFDebuggingInformation.settingData[index].value
			}
		}
	};
</script>

<style lang="scss">
	.setting {
		position: fixed;
		min-height: calc(80vh - 20rpx);
		width: 100vw;
		z-index: 999;
		left: 0;
		bottom: 0rpx;
		background: #333333;
		color: #FFFFFF;
	}
	.switch{
		transform:scale(0.6)
	}
</style>

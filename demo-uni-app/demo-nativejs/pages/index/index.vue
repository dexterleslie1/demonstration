<template>
	<view class="content">
		<image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view>
		<button type="primary" @click="showNativeToast">显示Android Toast</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'Hello'
			}
		},
		onLoad() {

		},
		methods: {
			showNativeToast() {
				// 平台检测
				if (plus.os.name !== 'Android') {
					uni.showToast({
						title: '此功能仅支持Android平台',
						icon: 'none'
					});
					return;
				}
				
				try {
					// 导入所需的Android类
					const Toast = plus.android.importClass('android.widget.Toast');
					const Activity = plus.android.runtimeMainActivity();
					
					// 创建并显示Toast消息
					Toast.makeText(Activity, '这是一个Android原生Toast消息', Toast.LENGTH_SHORT).show();
				} catch (error) {
					uni.showToast({
						title: 'Native.js调用失败',
						icon: 'none'
					});
					console.error('Native.js error:', error);
				}
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.logo {
		height: 200rpx;
		width: 200rpx;
		margin-top: 200rpx;
		margin-left: auto;
		margin-right: auto;
		margin-bottom: 50rpx;
	}

	.text-area {
		display: flex;
		justify-content: center;
	}

	.title {
		font-size: 36rpx;
		color: #8f8f94;
	}
</style>

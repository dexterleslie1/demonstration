<template>
	<view class="content">
		<image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view>
		<view class="button-section">
			<button class="request-button" @click="sendGetRequest">发送GET请求</button>
		</view>
		<view class="result-section">
			<text class="result-title">请求结果：</text>
			<text class="result-content">{{requestResult}}</text>
		</view>
	</view>
</template>

<script>
	export default {
			data() {
			return {
				title: 'Hello',
				requestResult: '点击按钮发送请求'
			}
		},
		onLoad() {
			console.log(`Hello World!`)
		},
		methods: {
			sendGetRequest() {
				uni.request({
					url: 'https://httpbin.org/get',
					method: 'GET',
					success: (res) => {
						console.log('请求成功:', res.data)
						this.requestResult = JSON.stringify(res.data, null, 2)
					},
					fail: (err) => {
						console.error('请求失败:', err)
						this.requestResult = '请求失败: ' + JSON.stringify(err)
					}
				})
			}
		}
	}
</script>

<style lang="scss">
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
		
		.title {
			font-size: 36rpx;
			font-weight: bold;
			color: yellowgreen;
		}
	}

	.button-section {
		margin-top: 50rpx;
		display: flex;
		justify-content: center;
	}

	.request-button {
		background-color: #007AFF;
		color: white;
		border: none;
		border-radius: 10rpx;
		padding: 20rpx 40rpx;
		font-size: 32rpx;
	}

	.result-section {
		margin-top: 40rpx;
		padding: 20rpx;
		background-color: #f5f5f5;
		border-radius: 10rpx;
		width: 80%;
		
		.result-title {
			font-size: 32rpx;
			font-weight: bold;
			color: #333;
			margin-bottom: 20rpx;
		}
		
		.result-content {
			font-size: 28rpx;
			color: #666;
			word-break: break-all;
			white-space: pre-wrap;
		}
	}
</style>

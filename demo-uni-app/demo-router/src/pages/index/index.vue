<template>
	<view class="container">
		<view class="header">
			<text class="title">uni-app 路由参数演示</text>
		</view>
		
		<view class="content">
			<view class="section">
				<text class="section-title">注意事项</text>
				<view class="warning-box">
					<text class="warning-text">⚠️ 在 uni-app 中，不能像 Vue Router 那样使用 this.$route.query 来获取路由参数！</text>
					<text class="info-text">正确的做法是：在 onLoad 生命周期中通过 options 参数获取。</text>
				</view>
			</view>
			
			<view class="section">
				<text class="section-title">路由跳转演示</text>
				
				<view class="button-group">
					<button class="btn" @tap="navigateToDetail">跳转到详情页 (带参数)</button>
				</view>
				
				<view class="code-example">
					<text class="code-title">示例代码:</text>
					<text class="code-content">// 跳转时传递参数
uni.navigateTo({
  url: '/pages/detail/detail?param1=hello&param2=world'
});
					</text>
					<text class="code-content">// 在详情页接收参数
onLoad(options) {
  console.log(options.param1); // hello
  console.log(options.param2); // world
}
					</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'uni-app 路由演示'
			}
		},
		onLoad() {

		},
		methods: {
			// 跳转到详情页并传递参数
			navigateToDetail() {
				uni.navigateTo({
					url: '/pages/detail/detail?param1=hello&param2=world'
				});
			}
		}
	}
</script>

<style scoped>
	.container {
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.header {
		text-align: center;
		padding: 30rpx 0;
		background-color: #ffffff;
		border-radius: 10rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333333;
	}

	.content {
		background-color: #ffffff;
		border-radius: 10rpx;
		padding: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.section {
		margin-bottom: 40rpx;
	}

	.section-title {
		display: block;
		font-size: 32rpx;
		font-weight: bold;
		color: #333333;
		margin-bottom: 20rpx;
		padding-bottom: 10rpx;
		border-bottom: 2rpx solid #e0e0e0;
	}

	.warning-box {
		padding: 20rpx;
		background-color: #fff3cd;
		border: 2rpx solid #ffeaa7;
		border-radius: 8rpx;
		margin-bottom: 20rpx;
	}

	.warning-text {
		display: block;
		font-size: 28rpx;
		font-weight: bold;
		color: #856404;
		margin-bottom: 10rpx;
	}

	.info-text {
		display: block;
		font-size: 26rpx;
		color: #6c5ce7;
		line-height: 40rpx;
	}

	.button-group {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
		margin-bottom: 30rpx;
	}

	.btn {
		padding: 20rpx;
		background-color: #007aff;
		color: #ffffff;
		border: none;
		border-radius: 8rpx;
		font-size: 28rpx;
		cursor: pointer;
	}

	.btn:active {
		background-color: #0056b3;
	}

	.code-example {
		padding: 20rpx;
		background-color: #f8f9fa;
		border-radius: 8rpx;
	}

	.code-title {
		display: block;
		font-size: 26rpx;
		font-weight: bold;
		color: #333333;
		margin-bottom: 10rpx;
	}

	.code-content {
		display: block;
		font-size: 24rpx;
		color: #666666;
		background-color: #ffffff;
		padding: 15rpx;
		border-radius: 6rpx;
		margin-bottom: 10rpx;
		white-space: pre-wrap;
		font-family: monospace;
		border: 1rpx solid #e0e0e0;
	}
</style>

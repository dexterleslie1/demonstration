<template>
	<view class="container">
		<view class="header">
			<text class="title">路由参数演示</text>
		</view>
		
		<view class="content">
			<view class="section">
				<text class="section-title">错误的获取方式 (Vue Router 方式)</text>
				<text class="error-text">{{ errorMessage }}</text>
			</view>
			
			<view class="section">
				<text class="section-title">正确的获取方式 (uni-app 方式)</text>
				<view class="success-info">
					<text>参数 param1: {{ param1 }}</text>
					<text>参数 param2: {{ param2 }}</text>
				</view>
			</view>
			
			<view class="section">
				<text class="section-title">使用 getCurrentPages() 获取参数</text>
				<view class="success-info">
					<text>参数 param1: {{ currentPagesParam1 }}</text>
					<text>参数 param2: {{ currentPagesParam2 }}</text>
				</view>
				<!-- <button class="btn" @tap="useGetCurrentPages">使用 getCurrentPages() 获取参数</button> -->
			</view>
			
			<view class="button-group">
				<button class="btn" @tap="goBack">返回首页</button>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			param1: '',
			param2: '',
			errorMessage: '',
			currentPagesParam1: '',
			currentPagesParam2: ''
		};
	},
	onLoad(options) {
		// 尝试使用错误的方式获取参数 (Vue Router)
		this.tryWrongWay();
		
		// 使用正确的方式获取参数 (uni-app)
		this.useCorrectWay(options);
		
		// 使用 getCurrentPages() 获取参数
		this.useGetCurrentPages();
	},
	methods: {
		// 错误的获取方式：尝试使用 this.$route.query
		tryWrongWay() {
			try {
				// 这是 Vue Router 的用法，在 uni-app 中不适用
				const param1 = this.$route.query.param1;
				this.errorMessage = `错误方式: this.$route.query.param1 = ${param1}`;
			} catch (error) {
				this.errorMessage = `错误信息: ${error.message}`;
			}
		},
		
		// 正确的获取方式：使用 onLoad 生命周期的 options 参数
		useCorrectWay(options) {
			if (options) {
				this.param1 = options.param1 || '未传递';
				this.param2 = options.param2 || '未传递';
			}
		},
		
		// 使用 getCurrentPages() 获取参数
		useGetCurrentPages() {
			// 获取当前页面栈
			const pages = getCurrentPages();
			// 获取当前页面实例 (页面栈的最后一个元素)
			const currentPage = pages[pages.length - 1];
			// 获取页面参数
			const pageOptions = currentPage.options;
			// 存储参数到 data
			this.currentPagesParam1 = pageOptions.param1 || '未传递';
			this.currentPagesParam2 = pageOptions.param2 || '未传递';
		},
		
		// 返回首页
		goBack() {
			uni.navigateBack({
				delta: 1
			});
		}
	}
};
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

.error-text {
	display: block;
	padding: 20rpx;
	background-color: #ffebee;
	color: #c62828;
	border-radius: 8rpx;
	font-size: 26rpx;
	line-height: 40rpx;
}

.success-info {
	padding: 20rpx;
	background-color: #e8f5e9;
	border-radius: 8rpx;
	font-size: 28rpx;
	color: #2e7d32;
	line-height: 48rpx;
}

.button-group {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
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
</style>
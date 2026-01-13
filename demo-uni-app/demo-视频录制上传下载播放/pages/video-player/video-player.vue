<template>
	<view class="content">
		<text class="title">视频播放</text>
		<view class="video-container">
			<video class="video-player" :src="videoUrl" controls autoplay></video>
		</view>
		<view class="actions">
			<button class="btn-back" @click="goBack">返回</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				videoUrl: ''
			}
		},
		onLoad(options) {
			// 获取传递过来的视频URL
			this.videoUrl = decodeURIComponent(options.url);
			console.log('视频URL:', this.videoUrl);
		},
		methods: {
			goBack() {
				uni.navigateBack();
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 20rpx;
	}

	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 40rpx;
	}

	.video-container {
		width: 100%;
		max-width: 600rpx;
	}

	.video-player {
		width: 100%;
		height: 400rpx;
		border-radius: 10rpx;
	}

	.actions {
		margin-top: 40rpx;
		width: 100%;
		max-width: 600rpx;
	}

	.btn-back {
		width: 100%;
		padding: 20rpx;
		background-color: #007aff;
		color: #fff;
		border: none;
		border-radius: 5rpx;
		font-size: 28rpx;
	}
</style>
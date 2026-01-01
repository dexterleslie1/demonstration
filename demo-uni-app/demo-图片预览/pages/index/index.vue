<template>
	<view class="content">
		<text class="title">图片预览功能演示</text>
		
		<button class="btn" @tap="chooseImage">从相册选择图片</button>
		
		<view class="image-container" v-if="imagePaths.length">
			<text class="section-title">已选择的图片</text>
			<scroll-view class="image-list" scroll-x>
				<view class="image-item" v-for="(path, index) in imagePaths" :key="index" @tap="previewImage(index)">
					<image class="thumbnail" :src="path" mode="aspectFill"></image>
				</view>
			</scroll-view>
			
			<button class="btn" @tap="previewImage">预览所有图片</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				imagePaths: [] // 存储图片路径的数组
			}
		},
		onLoad() {

		},
		methods: {
			// 从相册选择图片
			chooseImage() {
				uni.chooseImage({
					count: 9, // 最多选择9张图片
					sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album'], // 使用相册
					success: (res) => {
						// 获取图片路径
						this.imagePaths = res.tempFilePaths;
						console.log('图片路径:', this.imagePaths);
					},
					fail: (err) => {
						console.error('选择图片失败:', err);
						uni.showToast({
							title: '选择图片失败',
							icon: 'none'
						});
					}
				});
			},
			
			// 预览图片
			previewImage(index = 0) {
				if (this.imagePaths.length === 0) {
					uni.showToast({
						title: '请先选择图片',
						icon: 'none'
					});
					return;
				}
				
				// 使用 uni.previewImage API 预览图片
				uni.previewImage({
					current: this.imagePaths[index], // 当前显示图片的链接
					urls: this.imagePaths, // 需要预览的图片链接列表
					indicator: 'number', // 图片指示器样式，可取值："default" | "number" | "none"
					loop: true, // 是否可循环预览
					success: () => {
						console.log('预览图片成功');
					},
					fail: (err) => {
						console.error('预览图片失败:', err);
						uni.showToast({
							title: '预览图片失败',
							icon: 'none'
						});
					}
				});
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 40rpx;
		background-color: #f8f8f8;
		min-height: 100vh;
	}

	.title {
		font-size: 40rpx;
		color: #333333;
		font-weight: bold;
		margin-bottom: 40rpx;
	}

	.btn {
		width: 80%;
		padding: 20rpx;
		background-color: #007aff;
		color: #ffffff;
		border-radius: 10rpx;
		font-size: 32rpx;
		margin-bottom: 30rpx;
	}

	.btn:active {
		background-color: #0056b3;
	}

	.image-container {
		width: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-top: 20rpx;
		background-color: #ffffff;
		border-radius: 10rpx;
		padding: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.section-title {
		font-size: 32rpx;
		color: #333333;
		font-weight: bold;
		margin-bottom: 20rpx;
	}

	.image-list {
		width: 100%;
		white-space: nowrap;
		margin-bottom: 20rpx;
	}

	.image-item {
		display: inline-block;
		width: 150rpx;
		height: 150rpx;
		margin-right: 10rpx;
		border-radius: 8rpx;
		overflow: hidden;
	}

	.thumbnail {
		width: 100%;
		height: 100%;
	}
</style>

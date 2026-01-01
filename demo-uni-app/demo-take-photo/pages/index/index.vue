<template>
	<view class="content">
		<text class="title">拍照和预览功能</text>
		
		<button class="btn" @tap="takePhoto">拍照</button>
		
		<view class="image-container" v-if="photoPath">
			<image class="photo" :src="photoPath" mode="aspectFit"></image>
			<view class="button-row">
				<button class="btn" @tap="saveToAlbum">保存到相册</button>
				<button class="btn" @tap="deletePhoto">删除照片</button>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				photoPath: '' // 存储照片路径
			};
		},
		onLoad() {

		},
		methods: {
			// 拍照方法
			takePhoto() {
				uni.chooseImage({
					count: 1, // 最多选择1张图片
					sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
					sourceType: ['camera'], // 使用相机
					success: (res) => {
						// 获取图片路径
						this.photoPath = res.tempFilePaths[0];
						console.log('图片路径:', this.photoPath);
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
			
			// 保存到相册方法
			saveToAlbum() {
				if (!this.photoPath) {
					uni.showToast({
						title: '请先拍照',
						icon: 'none'
					});
					return;
				}
				
				// 检查平台兼容性问题
				// H5平台不支持saveImageToPhotosAlbum，需要使用其他方法
				// #ifdef H5
				uni.showModal({
					title: '提示',
					content: '在H5环境中，图片已经保存在临时路径中，您可以右键保存图片。',
					showCancel: false
				});
				return;
				// #endif
				
				// 非H5平台，使用标准保存方法
				// #ifndef H5
				uni.saveImageToPhotosAlbum({
					filePath: this.photoPath,
					success: () => {
						uni.showToast({
							title: '保存成功',
							icon: 'success'
						});
					},
					fail: (err) => {
						console.error('保存到相册失败:', err);
						
						// 如果是权限问题，尝试请求权限
						if (err.errMsg && err.errMsg.includes('auth deny')) {
							uni.showModal({
								title: '权限请求',
								content: '需要访问相册权限以保存图片',
								success: (res) => {
									if (res.confirm) {
										// 请求相册权限
										uni.openSetting({
											success: (settingRes) => {
												// 重新尝试保存
												if (settingRes.authSetting['scope.writePhotosAlbum']) {
													this.saveToAlbum(); // 递归调用，重新尝试保存
												}
											}
										});
									}
								}
							});
						} else {
							uni.showToast({
								title: '保存失败',
								icon: 'none'
							});
						}
					}
				});
				// #endif
			},
			
			// 删除图片方法
			deletePhoto() {
				this.photoPath = ''; // 清空图片路径
				uni.showToast({
					title: '照片已删除',
					icon: 'success'
				});
			}
		},
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
	}

	.photo {
		width: 90%;
		height: 600rpx;
		border-radius: 10rpx;
		margin-bottom: 30rpx;
	}

	.button-row {
		display: flex;
		justify-content: space-between;
		width: 90%;
		gap: 20rpx;
	}

	.button-row .btn {
		flex: 1;
		margin-bottom: 0;
	}
</style>

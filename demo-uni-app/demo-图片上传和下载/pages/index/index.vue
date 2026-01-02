<template>
	<view class="content">
		<view class="header">
			<text class="title">图片上传和下载演示</text>
		</view>
		
		<view class="upload-section">
			<button class="btn-primary" @click="chooseImage">从相册选择图片</button>
			<button class="btn-success" @click="uploadImage" :disabled="!selectedImage || uploading">
				{{ uploading ? '上传中...' : '上传图片' }}
			</button>
		</view>

		<view class="preview-section" v-if="selectedImage">
			<text class="section-title">已选择的图片：</text>
			<image :src="selectedImage" class="preview-image" @click="previewImage(selectedImage)"></image>
		</view>

		<view class="uploaded-section" v-if="uploadedFiles.length > 0">
			<text class="section-title">已上传的图片：</text>
			<view class="uploaded-list">
				<view v-for="(file, index) in uploadedFiles" :key="index" class="uploaded-item">
					<text class="filename">{{ file }}</text>
					<button class="btn-download" @click="downloadImage(file)">下载</button>
					<button class="btn-preview" @click="previewUploadedImage(file)">预览</button>
				</view>
			</view>
		</view>

		<view class="message" v-if="message">{{ message }}</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'Hello',
				selectedImage: '', // 选中的图片路径
				uploadedFiles: [], // 已上传的文件列表
				uploading: false, // 上传状态
				message: '', // 消息提示
				apiBaseUrl: 'http://192.168.1.182:8080/api/v1' // API基础地址
			}
		},
		onLoad() {
			// 根据平台显示不同的提示
			// #ifdef APP-PLUS
			this.message = 'APP模式：如遇上传失败，请确保服务器运行在 localhost:8080'
			// #endif
			// #ifdef H5
			this.message = 'H5模式：网络连接正常'
			// #endif
		},
		methods: {
			// 从相册选择图片
			chooseImage() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album'],
					success: (res) => {
						this.selectedImage = res.tempFilePaths[0]
						this.message = '图片选择成功'
					},
					fail: (err) => {
						console.error('选择图片失败:', err)
						this.message = '选择图片失败: ' + (err.errMsg || '未知错误')
					}
				})
			},

			// 上传图片到服务器
			uploadImage() {
				if (!this.selectedImage) {
					this.message = '请先选择图片'
					return
				}

				this.uploading = true
				this.message = '开始上传...'

				// 构建上传参数
				const uploadConfig = {
					url: this.apiBaseUrl + '/postWithFileUpload',
					filePath: this.selectedImage,
					name: 'files',
					formData: {},
					success: (res) => {
						try {
							const response = JSON.parse(res.data)
							if (response.errorCode == 0 && response.data && response.data.length > 0) {
								// 将上传的文件名添加到列表中
								this.uploadedFiles.push(...response.data)
								this.message = '上传成功！文件已保存到服务器'
								this.selectedImage = '' // 清空选中的图片
							} else {
								this.message = '上传失败: ' + (response.errorMessage || '服务器返回数据格式错误')
							}
						} catch (e) {
							this.message = '上传失败: 解析服务器响应失败'
							console.error('解析响应失败:', e)
						}
					},
					fail: (err) => {
						console.error('上传失败:', JSON.stringify(err))
						
						// #ifdef APP-PLUS
						// APP端错误处理
						if (err.errMsg && err.errMsg.includes('uploadFile:fail')) {
							this.message = 'APP上传失败：网络连接问题\n解决方案：\n1. 确保服务器运行在 http://localhost:8080\n2. 检查manifest.json中的域名白名单配置\n3. 确保APP有网络权限'
						} else {
							this.message = 'APP上传失败: ' + (err.errMsg || '未知错误')
						}
						// #endif
						
						// #ifdef H5
						// H5端错误处理
						this.message = 'H5上传失败: ' + (err.errMsg || '网络错误')
						// #endif
					},
					complete: () => {
						this.uploading = false
					}
				}

				// #ifdef APP-PLUS
				// APP端添加超时和头部配置
				uploadConfig.timeout = 10000
				// #endif

				uni.uploadFile(uploadConfig)
			},

			// 下载图片
			downloadImage(filename) {
				if (!filename) {
					this.message = '文件名不能为空'
					return
				}

				this.message = '开始下载...'

				const downloadConfig = {
					url: this.apiBaseUrl + '/' + filename,
					success: (res) => {
						if (res.statusCode === 200) {
							// 下载成功，可以保存到相册或显示
							this.message = '下载成功'
							// 可以选择保存到相册
							uni.saveImageToPhotosAlbum({
								filePath: res.tempFilePath,
								success: () => {
									this.message = '下载并保存到相册成功'
								},
								fail: (saveErr) => {
									console.error('保存到相册失败:', saveErr)
									this.message = '下载成功，但保存到相册失败'
								}
							})
						} else {
							this.message = '下载失败: HTTP ' + res.statusCode
						}
					},
					fail: (err) => {
						console.error('下载失败:', err)
						
						// #ifdef APP-PLUS
						// APP端错误处理
						if (err.errMsg && err.errMsg.includes('downloadFile:fail')) {
							this.message = 'APP下载失败：网络连接问题\n解决方案：\n1. 确保服务器运行在 http://localhost:8080\n2. 检查manifest.json中的域名白名单配置\n3. 确保APP有网络权限'
						} else {
							this.message = 'APP下载失败: ' + (err.errMsg || '未知错误')
						}
						// #endif
						
						// #ifdef H5
						// H5端错误处理
						this.message = 'H5下载失败: ' + (err.errMsg || '网络错误')
						// #endif
					}
				}

				// #ifdef APP-PLUS
				// APP端添加超时配置
				downloadConfig.timeout = 10000
				// #endif

				uni.downloadFile(downloadConfig)
			},

			// 预览选中的图片
			previewImage(imagePath) {
				if (!imagePath) return
				
				console.log('预览选中图片路径:', imagePath)
				
				uni.previewImage({
					current: imagePath,
					urls: [imagePath],
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
				})
			},

			// 预览已上传的图片
			previewUploadedImage(filename) {
				if (!filename) return
				
				// 生成预览URL
				const previewUrl = this.apiBaseUrl + '/' + filename
				console.log('预览已上传图片路径:', previewUrl)
				
				uni.previewImage({
					current: previewUrl,
					urls: [previewUrl]
				})
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.header {
		text-align: center;
		margin-bottom: 40rpx;
		padding: 20rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}

	.upload-section {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
		margin-bottom: 40rpx;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.btn-primary, .btn-success {
		padding: 20rpx 40rpx;
		border-radius: 8rpx;
		font-size: 32rpx;
		font-weight: bold;
		border: none;
		color: #fff;
	}

	.btn-primary {
		background-color: #007aff;
	}

	.btn-primary:hover {
		background-color: #0056d3;
	}

	.btn-success {
		background-color: #34c759;
	}

	.btn-success:hover {
		background-color: #28a745;
	}

	.btn-success:disabled {
		background-color: #ccc;
		color: #999;
	}

	.preview-section, .uploaded-section {
		margin-bottom: 30rpx;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.preview-image {
		width: 300rpx;
		height: 300rpx;
		border-radius: 10rpx;
		border: 2rpx solid #ddd;
	}

	.uploaded-list {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.uploaded-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx;
		background-color: #f8f9fa;
		border-radius: 8rpx;
		border: 1rpx solid #e9ecef;
	}

	.filename {
		font-size: 28rpx;
		color: #495057;
		flex: 1;
		margin-right: 20rpx;
		word-break: break-all;
	}

	.btn-download, .btn-preview {
		padding: 10rpx 20rpx;
		border-radius: 6rpx;
		font-size: 24rpx;
		border: none;
		color: #fff;
		margin-left: 10rpx;
	}

	.btn-download {
		background-color: #007aff;
	}

	.btn-preview {
		background-color: #ff9500;
	}

	.message {
		text-align: center;
		padding: 20rpx;
		margin-top: 20rpx;
		border-radius: 8rpx;
		font-size: 28rpx;
		background-color: #e3f2fd;
		color: #1976d2;
		border: 1rpx solid #bbdefb;
	}

	/* 响应式设计 */
	@media (max-width: 750px) {
		.content {
			padding: 10rpx;
		}
		
		.upload-section, .preview-section, .uploaded-section {
			padding: 20rpx;
		}
		
		.uploaded-item {
			flex-direction: column;
			align-items: stretch;
			gap: 15rpx;
		}
		
		.btn-download, .btn-preview {
			margin: 0;
		}
	}
</style>

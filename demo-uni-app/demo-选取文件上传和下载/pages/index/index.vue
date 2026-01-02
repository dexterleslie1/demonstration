<template>
	<view class="content">
		<view class="header">
			<text class="title">文件上传和下载演示</text>
		</view>
		
		<view class="upload-section">
			<button class="btn-primary" @click="chooseFile">选择文件</button>
			<button class="btn-success" @click="uploadFile" :disabled="!selectedFile || uploading">
				{{ uploading ? '上传中...' : '上传文件' }}
			</button>
		</view>

		<xe-upload ref="xeUpload" :options="uploadOptions" @callback="handleUploadCallback"></xe-upload>
		
		<view class="xe-upload-wrapper">
			<text class="xe-upload-title">跨平台文件选择</text>
			<text class="xe-upload-info">
				本功能使用 xe-upload 组件实现
				支持 H5、APP、微信小程序等平台
				点击上方按钮选择文件
			</text>
		</view>

		<view class="preview-section" v-if="selectedFile">
			<text class="section-title">已选择的文件：</text>
			<view class="file-info">
				<text class="filename">{{ getFileName(selectedFile) }}</text>
				<text class="filesize">{{ formatFileSize(selectedFileSize) }}</text>
			</view>
		</view>

		<view class="uploaded-section" v-if="uploadedFiles.length > 0">
			<text class="section-title">已上传的文件：</text>
			<view class="uploaded-list">
				<view v-for="(file, index) in uploadedFiles" :key="index" class="uploaded-item">
					<text class="filename">{{ file }}</text>
					<button class="btn-download" @click="downloadFile(file)">下载</button>
				</view>
			</view>
		</view>

		<view class="message" v-if="message">{{ message }}</view>
	</view>
</template>

<script>
	import XeUpload from '@/uni_modules/xe-upload/components/xe-upload/xe-upload.vue'
	
	export default {
		components: {
			XeUpload
		},
		data() {
			return {
				title: 'Hello',
				selectedFile: '', // 选中的文件路径
				selectedFileSize: 0, // 文件大小
				uploadedFiles: [], // 已上传的文件列表
				uploading: false, // 上传状态
				message: '', // 消息提示
				apiBaseUrl: '', // API基础地址，将在运行时根据环境设置
				uploadOptions: {
					// 不设置url，让组件返回本地文件信息
					name: 'file',
					extension: ['.doc', '.docx', '.pdf', '.txt', '.zip', '.rar', '.xls', '.xlsx', '.ppt', '.pptx', '.jpg', '.jpeg', '.png', '.gif']
				}
			}
		},
		onLoad() {
			// 根据运行环境设置 API 基础 URL
			// #ifdef H5
			// H5 环境通常可以通过 localhost 或 127.0.0.1 访问
			this.apiBaseUrl = 'http://127.0.0.1:8080/api/v1';
			this.message = 'H5模式：网络连接正常'
			// #endif
			
			// #ifdef APP-PLUS
			// APP 环境可能需要使用实际 IP 地址
			this.apiBaseUrl = 'http://192.168.1.182:8080/api/v1';
			this.message = 'APP模式：如遇上传失败，请确保服务器运行在 http://192.168.1.182:8080'
			// #endif
			
			// #ifdef MP-WEIXIN
			// 小程序环境可能需要使用 https 域名
			this.apiBaseUrl = 'https://your-domain.com/api/v1';
			this.message = '小程序模式：请确保服务器支持 HTTPS 并已配置域名白名单'
			// #endif
		},
		methods: {
			// 处理xe-upload组件的回调
			handleUploadCallback(e) {
				const { type, data } = e
				
				switch (type) {
					case 'choose':
						// 处理选择的文件
						console.log('选择了文件:', data)
						if (data && data.length > 0) {
							const file = data[0] // 获取第一个选择的文件
							this.selectedFile = file.tempFilePath || ''
							this.selectedFileSize = file.size || 0
							this.message = `文件选择成功: ${file.name || '未知文件'} (${this.formatFileSize(file.size || 0)})`
						} else {
							this.message = '没有选择文件'
						}
						break
						
					case 'success':
						// 上传成功（这里不会触发，因为我们没有设置url）
						console.log('上传成功:', data)
						break
						
					case 'warning':
						// 错误信息
						console.error('选择文件出错:', data)
						this.message = '选择文件失败: ' + (data.message || data.errMsg || '未知错误')
						break
						
					default:
						console.log('未知回调类型:', type, data)
				}
			},

			// 选择文件
			chooseFile() {
				// 使用xe-upload组件选择文件
				// 组件会自动根据平台选择合适的文件选择方式
				this.$refs.xeUpload.upload('file')
			},

			// 获取文件名
			getFileName(filePath) {
				if (!filePath) return ''
				const parts = filePath.split('/')
				return parts[parts.length - 1]
			},

			// 格式化文件大小
			formatFileSize(size) {
				if (size === 0) return '0 B'
				const k = 1024
				const sizes = ['B', 'KB', 'MB', 'GB']
				const i = Math.floor(Math.log(size) / Math.log(k))
				return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
			},

			// 上传文件到服务器
			uploadFile() {
				if (!this.selectedFile) {
					this.message = '请先选择文件'
					return
				}

				this.uploading = true
				this.message = '开始上传...'

				// 构建上传参数
				const uploadConfig = {
					url: this.apiBaseUrl + '/postWithFileUpload',
					filePath: this.selectedFile,
					name: 'files',
					formData: {},
					success: (res) => {
						try {
							const response = JSON.parse(res.data)
							if (response.errorCode == 0 && response.data && response.data.length > 0) {
								// 将上传的文件名添加到列表中
								this.uploadedFiles.push(...response.data)
								this.message = '上传成功！文件已保存到服务器'
								this.selectedFile = '' // 清空选中的文件
								this.selectedFileSize = 0 // 清空文件大小
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
							this.message = 'APP上传失败：网络连接问题\n解决方案：\n1. 确保服务器运行在 http://192.168.1.182:8080\n2. 检查manifest.json中的域名白名单配置\n3. 确保APP有网络权限'
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
				// APP端添加超时配置
				uploadConfig.timeout = 10000
				// #endif

				uni.uploadFile(uploadConfig)
			},

			// 下载文件
			downloadFile(filename) {
				if (!filename) {
					this.message = '文件名不能为空'
					return
				}

				this.message = '开始下载...'

				const downloadConfig = {
					url: this.apiBaseUrl + '/' + filename,
					success: (res) => {
						if (res.statusCode === 200) {
							// 下载成功
							this.message = '下载成功'
							
							// #ifdef H5
							// H5端保存文件
							const a = document.createElement('a')
							a.href = res.tempFilePath
							a.download = filename
							document.body.appendChild(a)
							a.click()
							document.body.removeChild(a)
							this.message = '下载并保存文件成功'
							// #endif
							
							// #ifdef APP-PLUS
							// APP端下载完成，只显示文件路径
							this.message = `文件下载成功: ${res.tempFilePath}`
							// 询问用户是否需要打开文件
							// this.openWithDefaultApp(res.tempFilePath, filename)
							// #endif
						} else {
							this.message = '下载失败: HTTP ' + res.statusCode
						}
					},
					fail: (err) => {
						console.error('下载失败:', err)
						
						// #ifdef APP-PLUS
						// APP端错误处理
						if (err.errMsg && err.errMsg.includes('downloadFile:fail')) {
							this.message = 'APP下载失败：网络连接问题\n解决方案：\n1. 确保服务器运行在 http://192.168.1.182:8080\n2. 检查manifest.json中的域名白名单配置\n3. 确保APP有网络权限'
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

			// 使用默认应用打开文件
			openWithDefaultApp(filePath, filename) {
				uni.showModal({
					title: '下载完成',
					content: `文件已下载，是否使用默认应用打开？`,
					success: (res) => {
						if (res.confirm) {
							uni.openDocument({
								filePath: filePath,
								success: () => {
									this.message = '文件已打开'
								},
								fail: () => {
									this.message = '无法打开文件，请检查文件格式是否支持'
								}
							})
						} else {
							this.message = '文件已下载，请手动保存'
						}
					}
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
	
	.xe-upload-wrapper {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 20rpx;
		margin-bottom: 20rpx;
		background-color: #f8f9fa;
		border-radius: 8rpx;
		border: 1rpx dashed #dee2e6;
	}
	
	.xe-upload-title {
		font-size: 28rpx;
		color: #6c757d;
		margin-bottom: 15rpx;
	}
	
	.xe-upload-info {
		font-size: 24rpx;
		color: #adb5bd;
		text-align: center;
		line-height: 1.5;
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

	.file-info {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
		background-color: #f8f9fa;
		border-radius: 8rpx;
		border: 1rpx solid #e9ecef;
	}

	.filename {
		font-size: 28rpx;
		color: #495057;
		margin-bottom: 10rpx;
		word-break: break-all;
	}

	.filesize {
		font-size: 24rpx;
		color: #6c757d;
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

	.uploaded-item .filename {
		flex: 1;
		margin-right: 20rpx;
		word-break: break-all;
	}

	.btn-download {
		padding: 10rpx 20rpx;
		border-radius: 6rpx;
		font-size: 24rpx;
		border: none;
		color: #fff;
		background-color: #007aff;
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
		
		.btn-download {
			margin: 0;
		}
	}
</style>

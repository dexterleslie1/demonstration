<template>
	<view class="content">
		<view class="header">
			<text class="title">多文件选择演示</text>
		</view>
		
		<view class="file-select-section">
			<button class="btn-primary" @click="chooseFiles">选择多个文件</button>
			<button class="btn-secondary" @click="clearFiles" :disabled="selectedFiles.length === 0">清空文件列表</button>
		</view>
		
		<xe-upload ref="xeUpload" :options="uploadOptions" @callback="handleUploadCallback"></xe-upload>

		<view class="files-section" v-if="selectedFiles.length > 0">
			<text class="section-title">已选择的文件：</text>
			<view class="file-list">
				<view v-for="(file, index) in selectedFiles" :key="index" class="file-item">
					<view class="file-info">
						<text class="filename">{{ file.name || file }}</text>
						<text class="filesize" v-if="file.size">{{ formatFileSize(file.size) }}</text>
					</view>
					<button class="btn-remove" @click="removeFile(index)">移除</button>
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
				selectedFiles: [], // 选中的文件列表
				message: '', // 消息提示
				uploadOptions: {
					// 不设置url，让组件返回本地文件信息
					name: 'file',
					extension: ['.doc', '.docx', '.pdf', '.txt', '.zip', '.rar', '.xls', '.xlsx', '.ppt', '.pptx', '.jpg', '.jpeg', '.png', '.gif']
				}
			}
		},
		onLoad() {
			// 根据运行环境显示不同的提示
			// #ifdef H5
			this.message = 'H5模式：点击"选择多个文件"按钮选择文件'
			// #endif
			
			// #ifdef APP-PLUS
			this.message = 'APP模式：点击"选择多个文件"按钮选择文件'
			// #endif
			
			// #ifdef MP-WEIXIN
			this.message = '小程序模式：点击"选择多个文件"按钮选择文件'
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
						// 将组件返回的文件信息转换为我们需要的格式
						const files = data.map(file => ({
							name: file.name || '未知文件',
							size: file.size || 0,
							path: file.tempFilePath || '',
							type: file.type || 'application/octet-stream',
							fileType: file.fileType || 'file'
						}))
						
						this.selectedFiles = [...this.selectedFiles, ...files]
						this.message = `已选择 ${files.length} 个文件，总计 ${this.selectedFiles.length} 个`
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

			// 选择多个文件
			chooseFiles() {
				// 使用xe-upload组件选择文件
				// 组件会自动根据平台选择合适的文件选择方式
				this.$refs.xeUpload.upload('file')
			},

			// 移除文件
			removeFile(index) {
				this.selectedFiles.splice(index, 1)
				this.message = `已移除文件，剩余 ${this.selectedFiles.length} 个文件`
			},

			// 清空文件列表
			clearFiles() {
				this.selectedFiles = []
				this.message = '已清空文件列表'
			},

			// 格式化文件大小
			formatFileSize(size) {
				if (size === 0) return '0 B'
				const k = 1024
				const sizes = ['B', 'KB', 'MB', 'GB']
				const i = Math.floor(Math.log(size) / Math.log(k))
				return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
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

	.file-select-section {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
		margin-bottom: 40rpx;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.btn-primary, .btn-secondary {
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

	.btn-secondary {
		background-color: #8e8e93;
	}

	.btn-secondary:hover {
		background-color: #6d6d72;
	}

	.btn-secondary:disabled {
		background-color: #ccc;
		color: #999;
	}

	.files-section {
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
	}

	.file-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.file-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx;
		background-color: #f8f8f8;
		border-radius: 8rpx;
	}

	.file-info {
		display: flex;
		flex-direction: column;
		flex: 1;
		overflow: hidden;
	}

	.filename {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 10rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.filesize {
		font-size: 24rpx;
		color: #8e8e93;
	}

	.btn-remove {
		padding: 10rpx 20rpx;
		border-radius: 6rpx;
		font-size: 26rpx;
		border: none;
		background-color: #ff3b30;
		color: #fff;
	}

	.message {
		padding: 20rpx;
		text-align: center;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
		margin-top: 20rpx;
	}
</style>
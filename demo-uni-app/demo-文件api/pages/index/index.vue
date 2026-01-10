<template>
	<view class="content">
		<image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view>
		
		<!-- 文件API示例按钮 -->
		<view class="api-list">
			<button @click="saveFileDemo" type="primary">保存文件</button>
			<button @click="getSavedFileListDemo" type="default">获取保存的文件列表</button>
			<button @click="getSavedFileInfoDemo" type="default">获取文件信息</button>
			<button @click="removeSavedFileDemo" type="warn">删除文件</button>
		</view>
		
		<!-- 结果展示 -->
		<view class="result" v-if="result">
			<text class="result-title">操作结果：</text>
			<text class="result-content">{{result}}</text>
		</view>
		
		<!-- 文件列表展示 -->
		<view class="file-list" v-if="fileList.length > 0">
			<text class="file-list-title">文件列表：</text>
			<view class="file-item" 
				  v-for="(file, index) in fileList" 
				  :key="index"
				  :class="{ 'file-item-selected': index === selectedIndex }"
				  @click="selectFile(file, index)">
				<text class="file-index">{{index + 1}}. </text>
				<text class="file-path">{{file.filePath}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: '文件API示例',
				result: '',
				savedFilePath: '', // 保存的文件路径，用于测试其他API
				fileList: [], // 保存的文件列表
				selectedIndex: -1 // 当前选中的文件索引，-1表示未选中
			}
		},
		onLoad() {

		},
		methods: {
			// 保存文件示例
			saveFileDemo() {
				// 注意：实际使用时需要先通过uni.downloadFile等获取临时文件路径
				// 这里使用一个模拟的临时文件路径示例，使用.png类型确保能被过滤条件匹配
				const tempFilePath = '/temp/test.png';
				
				uni.saveFile({
					tempFilePath: tempFilePath,
					success: (res) => {
						this.result = '保存文件成功，路径：' + res.savedFilePath;
						this.savedFilePath = res.savedFilePath;
						console.log('保存文件成功', res);
					},
					fail: (err) => {
						this.result = '保存文件失败：' + err.errMsg;
						console.error('保存文件失败', err);
					}
				});
			},
			
			// 从应用的本地文件系统中，查询并返回由 uni.saveFile或 uni.downloadFile等接口成功保存到本地的所有文件的清单。
			getSavedFileListDemo() {
				uni.getSavedFileList({
					success: (res) => {
						// 显示原始文件列表（调试信息）
						console.log('原始文件列表：', res.fileList);
						
						// 过滤只包括doc、docx和图片类型文件
						const filteredFiles = res.fileList.filter(file => {
							const lowerFilePath = file.filePath.toLowerCase();
							return lowerFilePath.endsWith('.doc') || 
								   lowerFilePath.endsWith('.docx') ||
								   lowerFilePath.endsWith('.jpg') ||
								   lowerFilePath.endsWith('.jpeg') ||
								   lowerFilePath.endsWith('.png') ||
								   lowerFilePath.endsWith('.gif') ||
								   lowerFilePath.endsWith('.bmp');
						});
						
						// 显示过滤后的文件列表（调试信息）
						console.log('过滤后的文件列表：', filteredFiles);
						
						this.result = '获取文件列表成功，共' + filteredFiles.length + '个文件（已过滤非doc/docx/图片类型）';
						this.fileList = filteredFiles;
						console.log('获取文件列表成功（已过滤）', filteredFiles);
					},
					fail: (err) => {
						this.result = '获取文件列表失败：' + err.errMsg;
						this.fileList = [];
						console.error('获取文件列表失败', err);
					}
				});
			},
			
			// 获取文件信息示例
			getSavedFileInfoDemo() {
				if (!this.savedFilePath) {
					this.result = '请先保存文件获取文件路径';
					return;
				}
				
				uni.getSavedFileInfo({
					filePath: this.savedFilePath,
					success: (res) => {
						this.result = '文件大小：' + res.size + '字节，保存时间：' + new Date(res.createTime * 1000).toLocaleString();
						console.log('获取文件信息成功', res);
					},
					fail: (err) => {
						this.result = '获取文件信息失败：' + err.errMsg;
						console.error('获取文件信息失败', err);
					}
				});
			},
			
			// 删除文件示例
			removeSavedFileDemo() {
				if (!this.savedFilePath) {
					this.result = '请先保存文件获取文件路径';
					return;
				}
				
				uni.removeSavedFile({
					filePath: this.savedFilePath,
					success: (res) => {
						this.result = '删除文件成功';
						this.savedFilePath = '';
						this.selectedIndex = -1; // 清除选中状态
						console.log('删除文件成功', res);
					},
					fail: (err) => {
						this.result = '删除文件失败：' + err.errMsg + '，文件路径：' + this.savedFilePath;
						console.error('删除文件失败', err);
					},
					complete: (res) => {
					  this.getSavedFileListDemo();
					}
				});
			},
			
			// 选择文件
			selectFile(file, index) {
				this.selectedIndex = index;
				this.savedFilePath = file.filePath;
				this.result = '已选中文件：' + file.filePath;
				console.log('已选中文件', file);
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

	.logo {
		height: 200rpx;
		width: 200rpx;
		margin-top: 50rpx;
		margin-left: auto;
		margin-right: auto;
		margin-bottom: 50rpx;
	}

	.text-area {
		display: flex;
		justify-content: center;
		margin-bottom: 50rpx;
	}

	.title {
		font-size: 36rpx;
		color: #8f8f94;
	}
	
	.api-list {
		width: 100%;
		max-width: 600rpx;
		display: flex;
		flex-direction: column;
		gap: 20rpx;
		margin-bottom: 50rpx;
	}
	
	.result {
		width: 100%;
		max-width: 600rpx;
		padding: 20rpx;
		background-color: #f5f5f5;
		border-radius: 10rpx;
	}
	
	.result-title {
		font-weight: bold;
		margin-bottom: 10rpx;
	}
	
	.result-content {
		font-size: 28rpx;
		color: #333;
	}
	
	.file-list {
		width: 100%;
		max-width: 600rpx;
		padding: 20rpx;
		background-color: #f9f9f9;
		border-radius: 10rpx;
		margin-top: 20rpx;
	}
	
	.file-list-title {
		font-weight: bold;
		display: block;
		margin-bottom: 15rpx;
	}
	
	.file-item {
		display: flex;
		align-items: flex-start;
		padding: 10rpx 0;
		border-bottom: 1rpx solid #eee;
		cursor: pointer;
		transition: background-color 0.2s;
	}
	
	.file-item:hover {
		background-color: #f0f0f0;
	}
	
	.file-item:last-child {
		border-bottom: none;
	}
	
	.file-item-selected {
		background-color: #e6f7ff;
		border-left: 4rpx solid #1890ff;
		padding-left: 16rpx;
	}
	
	.file-index {
		font-weight: bold;
		margin-right: 10rpx;
		color: #007aff;
	}
	
	.file-path {
		font-size: 26rpx;
		color: #555;
		word-break: break-all;
		flex: 1;
	}
</style>

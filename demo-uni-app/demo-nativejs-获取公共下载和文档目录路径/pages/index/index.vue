<template>
	<view class="content">
		<image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view>
		
		<view class="btn-area">
			<button type="primary" @click="getDownloadPath">获取公共下载目录</button>
			<button type="primary" @click="getDocumentPath" style="margin-top: 20rpx;">获取公共文档目录</button>
		</view>
		
		<view class="result-area">
			<view class="result-item">
				<text class="label">下载目录路径：</text>
				<text class="value">{{downloadPath || '未获取'}}</text>
			</view>
			<view class="result-item">
				<text class="label">文档目录路径：</text>
				<text class="value">{{documentPath || '未获取'}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'Native.js目录获取演示',
				downloadPath: '',
				documentPath: ''
			}
		},
		onLoad() {

		},
		methods: {
			getDownloadPath() {
				if (uni.getSystemInfoSync().platform !== 'android') {
					uni.showToast({
						title: '此功能仅支持Android平台',
						icon: 'none'
					});
					return;
				}
				
				try {
					// 使用native.js获取Android公共下载目录
					const Context = plus.android.importClass('android.content.Context');
					const Environment = plus.android.importClass('android.os.Environment');
					const Build = plus.android.importClass('android.os.Build');
					const activity = plus.android.runtimeMainActivity();
					
					const DIRECTORY_DOWNLOADS = 'Download';
					
					let downloadDir;
					// 检查Android版本，Android Q(API 29)及以上版本需要使用新的API
					if (Build.VERSION.SDK_INT >= 29) {
						// Android Q及以上版本获取应用私有下载目录
						downloadDir = activity.getExternalFilesDir(DIRECTORY_DOWNLOADS);
					} else {
						// Android Q以下版本获取公共下载目录
						downloadDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
					}
					
					if (downloadDir) {
						// 使用native.js的方式获取路径，兼容不同的Android版本
						this.downloadPath = plus.android.invoke(downloadDir, 'getAbsolutePath');
						uni.showToast({
							title: '获取成功',
							icon: 'success'
						});
					} else {
						throw new Error('无法获取下载目录');
					}
				} catch (e) {
					uni.showToast({
						title: '获取失败：' + e.message,
						icon: 'none'
					});
					console.error('获取下载目录失败：', e);
				}
			},
			
			getDocumentPath() {
				if (uni.getSystemInfoSync().platform !== 'android') {
					uni.showToast({
						title: '此功能仅支持Android平台',
						icon: 'none'
					});
					return;
				}
				
				try {
					// 使用native.js获取Android公共文档目录
					const Context = plus.android.importClass('android.content.Context');
					const Environment = plus.android.importClass('android.os.Environment');
					const Build = plus.android.importClass('android.os.Build');
					const activity = plus.android.runtimeMainActivity();
					
					const DIRECTORY_DOCUMENTS = 'Document';
					
					let documentDir;
					// 检查Android版本，Android Q(API 29)及以上版本需要使用新的API
					if (Build.VERSION.SDK_INT >= 29) {
						// Android Q及以上版本获取应用私有文档目录
						documentDir = activity.getExternalFilesDir(DIRECTORY_DOCUMENTS);
					} else {
						// Android Q以下版本获取公共文档目录
						documentDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS);
					}
					
					if (documentDir) {
						// 使用native.js的方式获取路径，兼容不同的Android版本
						this.documentPath = plus.android.invoke(documentDir, 'getAbsolutePath');
						uni.showToast({
							title: '获取成功',
							icon: 'success'
						});
					} else {
						throw new Error('无法获取文档目录');
					}
				} catch (e) {
					uni.showToast({
						title: '获取失败：' + e.message,
						icon: 'none'
					});
					console.error('获取文档目录失败：', e);
				}
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
		margin-top: 100rpx;
		margin-left: auto;
		margin-right: auto;
		margin-bottom: 50rpx;
	}

	.text-area {
		display: flex;
		justify-content: center;
		margin-bottom: 60rpx;
	}

	.title {
		font-size: 36rpx;
		color: #8f8f94;
	}
	
	.btn-area {
		width: 100%;
		max-width: 600rpx;
		padding: 0 40rpx;
		margin-bottom: 60rpx;
	}
	
	.result-area {
		width: 100%;
		max-width: 600rpx;
		padding: 30rpx;
		background-color: #f5f5f5;
		border-radius: 10rpx;
	}
	
	.result-item {
		margin-bottom: 20rpx;
		line-height: 40rpx;
	}
	
	.result-item:last-child {
		margin-bottom: 0;
	}
	
	.label {
		font-weight: bold;
		color: #333;
	}
	
	.value {
		color: #007aff;
		word-break: break-all;
		font-size: 28rpx;
	}
</style>

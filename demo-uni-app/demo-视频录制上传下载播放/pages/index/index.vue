<template>
	<view class="content">
		<text class="title">视频录制上传下载播放演示</text>
		
		<!-- 视频录制区域 -->
		<view class="section">
			<text class="section-title">视频录制</text>
			<button class="btn-primary" @click="recordVideo">录制视频</button>
			<button class="btn-secondary" @click="chooseVideoFromAlbum">从相册选择视频</button>
		</view>
		
		<!-- 视频播放区域 -->
		<view class="section" v-if="videoPath">
			<text class="section-title">视频预览</text>
			<video class="video-player" :src="videoPath" controls></video>
		</view>
		
		<!-- 视频上传区域 -->
		<view class="section" v-if="videoPath">
			<text class="section-title">视频上传</text>
			<button class="btn-success" @click="uploadVideo" :disabled="uploading">
				{{ uploading ? '上传中...' : '上传视频' }}
			</button>
		</view>
		
		<!-- 已上传视频列表 -->
		<view class="section" v-if="uploadedVideos.length > 0">
			<text class="section-title">已上传视频</text>
			<view class="video-list">
				<view class="video-item" v-for="(video, index) in uploadedVideos" :key="index">
					<text class="video-name">{{ video.filename }}</text>
					<view class="video-actions">
						<button class="btn-small btn-play" @click="playUploadedVideo(video.url)">播放</button>
						<button class="btn-small btn-download" @click="downloadVideo(video.url, video.filename)">
							下载
						</button>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: '视频演示',
				videoPath: '',
				uploading: false,
				uploadedVideos: [],
				// 模拟API地址，实际使用时需要替换为真实的服务器地址
				apiBaseUrl: 'http://192.168.3.27:8080'
			}
		},
		onLoad() {

		},
		methods: {
			// 录制视频
			recordVideo() {
				uni.chooseVideo({
					count: 1,
					sourceType: ['camera'],
					maxDuration: 60,
					success: (res) => {
						this.videoPath = res.tempFilePath;
						console.log('录制视频成功:', res);
					},
					fail: (err) => {
						console.error('录制视频失败:', err);
						uni.showToast({
							title: '录制失败',
							icon: 'none'
						});
					}
				});
			},
			
			// 从相册选择视频
			chooseVideoFromAlbum() {
				uni.chooseVideo({
					count: 1,
					sourceType: ['album'],
					success: (res) => {
						this.videoPath = res.tempFilePath;
						console.log('选择视频成功:', res);
					},
					fail: (err) => {
						console.error('选择视频失败:', err);
						uni.showToast({
							title: '选择失败',
							icon: 'none'
						});
					}
				});
			},
			
			// 上传视频
			uploadVideo() {
				if (!this.videoPath) {
					uni.showToast({
						title: '请先选择或录制视频',
						icon: 'none'
					});
					return;
				}
				
				this.uploading = true;
				
				// 构造文件名
				const timestamp = Date.now();
				const filename = `video_${timestamp}.mp4`;
				
				uni.uploadFile({
					url: this.apiBaseUrl + '/api/v1/postWithFileUpload',
					filePath: this.videoPath,
					name: 'files',
					formData: {
						filename: filename
					},
					success: (res) => {
						console.log('上传成功:', res);
						try {
							const data = JSON.parse(res.data);
							this.uploadedVideos.push({
								filename: filename,
								url: this.apiBaseUrl + '/api/v1/' + data.data[0]
							});
							uni.showToast({
								title: '上传成功',
								icon: 'success'
							});
						} catch (e) {
							console.error('解析上传结果失败:', e);
							uni.showToast({
								title: '上传成功但解析结果失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('上传失败:', err);
						uni.showToast({
							title: '上传失败',
							icon: 'none'
						});
					},
					complete: () => {
						this.uploading = false;
					}
				});
			},
			
			// 播放已上传的视频
			playUploadedVideo(url) {
				uni.navigateTo({
					url: `/pages/video-player/video-player?url=${encodeURIComponent(url)}`
				});
			},
			
			// 下载视频
			downloadVideo(url, filename) {
				uni.showLoading({
					title: '下载中...'
				});
				
				uni.downloadFile({
					url: url,
					success: (res) => {
						if (res.statusCode === 200) {
							// 保存视频到相册
							uni.saveVideoToPhotosAlbum({
								filePath: res.tempFilePath,
								success: () => {
									uni.hideLoading();
									uni.showToast({
										title: '下载成功并保存到相册',
										icon: 'success'
									});
								},
								fail: (err) => {
									uni.hideLoading();
									console.error('保存视频失败:', err);
									uni.showToast({
										title: '保存视频失败',
										icon: 'none'
									});
								}
							});
						} else {
							uni.hideLoading();
							uni.showToast({
								title: '下载失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						uni.hideLoading();
						console.error('下载失败:', err);
						uni.showToast({
							title: '下载失败',
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
		padding: 20rpx;
	}

	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 40rpx;
	}

	.section {
		width: 100%;
		max-width: 600rpx;
		margin-bottom: 40rpx;
		padding: 20rpx;
		background-color: #f5f5f5;
		border-radius: 10rpx;
	}

	.section-title {
		display: block;
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}

	.btn-primary, .btn-secondary, .btn-success {
		width: 100%;
		padding: 20rpx;
		margin-bottom: 15rpx;
		border: none;
		border-radius: 5rpx;
		font-size: 28rpx;
		color: #fff;
	}

	.btn-primary {
		background-color: #007aff;
	}

	.btn-secondary {
		background-color: #5ac8fa;
	}

	.btn-success {
		background-color: #4cd964;
	}

	.video-player {
		width: 100%;
		height: 300rpx;
		margin-top: 20rpx;
		border-radius: 5rpx;
	}

	.video-list {
		margin-top: 20rpx;
	}

	.video-item {
		padding: 15rpx;
		background-color: #fff;
		border-radius: 5rpx;
		margin-bottom: 15rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.video-name {
		font-size: 24rpx;
		color: #333;
		flex: 1;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.video-actions {
		display: flex;
		gap: 10rpx;
	}

	.btn-small {
		padding: 10rpx 20rpx;
		border: none;
		border-radius: 5rpx;
		font-size: 20rpx;
		color: #fff;
	}

	.btn-play {
		background-color: #007aff;
	}

	.btn-download {
		background-color: #ff9500;
	}
</style>

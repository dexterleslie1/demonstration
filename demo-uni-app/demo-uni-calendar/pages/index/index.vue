<template>
	<view class="container">
		<view class="section">
			<view class="section-title">插入模式（insert=true）</view>
			<view class="section-desc">日历直接嵌入页面中，选择日期触发 change 事件</view>
			<uni-calendar 
				:insert="true"
				:lunar="true" 
				:startDate="startDate"
				:endDate="endDate"
				@change="onChange"
			/>
		</view>

		<view class="section">
			<view class="section-title">弹出模式（insert=false）</view>
			<view class="section-desc">通过方法打开日历弹窗，选择日期触发 confirm 事件</view>
			<button class="open-btn" type="primary" @click="openCalendar">打开日历</button>
			<uni-calendar 
				ref="calendar"
				:insert="false"
				:lunar="true"
				:range="true"
				@confirm="onConfirm"
				@close="onClose"
			/>
		</view>

		<view class="section">
			<view class="section-title">选择结果</view>
			<view class="result-box">
				<text v-if="selectedDate">已选择日期：{{ selectedDate }}</text>
				<text v-else class="placeholder">请选择日期</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				startDate: '2024-01-01',
				endDate: '2024-12-31',
				selectedDate: ''
			}
		},
		onLoad() {
			// 设置默认日期范围（当前日期前后各3个月）
			const today = new Date()
			const start = new Date(today.getFullYear(), today.getMonth() - 3, 1)
			const end = new Date(today.getFullYear(), today.getMonth() + 3, 0)
			
			this.startDate = this.formatDate(start)
			this.endDate = this.formatDate(end)
		},
		methods: {
			// 格式化日期为 YYYY-MM-DD
			formatDate(date) {
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				return `${year}-${month}-${day}`
			},
			// 打开日历弹窗
			openCalendar() {
				this.$refs.calendar.open()
			},
			// 插入模式选择日期
			onChange(e) {
				console.log('change 事件:', e)
				this.selectedDate = e.fulldate || e.fullDate
				uni.showToast({
					title: `选择了：${this.selectedDate}`,
					icon: 'none'
				})
			},
			// 弹出模式确认选择
			onConfirm(e) {
				console.log('confirm 事件:', e)
				if (e.range && e.range.before && e.range.after) {
					this.selectedDate = `${e.range.before} 至 ${e.range.after}`
				} else {
					this.selectedDate = e.fulldate || e.fullDate
				}
				uni.showToast({
					title: '选择成功',
					icon: 'success'
				})
			},
			// 关闭弹窗
			onClose() {
				console.log('日历弹窗关闭')
			}
		}
	}
</script>

<style>
	.container {
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.section {
		background-color: #ffffff;
		border-radius: 10rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}

	.section-desc {
		font-size: 26rpx;
		color: #999;
		margin-bottom: 20rpx;
	}

	.open-btn {
		width: 100%;
		margin-top: 20rpx;
	}

	.result-box {
		padding: 20rpx;
		background-color: #f8f8f8;
		border-radius: 8rpx;
		min-height: 60rpx;
		display: flex;
		align-items: center;
	}

	.result-box text {
		font-size: 28rpx;
		color: #333;
	}

	.placeholder {
		color: #999;
	}
</style>

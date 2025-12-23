<template>
	<view class="container">
		<view class="date-display" @click="show = true">
			<text v-if="dateRange">{{ dateRange }}</text>
			<text v-else class="placeholder">请选择日期范围</text>
		</view>
		<u-calendar :show="show" :mode="mode" @confirm="confirm" @close="()=>{show = false}"></u-calendar>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				show: false,
				mode: 'range',
				dateRange: ''
			}
		},
		methods: {
			confirm(e) {
				console.log(e);
				// 格式化日期为 yyyy-MM-dd
				const startDate = this.formatDate(e[0]);
				const endDate = this.formatDate(e[1]);
				this.dateRange = `${startDate} ~ ${endDate}`;
				this.show = false;
			},
			formatDate(timestamp) {
				const date = new Date(timestamp);
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				return `${year}-${month}-${day}`;
			}
		}
	}
</script>

<style scoped>
	.container {
		padding: 20rpx;
	}
	
	.date-display {
		padding: 24rpx 30rpx;
		background-color: #fff;
		border-radius: 8rpx;
		border: 1rpx solid #e4e7ed;
	}
	
	.date-display text {
		font-size: 28rpx;
		color: #303133;
	}
	
	.date-display .placeholder {
		color: #909399;
	}
</style>

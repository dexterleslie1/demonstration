<template>
	<view class="page">
		<view class="section">
			<text class="section-title">日期选择</text>
			<view class="example-body">
				<uni-datetime-picker
					type="date"
					v-model="dateValue"
					@change="onDateChange"
				/>
			</view>
			<text class="result-text">选择结果：{{ dateValue || '未选择' }}</text>
		</view>

		<view class="section">
			<text class="section-title">日期时间选择</text>
			<view class="example-body">
				<uni-datetime-picker
					type="datetime"
					v-model="datetimeValue"
					@change="onDatetimeChange"
				/>
			</view>
			<text class="result-text">选择结果：{{ datetimeValue || '未选择' }}</text>
		</view>

		<view class="section">
			<text class="section-title">日期范围选择</text>
			<view class="example-body">
				<uni-datetime-picker
					type="daterange"
					v-model="dateRangeValue"
					@change="onDateRangeChange"
				/>
			</view>
			<text class="result-text">选择结果：{{ dateRangeValue.length > 0 ? dateRangeValue.join(' 至 ') : '未选择' }}</text>
		</view>

		<view class="section">
			<text class="section-title">日期时间范围选择</text>
			<view class="example-body">
				<uni-datetime-picker
					type="datetimerange"
					v-model="datetimeRangeValue"
					rangeSeparator="至"
					@change="onDatetimeRangeChange"
				/>
			</view>
			<text class="result-text">选择结果：{{ datetimeRangeValue.length > 0 ? datetimeRangeValue.join(' 至 ') : '未选择' }}</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				dateValue: '',
				datetimeValue: '',
				dateRangeValue: [],
				datetimeRangeValue: []
			}
		},
		methods: {
			onDateChange(e) {
				console.log('日期选择:', e);
			},
			onDatetimeChange(e) {
				console.log('日期时间选择:', e);
			},
			onDateRangeChange(e) {
				console.log('日期范围选择:', e);
			},
			onDatetimeRangeChange(e) {
				console.log('日期时间范围选择:', e);
			}
		}
	}
</script>

<style>
	.page {
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.section {
		background-color: #fff;
		border-radius: 10rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 20rpx;
	}

	.example-body {
		margin-bottom: 20rpx;
	}

	.result-text {
		font-size: 28rpx;
		color: #666;
		word-break: break-all;
	}
</style>

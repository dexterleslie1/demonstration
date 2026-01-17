<template>
	<view class="content">
		<view class="demo-section">
			<view class="demo-title">基本使用（日期时间）</view>
			<u-button @click="showDatetime = true" type="primary">选择日期时间</u-button>
			<view class="result-text">选择的值：{{ datetimeValue ? formatTime(datetimeValue) : '未选择' }}</view>
		</view>

		<view class="demo-section">
			<view class="demo-title">年月日选择</view>
			<u-button @click="showDate = true" type="primary">选择日期</u-button>
			<view class="result-text">选择的值：{{ dateValue ? formatDate(dateValue) : '未选择' }}</view>
		</view>

		<view class="demo-section">
			<view class="demo-title">自定义格式化</view>
			<u-button @click="showFormatted = true" type="primary">选择日期（格式化）</u-button>
			<view class="result-text">选择的值：{{ formattedValue ? formatDate(formattedValue) : '未选择' }}</view>
		</view>

		<view class="demo-section">
			<view class="demo-title">日期范围选择</view>
			<view class="range-buttons">
				<u-button @click="showStartDate = true" type="primary" size="small">选择开始日期</u-button>
				<u-button @click="showEndDate = true" type="success" size="small">选择结束日期</u-button>
			</view>
			<view class="result-text">开始日期：{{ startDateValue ? formatDate(startDateValue) : '未选择' }}</view>
			<view class="result-text">结束日期：{{ endDateValue ? formatDate(endDateValue) : '未选择' }}</view>
			<view class="result-text" v-if="startDateValue && endDateValue">
				范围：{{ formatDate(startDateValue) }} 至 {{ formatDate(endDateValue) }}
			</view>
		</view>

		<!-- 日期时间选择器 -->
		<!-- closeOnClickOverlay="true"：允许点击遮罩层关闭选择器 -->
		<u-datetime-picker
			:show="showDatetime"
			v-model="datetimeValue"
			mode="datetime"
			:closeOnClickOverlay="true"
			@confirm="onDatetimeConfirm"
			@cancel="showDatetime = false"
			@close="showDatetime = false"
		></u-datetime-picker>

		<!-- 日期选择器 -->
		<u-datetime-picker
			:show="showDate"
			v-model="dateValue"
			mode="date"
			:closeOnClickOverlay="true"
			@confirm="onDateConfirm"
			@cancel="showDate = false"
			@close="showDate = false"
		></u-datetime-picker>

		<!-- 格式化选择器 -->
		<u-datetime-picker
			ref="datetimePicker"
			:show="showFormatted"
			v-model="formattedValue"
			mode="date"
			:formatter="formatter"
			:closeOnClickOverlay="true"
			@confirm="onFormattedConfirm"
			@cancel="showFormatted = false"
			@close="showFormatted = false"
		></u-datetime-picker>

		<!-- 开始日期选择器 -->
		<u-datetime-picker
			:show="showStartDate"
			v-model="startDateValue"
			mode="date"
			:maxDate="endDateValue || undefined"
			:closeOnClickOverlay="true"
			@confirm="onStartDateConfirm"
			@cancel="showStartDate = false"
			@close="showStartDate = false"
		></u-datetime-picker>

		<!-- 结束日期选择器 -->
		<u-datetime-picker
			:show="showEndDate"
			v-model="endDateValue"
			mode="date"
			:minDate="startDateValue || undefined"
			:closeOnClickOverlay="true"
			@confirm="onEndDateConfirm"
			@cancel="showEndDate = false"
			@close="showEndDate = false"
		></u-datetime-picker>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				showDatetime: false,
				datetimeValue: Number(new Date()),
				showDate: false,
				dateValue: Number(new Date()),
				showFormatted: false,
				formattedValue: Number(new Date()),
				showStartDate: false,
				startDateValue: null,
				showEndDate: false,
				endDateValue: null
			}
		},
		onReady() {
			// 微信小程序需要用此写法
			// #ifdef MP-WEIXIN
			if (this.$refs.datetimePicker) {
				this.$refs.datetimePicker.setFormatter(this.formatter)
			}
			// #endif
		},
		methods: {
			onDatetimeConfirm(e) {
				this.datetimeValue = e.value
				this.showDatetime = false
			},
			onDateConfirm(e) {
				this.dateValue = e.value
				this.showDate = false
			},
			onFormattedConfirm(e) {
				this.formattedValue = e.value
				this.showFormatted = false
			},
			onStartDateConfirm(e) {
				this.startDateValue = e.value
				this.showStartDate = false
				// 如果开始日期大于结束日期，清空结束日期
				if (this.endDateValue && this.startDateValue > this.endDateValue) {
					this.endDateValue = null
				}
			},
			onEndDateConfirm(e) {
				this.endDateValue = e.value
				this.showEndDate = false
			},
			formatTime(timestamp) {
				const date = new Date(timestamp)
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				const hour = String(date.getHours()).padStart(2, '0')
				const minute = String(date.getMinutes()).padStart(2, '0')
				const second = String(date.getSeconds()).padStart(2, '0')
				return `${year}-${month}-${day} ${hour}:${minute}:${second}`
			},
			formatDate(timestamp) {
				const date = new Date(timestamp)
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				return `${year}-${month}-${day}`
			},
			formatter(type, value) {
				if (type === 'year') {
					return `${value}年`
				}
				if (type === 'month') {
					return `${value}月`
				}
				if (type === 'day') {
					return `${value}日`
				}
				return value
			}
		}
	}
</script>

<style>
	.content {
		padding: 40rpx;
	}

	.demo-section {
		margin-bottom: 60rpx;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.demo-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 30rpx;
	}

	.result-text {
		margin-top: 20rpx;
		font-size: 28rpx;
		color: #666;
	}

	.range-buttons {
		display: flex;
		gap: 20rpx;
		margin-bottom: 20rpx;
	}
</style>

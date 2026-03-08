<template>
	<view class="content">
		<image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view>
		<button class="btn" type="primary" @click="reportLog">上报业务日志</button>
		<button class="btn" type="default" @click="reportLogImmediate">立即上报一条</button>
	</view>
</template>

<script>
	import { reportBusinessLog, reportBusinessLogImmediate } from '@/utils/sls.js'

	export default {
		data() {
			return {
				title: 'Hello'
			}
		},
		onLoad() {
			reportBusinessLog('pages/index', '页面加载')
		},
		methods: {
			reportLog() {
				reportBusinessLog('pages/index', '用户点击上报业务日志', { action: 'click_report' })
			},
			reportLogImmediate() {
				reportBusinessLogImmediate('pages/index', '用户点击立即上报', { action: 'click_immediate' })
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.logo {
		height: 200rpx;
		width: 200rpx;
		margin-top: 200rpx;
		margin-left: auto;
		margin-right: auto;
		margin-bottom: 50rpx;
	}

	.text-area {
		display: flex;
		justify-content: center;
	}

	.title {
		font-size: 36rpx;
		color: #8f8f94;
	}

	.btn {
		margin-top: 24rpx;
		width: 400rpx;
	}
</style>

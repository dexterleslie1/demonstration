<template>
	<view class="content">
		<button type="default" @click="handleClickSuccess()">uni.showToast success</button>
		<button type="default" @click="handleClickError()">uni.showToast error</button>
		<button type="default" @click="handleClickFail()">uni.showToast fail</button>
		<button type="default" @click="handleClickException()">uni.showToast exception</button>
		<button type="default" @click="handleClickLoading()">uni.showToast loading</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'Hello'
			}
		},
		onLoad() {

		},
		methods: {
			handleClickSuccess() {
				uni.showToast({
					title: '提示',
					icon: 'success',
					mask: true,
				})
			},
			handleClickError() {
				uni.showToast({
					title: '提示',
					icon: 'error',
					mask: true,
				})
			},
			handleClickFail() {
				uni.showToast({
					title: '提示',
					icon: 'fail',
					mask: true,
				})
			},
			handleClickException() {
				uni.showToast({
					title: '提示',
					icon: 'exception',
					mask: true,
				})
			},
			handleClickLoading() {
				uni.showToast({
					title: '提示',
					icon: 'loading',
					mask: true,
				})
			},
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
</style>

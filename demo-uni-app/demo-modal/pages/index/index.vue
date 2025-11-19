<template>
	<view class="content">
		<button type="default" @click="handleClick()">un.showModal</button>
		<button type="default" @click="handleClickWithoutCancel()">un.showModal没有cancel按钮</button>
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
			handleClick() {
				uni.showModal({
					title: '提示',
					content: '提示内容',
				})
			},
			handleClickWithoutCancel() {
				uni.showModal({
					title: '提示',
					content: '提示内容',
					showCancel: false,
				})
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
</style>

<template>
	<view ref="HF" id="HF" class="" @click="onclick" @touchstart="ontouchstart" @touchmove="ontouchmove"
		:style="{top:startY,left:startX}">
		HF调试器
	</view>
</template>

<script>
	let onType = true
	export default {
		name: 'BntBox',
		data() {
			return {
				startY: '90%',
				startX: '60%',
				start: {},
			}
		},
		mounted() {

		},
		methods: {
			onclick(e) {
				uni.$emit("HFshow")
			},
			ontouchmove(touch) {
				touch = touch.touches[0]
				let page = this.start
				if ((touch.pageY - 20 > 0 && touch.pageX - 50 > 0) &&
					(Math.abs(touch.pageX - page.pageX) > 20 ||
						Math.abs(touch.pageY - page.pageY) > 20)) {
					this.startY = (touch.pageY + uni.upx2px(60) / 2) + 'px'
					this.startX = (touch.pageX - uni.upx2px(180) / 2) + 'px'
					onType = true
				} else {
					onType = false
				}
			},
			ontouchstart(e) {
				this.start = e.touches[0]
			},
		}
	}
</script>

<style>
	#HF {
		text-align: center;
		position: fixed;
		border-radius: 10rpx;
		background: #FF4A46;
		color: #FFFFFF;
		width: 180rpx;
		height: 60rpx;
		line-height: 60rpx;
		z-index: 999;
		left: 60%;
	}
</style>

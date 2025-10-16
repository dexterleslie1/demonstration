<template>
	<view class="container">
		<view>A</view>
		<view>B</view>
		<view>C</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
			}
		},
		onLoad() {

		},
		methods: {

		}
	}
</script>

<style>
	.container {
		display: flex;
		flex-direction: row;
		justify-content: space-around;
	}
	
	.container view {
		width: 100px;
		line-height: 100px;
		text-align: center;
	}
	
	.container view:nth-child(1) {
		background-color: red;
	}
	
	.container view:nth-child(2){
		background-color: green;
	}
	
	.container view:nth-child(3) {
		background-color: greenyellow;
	}
</style>

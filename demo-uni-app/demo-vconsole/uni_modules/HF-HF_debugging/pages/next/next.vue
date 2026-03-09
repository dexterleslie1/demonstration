<template>
	<view>
		<HFBox></HFBox>
	</view>
</template>

<script>
	import HFBox from './../../components/HFBox.vue'
	export default {
		components:{
			HFBox
		},
		data() {
			return {
				tets:'测试'
			}
		},
		onShow() {
			// ||getCurrentPages().lengt<=1
			/* uni.navigateBack({
				delta: 1
			}) */
		},
		onBackPress(){
			uni.$emit("viewShow")
		},
		methods: {
			
		},
		onHide() {
			uni.$emit("viewShow")
		}
	}
</script>

<style>

</style>

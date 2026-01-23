<template>
	<view class="container">
		<view class="section">
			<view class="section-title">基本使用</view>
			<u-radio-group
				v-model="radioValue1"
				placement="column"
				@change="groupChange"
			>
				<u-radio
					:customStyle="{marginBottom: '8px'}"
					v-for="(item, index) in radioList1"
					:key="index"
					:label="item.label"
					:name="item.value"
					@change="radioChange"
				>
				</u-radio>
			</u-radio-group>
			<view class="result">当前选中值：{{ radioValue1 }}，显示文本：{{ selectedLabel1 }}</view>
		</view>

		<view class="section">
			<view class="section-title">横向排列</view>
			<u-radio-group
				v-model="radioValue2"
				placement="row"
				@change="groupChange"
			>
				<u-radio
					v-for="(item, index) in radioList2"
					:key="index"
					:label="item.label"
					:name="item.value"
				>
				</u-radio>
			</u-radio-group>
			<view class="result">当前选中值：{{ radioValue2 }}，显示文本：{{ selectedLabel2 }}</view>
		</view>

		<view class="section">
			<view class="section-title">自定义形状和颜色</view>
			<u-radio-group
				v-model="radioValue3"
				placement="column"
			>
				<u-radio
					:customStyle="{marginBottom: '8px'}"
					shape="circle"
					activeColor="#ff6b6b"
					label="圆形 - 红色"
					name="circle-red"
				></u-radio>
				<u-radio
					:customStyle="{marginBottom: '8px'}"
					shape="square"
					activeColor="#4ecdc4"
					label="方形 - 青色"
					name="square-cyan"
				></u-radio>
			</u-radio-group>
			<view class="result">当前选中值：{{ radioValue3 }}，显示文本：{{ selectedLabel3 }}</view>
		</view>

		<view class="section">
			<view class="section-title">禁用选项</view>
			<u-radio-group
				v-model="radioValue4"
				placement="column"
			>
				<u-radio
					:customStyle="{marginBottom: '8px'}"
					label="正常选项"
					name="normal"
				></u-radio>
				<u-radio
					:customStyle="{marginBottom: '8px'}"
					:disabled="true"
					label="禁用选项"
					name="disabled"
				></u-radio>
			</u-radio-group>
			<view class="result">当前选中值：{{ radioValue4 }}，显示文本：{{ selectedLabel4 }}</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// 基本使用数据 - name 是值，label 是显示文本
				radioList1: [
					{ value: 'apple', label: '苹果' },
					{ value: 'banana', label: '香蕉' },
					{ value: 'orange', label: '橙子' },
					{ value: 'durian', label: '榴莲' }
				],
				radioValue1: 'apple',
				
				// 横向排列数据
				radioList2: [
					{ value: 'optionA', label: '选项A' },
					{ value: 'optionB', label: '选项B' },
					{ value: 'optionC', label: '选项C' }
				],
				radioValue2: 'optionA',
				
				// 自定义形状和颜色
				radioValue3: '',
				
				// 禁用选项
				radioValue4: 'normal'
			}
		},
		computed: {
			// 根据选中的值获取对应的 label
			selectedLabel1() {
				const item = this.radioList1.find(item => item.value === this.radioValue1);
				return item ? item.label : '';
			},
			selectedLabel2() {
				const item = this.radioList2.find(item => item.value === this.radioValue2);
				return item ? item.label : '';
			},
			selectedLabel3() {
				const labels = {
					'circle-red': '圆形 - 红色',
					'square-cyan': '方形 - 青色'
				};
				return labels[this.radioValue3] || '';
			},
			selectedLabel4() {
				const labels = {
					'normal': '正常选项',
					'disabled': '禁用选项'
				};
				return labels[this.radioValue4] || '';
			}
		},
		methods: {
			groupChange(value) {
				console.log('groupChange', value);
			},
			radioChange(value) {
				console.log('radioChange', value);
			}
		}
	}
</script>

<style scoped>
	.container {
		padding: 30rpx;
		background-color: #f8f8f8;
		min-height: 100vh;
	}

	.section {
		background-color: #ffffff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #303133;
		margin-bottom: 30rpx;
	}

	.result {
		margin-top: 20rpx;
		padding: 20rpx;
		background-color: #f0f0f0;
		border-radius: 8rpx;
		font-size: 28rpx;
		color: #606266;
	}
</style>

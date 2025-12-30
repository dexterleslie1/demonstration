<template>
  <div id="app">
    <van-steps direction="vertical" :active="3">
      <van-step class="step-approved">
        <div class="step-title">
          <span class="title-text">发起人</span>
          <span class="step-time">06-04 00:04</span>
        </div>
        <div class="step-detail">
          <div class="applicant">quuyee</div>
          <div class="label">提交审批</div>
        </div>
      </van-step>

      <van-step class="step-rejected">
        <div class="step-title">
          <span class="title-text">拒绝审批节点</span>
          <span class="step-time">06-04 07:12</span>
        </div>
        <div class="step-detail">
          <div class="applicant">zhangsan</div>
          <div class="label">拒绝</div>
        </div>
      </van-step>

      <van-step class="step-approved">
        <div class="step-title">
          <span class="title-text">审批节点2</span>
          <span class="step-time">06-04 09:16</span>
        </div>
        <div class="step-detail">
          <div class="applicant">lisi</div>
          <div class="label">审批意见</div>
        </div>
      </van-step>
      
      <van-step class="current-approver-step">
        <div class="step-title">
          <span class="title-text">当前审批人</span>
          <span class="step-time">06-16 11:08</span>
        </div>
        <div class="step-detail">
          <div class="approver-list">
            <div class="approver">admin@qq.com</div>
            <div class="approver">tessali</div>
            <div class="approver">adaxie</div>
            <div class="approver">lucy</div>
            <div class="approver">andy12</div>
          </div>
        </div>
      </van-step>
      
    </van-steps>
  </div>
</template>

<script>
import { Step, Steps } from 'vant'

export default {
  name: 'App',
  components: {
    [Step.name]: Step,
    [Steps.name]: Steps
  },
  mounted() {
    // 处理已通过的步骤和拒绝的步骤
    this.$nextTick(() => {
      // 确保当前审批人步骤显示为空心圆（保持不变）
      const step = this.$el.querySelector('.current-approver-step')
      if (step) {
        // 查找所有可能的图标元素
        const selectors = [
          '.van-step__circle',
          '[class*="circle"]',
          '[class*="icon"]',
          '.van-step__icon'
        ]
        
        let circleElement = null
        for (const selector of selectors) {
          circleElement = step.querySelector(selector)
          if (circleElement) break
        }
        
        if (circleElement) {
          // 设置为空心圆样式
          circleElement.style.backgroundColor = 'white'
          circleElement.style.border = '2px solid #30968a'
          circleElement.style.width = '16px'
          circleElement.style.height = '16px'
          circleElement.style.boxSizing = 'border-box'
          circleElement.style.borderRadius = '50%'
          
          // 移除内部内容
          const children = circleElement.children
          for (let i = 0; i < children.length; i++) {
            children[i].style.display = 'none'
          }
        }
      }
    })
  }
}
</script>

<style>
.step-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  font-size: 14px;
  color: #323233;
  margin-bottom: 8px;
  font-weight: 500;
}

.title-text {
  flex: 1;
}

.step-time {
  font-size: 12px;
  color: #969799;
  text-align: right;
  line-height: 1.5;
  font-weight: normal;
  white-space: nowrap;
  margin-left: 20px;
}

.step-detail {
  font-size: 13px;
  color: #969799;
  margin-bottom: 16px;
}

.applicant {
  color: #323233;
  margin-bottom: 4px;
}

.label {
  color: #07c160;
  font-size: 12px;
}

.approver-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.approver {
  color: #646566;
  font-size: 13px;
}



/* 修改连接线条颜色 */
.van-steps--vertical .van-step__line,
.van-steps .van-step__line {
  background-color: #30968a !important;
}

.van-steps--vertical .van-step__line--active,
.van-steps .van-step__line--active {
  background-color: #30968a !important;
}

/* 已通过步骤显示对勾图标 */
.van-steps .van-step.step-approved .van-step__circle {
  background-color: #30968a !important;
  border: none !important;
  width: 16px !important;
  height: 16px !important;
  border-radius: 50% !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  position: relative !important;
}

.van-steps .van-step.step-approved .van-step__circle::after {
  content: '✓';
  color: #fff;
  font-size: 11px;
  font-weight: bold;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  line-height: 1;
  margin: 0;
  padding: 0;
}

/* 拒绝步骤显示X图标 */
.van-steps .van-step.step-rejected .van-step__circle {
  background-color: #ee0a24 !important;
  border: none !important;
  width: 16px !important;
  height: 16px !important;
  border-radius: 50% !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  position: relative !important;
}

.van-steps .van-step.step-rejected .van-step__circle::after {
  content: '×';
  color: #fff;
  font-size: 14px;
  font-weight: bold;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  line-height: 1;
  margin: 0;
  padding: 0;
}

/* 当前审批人步骤使用空心圆图标 */
/* 方法1: 通过类名选择器 */
.van-steps .van-step.current-approver-step .van-step__circle {
  background-color: transparent !important;
  border: 2px solid #30968a !important;
  width: 16px !important;
  height: 16px !important;
  box-sizing: border-box !important;
}

.van-steps .van-step.current-approver-step .van-step__circle::after {
  display: none !important;
  content: none !important;
}

/* 方法2: 使用 nth-child 选择器定位第4个步骤（当前审批人） */
.van-steps .van-step:nth-child(4) .van-step__circle {
  background-color: transparent !important;
  border: 2px solid #30968a !important;
  width: 16px !important;
  height: 16px !important;
  box-sizing: border-box !important;
}

.van-steps .van-step:nth-child(4) .van-step__circle::after {
  display: none !important;
  content: none !important;
}
</style>

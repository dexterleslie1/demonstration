<template>
  <div id="app">
    <van-steps direction="vertical" :active="3">
      <van-step>
        <div class="step-title">
          <span class="title-text">发起人</span>
          <span class="step-time">06-04 00:04</span>
        </div>
        <div class="step-detail">
          <div class="applicant">quuyee</div>
          <div class="label">提交审批</div>
        </div>
      </van-step>

      <van-step>
        <div class="step-title">
          <span class="title-text">审批节点1</span>
          <span class="step-time">06-04 07:12</span>
        </div>
        <div class="step-detail">
          <div class="applicant">zhangsan</div>
          <div class="label">审批意见</div>
        </div>
      </van-step>

      <van-step>
        <div class="step-title">
          <span class="title-text">审批节点2</span>
          <span class="step-time">06-04 09:16</span>
        </div>
        <div class="step-detail">
          <div class="applicant">lisi</div>
          <div class="label">审批意见</div>
        </div>
      </van-step>
      
      <van-step>
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
      
      <van-step>
        <div class="step-title">审批结果</div>
        <div class="step-detail">
          <div class="status">审批中</div>
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
  }
}
</script>

<style>
#app {
}

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

.status {
  color: #ff976a;
  font-size: 13px;
}
</style>

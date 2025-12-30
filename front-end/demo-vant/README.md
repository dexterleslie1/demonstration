## 概念

>官网地址：https://vant-ui.github.io/vant/v2/#/zh-CN/home

Vant 是一个**轻量、可靠的移动端组件库**，于 2017 年开源。

目前 Vant 官方提供了 [Vue 2 版本](https://vant-contrib.gitee.io/vant/v2)、[Vue 3 版本](https://vant-contrib.gitee.io/vant)和[微信小程序版本](http://vant-contrib.gitee.io/vant-weapp)，并由社区团队维护 [React 版本](https://github.com/3lang3/react-vant)和[支付宝小程序版本](https://github.com/ant-move/Vant-Aliapp)。

## 和Vue2项目集成

>说明：Vant2只能和Vue2集成。
>
>详细指引请参考官方链接：https://vant-ui.github.io/vant/v2/#/zh-CN/quickstart
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-vant/demo-getting-started

创建Vue2项目

安装Vant2依赖

```sh
# Vue 2 项目，安装 Vant 2：
npm i vant@latest-v2 -S
```

Vant 支持一次性导入所有组件，引入所有组件会增加代码包体积，因此不推荐这种做法。

```javascript
import Vue from 'vue';
import Vant from 'vant';
import 'vant/lib/index.css';

Vue.use(Vant);
```

App.vue如下：

```vue
<template>
  <div id="app">
    <van-button type="primary">主要按钮</van-button>
    <van-button type="info">信息按钮</van-button>
    <van-button type="default">默认按钮</van-button>
    <van-button type="warning">警告按钮</van-button>
    <van-button type="danger">危险按钮</van-button>
  </div>
</template>

<script>

export default {
  name: 'App',
  components: {
  }
}
</script>

<style>
#app {
}
</style>

```

启动项目测试

```sh
npm run serve
```

## Steps步骤条

>官方文档：https://vant-ui.github.io/vant/v2/#/zh-CN/steps
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-vant/demo-step

### 竖向步骤条

说明：可以通过设置 `direction` 属性来改变步骤条的显示方向。

App.vue

```vue
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

```

